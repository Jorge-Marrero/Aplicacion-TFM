package es.tfm.clientetfm.util.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.Message
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import es.tfm.clientetfm.MainActivity
import es.tfm.clientetfm.R

class NotificationHandler(base: Context?) : ContextWrapper(base) {

    private val GROUP_NAME = "GROUP"
    private val GROUP_ID = 111
    private val CHANNEL_ID = "1"
    private val CHANNEL_ID_NAME = "MENSAJE_SERVIDOR"
    lateinit var manager : NotificationManager


    init {
        crearCanales()
    }

    fun getManagerSingle() : NotificationManager{
        return try {
            manager
        } catch (e: UninitializedPropertyAccessException) {
            manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager
        }
    }

    private fun crearCanales() {
        val nc = NotificationChannel(CHANNEL_ID, CHANNEL_ID_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        nc.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        getManagerSingle().createNotificationChannel(nc)
    }

    fun createNotification(title: String, message: String) : Notification.Builder{
        return crearNotificationChannels(title, message, CHANNEL_ID)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun crearNotificationChannels(title : String, msg: String, channel: String) :Notification.Builder {
        val intent = Intent(baseContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent : PendingIntent = PendingIntent.getActivity(
            baseContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Builder(applicationContext, channel)
            .setContentTitle(title)
            .setContentText(msg)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setGroup(GROUP_NAME)
    }

    fun publishGroup() {
        val group : Notification.Builder = Notification.Builder(applicationContext, CHANNEL_ID)
            .setGroup(GROUP_NAME)
            .setGroupSummary(true)
            .setSmallIcon(R.drawable.ic_launcher_background)
        getManagerSingle().notify(GROUP_ID, group.notification)
    }

}