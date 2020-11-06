package dev.kandavu.intellij

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType

object StatusNotification {

    private val group = NotificationGroup("Kandavu Plugin", NotificationDisplayType.BALLOON, false)

    private val notificationListener = NotificationListener { notification, _ ->
        // dismiss notification whenever clicked something
        notification.expire()
    }

    fun notifyUser() {
        // create notification
        val notification = group.createNotification("Status", "Status has been added", NotificationType.INFORMATION, notificationListener)

        // ping user, not need for project object
        notification.notify(null)
    }
}