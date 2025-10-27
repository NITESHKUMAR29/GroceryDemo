package com.example.grocery.uis.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.domain.models.Category
import com.example.domain.models.Product
import com.example.grocery.R
import com.example.grocery.databinding.ActivityAddProductBinding
import com.example.grocery.viewModels.ProductListViewModel
import com.example.grocery.states.UiState
import com.example.grocery.utility.CategoryIds
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val viewModel: ProductListViewModel by viewModels()
    private var categoryList: List<Category> = emptyList()
    private var selectedImageUri: Uri? = null
    private var uploadedImageUrl: String? = null

    private var selectedCategoryId: Int? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val uri: Uri? = result.data!!.data
            uri?.let {
                binding.imagePreview.setImageURI(uri)
                selectedImageUri = it
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_product)
        viewModel.getCategories()
        observeCategories()

        binding.btnUploadImage.setOnClickListener { openGallery() }
        binding.btnCreateProduct.setOnClickListener { createProduct() }
        observeUploadState()
        observeCreateProductState()
    }

    private fun observeCategories() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productCategoryState.collect { state ->
                    when (state) {
                        is UiState.Success -> {

                            categoryList = state.data.filter { it.name in listOf("Grocery", "Stationary", "Sweets") }

                            Log.d("CategoryStateProductAddActivity",CategoryIds.GROCERY.toString())
                            setupCategorySpinner(categoryList)

                            categoryList.forEach { category ->
                                when (category.name) {
                                    "Grocery" -> CategoryIds.GROCERY = category.id
                                    "Stationary" -> CategoryIds.STATIONARY = category.id
                                    "Sweets" -> CategoryIds.SWEETS = category.id
                                }
                            }
                        }
                        is UiState.Error -> {
                            Toast.makeText(this@AddProductActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                        UiState.Loading -> {}
                        UiState.Idle -> {}
                    }
                }
            }
        }
    }

    private fun setupCategorySpinner(categories: List<Category>) {
        val categoryNames = categories.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

        if (categories.isNotEmpty()) {
            binding.spinnerCategory.setSelection(0)
            selectedCategoryId = categories[0].id
        }

        binding.spinnerCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCategoryId = categories[position].id
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    selectedCategoryId = null
                }
            }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun createProduct() {
        val title = binding.etTitle.text.toString().trim()
        val priceText = binding.etPrice.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (title.isEmpty() || priceText.isEmpty() || description.isEmpty() || selectedCategoryId == null) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceText.toIntOrNull()
        if (price == null) {
            Toast.makeText(this, "Invalid price input", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        uploadImage(selectedImageUri!!)
    }

    private fun uploadImage(uri: Uri) {
        lifecycleScope.launch {
            try {
                val originalFile = uriToFile(uri)
                val compressedFile = Compressor.compress(this@AddProductActivity, originalFile)
                viewModel.uploadImage(compressedFile)
                binding.progressBar.isVisible = true
            } catch (e: Exception) {
                binding.progressBar.isVisible = false
                Toast.makeText(this@AddProductActivity, "Image processing failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open URI")
        val file = File(cacheDir, "upload_image_${System.currentTimeMillis()}.jpg")
        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun observeUploadState() {
        lifecycleScope.launch {
            viewModel.uploadImageState.collectLatest { state ->
                Log.d("uploadImageState", state.toString())
                when (state) {
                    is UiState.Loading -> binding.progressBar.isVisible = true
                    is UiState.Success -> {
                        binding.progressBar.isVisible = false
                        uploadedImageUrl = state.data
                        submitProduct()
                    }
                    is UiState.Error -> {
                        Toast.makeText(this@AddProductActivity, state.message, Toast.LENGTH_SHORT).show()
                        binding.progressBar.isVisible = false
                        Toast.makeText(this@AddProductActivity, state.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun submitProduct() {
        val title = binding.etTitle.text.toString().trim()
        val price = binding.etPrice.text.toString().trim().toInt()
        val description = binding.etDescription.text.toString().trim()
        val categoryId = selectedCategoryId!!

        val product = Product(
            id = 0,
            slug = "",
            title = title,
            price = price,
            description = description,
            category = Category(categoryId, "", ),
            images = listOf(uploadedImageUrl ?: ""),
            creationAt = "",
            updatedAt = ""
        )

        Log.d("submitProduct", product.toString())
        viewModel.createProduct(product)
    }

    private fun observeCreateProductState() {
        lifecycleScope.launch {
            viewModel.createProductState.collectLatest { state ->
                Log.d("createProductState", state.toString())
                when (state) {
                    is UiState.Loading -> binding.progressBar.isVisible = true
                    is UiState.Success -> {
                        binding.progressBar.isVisible = false
                        Toast.makeText(this@AddProductActivity, "Product created successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is UiState.Error -> {
                        Toast.makeText(this@AddProductActivity, state.message, Toast.LENGTH_SHORT).show()
                        binding.progressBar.isVisible = false
                        Toast.makeText(this@AddProductActivity, state.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }
}
