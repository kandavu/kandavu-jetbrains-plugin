package dev.kandavu.intellij

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.Disposable
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.Configurable.NoScroll
import com.intellij.util.ui.FormBuilder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.IOException
import javax.swing.*

class Login(
    val username: String,
    val password: String,
) {
    override fun toString(): String {
        return "Login [username: ${this.username}]"
    }
}

class ConfigPersistentStateConfigurable : Configurable, NoScroll, Disposable {
    // this is the persistent component we can read or write to
    private val state
        get() = ConfigPersistentStateComponent.instance.state

    // ui components
    private var usernameField: JTextField? = JTextField()
    private var passwordField: JPasswordField? = JPasswordField()

    val gson = Gson()

    private var loginButton: JButton? = JButton("Login").apply {
        actionCommand = "Login"
        addActionListener(LoginClickListener())
    }

    private var logoutButton: JButton? = JButton("Logout").apply {
        actionCommand = "Logout"
        addActionListener(LogoutClickListener())
    }

    companion object {
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }

    private inner class LoginClickListener : ActionListener {
        override fun actionPerformed(e: ActionEvent) {
            val password: String? = passwordField?.password?.concatToString()

            val client = OkHttpClient()

            val login = password?.let { usernameField?.text?.let { it1 -> Login(it1, it) } }

            val loginJson: String = gson.toJson(login)

            val request = Request.Builder()
                .url("${state.API_URL}/accounts/login")
                .post(loginJson.toRequestBody(MEDIA_TYPE_JSON))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val mapType = object : TypeToken<Map<String, Any>>() {}.type

                var authorizationMap: Map<String, Any> = gson.fromJson(
                    response.body!!.string(),
                    object : TypeToken<Map<String, Any>>() {}.type
                )

                val authorizationKey: String = authorizationMap.get("authorizationKey") as String
                val username: String = authorizationMap.get("username") as String

                state.authorizationKey = authorizationKey
                state.username = username

            }
        }
    }

    private inner class LogoutClickListener : ActionListener {
        override fun actionPerformed(e: ActionEvent) {
            val client = OkHttpClient()

            if(state.authorizationKey != null) {
                val request = Request.Builder()
                    .url("${state.API_URL}/accounts/logout")
                    .header("Authorization", state.authorizationKey as String)
                    .post("".toRequestBody())
                    .build()

                client.newCall(request).execute().use { response ->
                    if(!response.isSuccessful) println("Unexpected code when attempting to logout $response")

                    state.authorizationKey = null
                    state.username = null
                }
            }

            state.authorizationKey = null
            state.username = null
        }
    }


    override fun getPreferredFocusedComponent(): JComponent? {
        return usernameField
    }

    // providing a title
    override fun getDisplayName(): String = "Kandavu Plugin Configuration"

    // creating the ui
    override fun createComponent(): JComponent? {

        println(state)
        if(state.authorizationKey !== null && state.username !== null) {
            println("state.authorizationKey ${state.authorizationKey} or state.username ${state.username} is equal to null")
            val logoutPanel = JPanel(FlowLayout(FlowLayout.CENTER)).also {
                it.add(logoutButton)
            }
            return JPanel(BorderLayout()).also {
                it.add(logoutPanel, BorderLayout.NORTH)
            }
        } else {
            val formPanel = FormBuilder.createFormBuilder()
                // show toolbar button checkbox
                .addLabeledComponent("Username: ", JPanel(FlowLayout(FlowLayout.CENTER)).also {
                    usernameField?.preferredSize = Dimension(300, 30)
                    it.add(usernameField)
                })
                .addLabeledComponent("Password: ", JPanel(FlowLayout(FlowLayout.CENTER)).also {
                    passwordField?.preferredSize = Dimension(300, 30)
                    it.add(passwordField)
                })
                .addComponent(JPanel(FlowLayout(FlowLayout.CENTER)).also {
                    it.add(loginButton)
                })
                .panel

            return JPanel(BorderLayout()).also { it.add(formPanel, BorderLayout.NORTH) }
        }
    }


    override fun dispose() {
        usernameField = null
        passwordField = null
        loginButton = null
        logoutButton = null
    }

    // this tells the preferences window whether to enable or disable the "Apply" button.
    // so if the user has changed anything - we want to know.
    override fun isModified(): Boolean {

        return false
//        return configState.reminderHour != hourField!!.text.toIntOrNull()
//                || configState.reminderMinutes != minutesField!!.text.toIntOrNull()
//                || configState.showToolbarIcon != showToolbarCheckbox!!.isSelected
//                || configState.showReminders != showRemindersCheckbox!!.isSelected
    }

    // when the user hits "ok" or "apply" we want o update the configurable state
    override fun apply() {
//        hourField!!.text.toIntOrNull()?.let {
//            if (it in 0..23)
//                configState.reminderHour = it
//
//        }
//
//        minutesField!!.text.toIntOrNull()?.let {
//            if (it in 0..59)
//                configState.reminderMinutes = it
//        }
//        configState.showToolbarIcon = showToolbarCheckbox!!.isSelected
//        configState.showReminders = showRemindersCheckbox!!.isSelected
    }

    // hitting "reset" shold reset the ui to the latest saved config
    override fun reset() {
        usernameField?.text = ""
        passwordField?.text = ""
    }
}
