package com.example.leadreminder

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AppExecutor {
    companion object{
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val mainThread: Handler = Handler(Looper.getMainLooper())

    }
}