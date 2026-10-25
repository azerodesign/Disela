package com.azerostudio.disela.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.azerostudio.disela.MainActivity

/*
 * All Disela notifications go through here, on one channel.
 * One channel (not one per feature) keeps the settings surface simple for
 * a personal app: the user either wants Disela reminders or does not.
 * Importance HIGH is deliberate: a due reminder that arrives silently in
 * the notification shade has already failed at its one job.
 *
 * Small icon: the platform info icon is used in v1 because Disela has no
 * launcher icon asset yet. Replace setSmallIcon with the Disela icon the
 * moment an icon asset exists — a notification without a recognisable icon
 * is easy to dismiss by accident.
 */
object NotificationHelper {
    const val CHANNEL_ID = "disela_reminders"
    const val EXTRA_OPEN_CAPTURE = "disela_open_capture"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pengingat Disela",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Pengingat tugas dan nudge capture PR Disela."
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun showDueReminder(context: Context, taskId: Long, title: String, categoryLabel: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            taskId.toInt(),
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText("Jatuh tempo hari ini · $categoryLabel")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(context).notify(taskId.toInt(), notification)
    }

    fun showPrNudge(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_OPEN_CAPTURE, true)
            // Pre-select the PR category in the capture sheet.
            putExtra("disela_capture_category", "PR")
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            ReminderScheduler.PR_NUDGE_REQUEST_CODE,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Hari ini ada PR apa?")
            .setContentText("Catat PR hari ini sebelum keburu lupa — cuma butuh 10 detik.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(context).notify(
            ReminderScheduler.PR_NUDGE_REQUEST_CODE,
            notification
        )
    }
}
