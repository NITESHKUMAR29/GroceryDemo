package com.example.grocery.newsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocery.states.UiState
import com.example.domain.models.News
import com.example.domain.useCases.GetTopHeadlinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase) :
    ViewModel() {
    private val _newsState = MutableStateFlow<UiState<List<News>>>(UiState.Loading)
    val newsState: StateFlow<UiState<List<News>>> = _newsState


    fun getTopHeadLInes(country: String) {
        viewModelScope.launch {
            getTopHeadlinesUseCase(country)
                .onStart { _newsState.value = UiState.Loading }
                .catch { _newsState.value = UiState.Error(it.message.toString()) }
                .collect {
                    _newsState.value = UiState.Success(it)
                }
        }
    }


}