package com.example.grocery.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.domain.repositories.NewsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NewsSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val newsRepository: NewsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Fetch latest news from API and update Room
//            newsRepository.getTopHeadlines("us")
//                .collect { /* Already updates Room in offline-first repo */ }
            Log.d("NewsSyncWorker", "News sync completed")

            Result.success()
        } catch (e: Exception) {
//            Log.d("NewsSyncWorker", e.message.toString())
            Result.retry()
        }
    }
}