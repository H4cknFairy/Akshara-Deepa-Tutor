package com.example.akshara_deepatutor.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.akshara_deepatutor.MainActivity
import com.example.akshara_deepatutor.R
import java.util.*
import java.util.concurrent.TimeUnit

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "daily_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Study Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminds you to continue your learning journey"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 
            0, 
            intent, 
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Using default if my_logo is only a drawable
            .setContentTitle("Akshara-Deepa Tutor")
            .setContentText("Time to continue your learning! 📚 Keep the light of knowledge burning.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    companion object {
        fun scheduleReminder(context: Context, hour: Int = 18, minute: Int = 0) {
            val calendar = Calendar.getInstance()
            val now = calendar.timeInMillis
            
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            
            if (calendar.timeInMillis <= now) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            
            val delay = calendar.timeInMillis - now

            val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("daily_reminder")
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "daily_reminder_work",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }

        fun sendTestNotification(context: Context) {
            val testRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .addTag("test_reminder")
                .build()
            WorkManager.getInstance(context).enqueue(testRequest)
        }

        fun cancelReminder(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag("daily_reminder")
        }
    }
}
