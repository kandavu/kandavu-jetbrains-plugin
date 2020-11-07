package dev.kandavu.intellij

import com.google.gson.Gson
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.IOException
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.border.Border
import javax.swing.border.EmptyBorder


class AddStatusAction : AnAction() {
    // this is the persistent component we can read or write to
    private val state
        get() = ConfigPersistentStateComponent.instance.state

    private var statusField: JTextField? = JTextField()

    private var statusPopup: JBPopup? = null

    var editor: Editor? = null

    val gson = Gson()

    private var addStatusButton: JButton? = JButton("Add Status").apply {
        actionCommand = "Add Status"
        addActionListener(AddStatusClickListener())
    }

    private var cancelAddStatusButton: JButton? = JButton("Cancel").apply {
        actionCommand = "Cancel Add Status"
        addActionListener(CancelAddStatusClickListener())
    }

    class Status(
        val description: String?,
    ) {
        override fun toString(): String {
            return "Status [description: ${this.description}]"
        }
    }

    private inner class CancelAddStatusClickListener : ActionListener {
        override fun actionPerformed(e: ActionEvent) {
            statusField!!.text = ""
            statusPopup!!.dispose()
        }
    }

    private inner class AddStatusClickListener : ActionListener {
        override fun actionPerformed(e: ActionEvent) {
            val client = OkHttpClient()

            val statusText = statusField?.text as String
            val status = Status(statusText)

            val statusJson: String = gson.toJson(status)

            val request = Request.Builder()
                .url("${state.API_URL}/statuses")
                .header("Authorization", state.authorizationKey as String)
                .post(statusJson.toRequestBody(ConfigPersistentStateConfigurable.MEDIA_TYPE_JSON))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                statusField!!.text = ""
                statusPopup!!.dispose()

                StatusNotification.notifyEvent("Status", "Status has been updated.")
            }
        }
    }

    override fun actionPerformed(event: AnActionEvent) {
        if(state.authorizationKey === null) {
            StatusNotification.notifyEvent("Status", "Login not found, please login in Kandavu preferences", NotificationType.ERROR)

            return
        }

        editor = event.getData(PlatformDataKeys.EDITOR)
        statusField!!.columns = 20

        val margin : Border = EmptyBorder(10, 10, 10, 10)

        val statusTextFieldPanel = JPanel(FlowLayout(FlowLayout.CENTER)).also {
            it.add(statusField)
            it.add(addStatusButton)
            it.add(cancelAddStatusButton)
        }

        statusTextFieldPanel.border = margin

        statusPopup = JBPopupFactory.getInstance().createComponentPopupBuilder(statusTextFieldPanel, statusField)
            .setShowBorder(true)
            .setShowShadow(true)
            .setRequestFocus(true)
            .setCancelOnWindowDeactivation(true)
            .createPopup()

        statusPopup!!.showInFocusCenter()
    }

}