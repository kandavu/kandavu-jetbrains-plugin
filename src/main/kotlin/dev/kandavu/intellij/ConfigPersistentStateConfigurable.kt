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
import java.awt.*
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class ConfigPersistentStateConfigurable : Configurable, NoScroll, Disposable {

    private val state
        get() = ConfigPersistentStateComponent.instance.state


    private var accessTokenField: JTextField? = JTextField()
    private var statusLabel: JLabel? = JLabel()

    val gson = Gson()

    companion object {
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return accessTokenField
    }

    // providing a title
    override fun getDisplayName(): String = "Kandavu Plugin Configuration"


    // creating the ui
    override fun createComponent(): JComponent? {

        val formPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Access Token: ", JPanel(FlowLayout(FlowLayout.CENTER)).also {
                accessTokenField?.preferredSize = Dimension(300, 30)

                accessTokenField?.document?.addDocumentListener(object : DocumentListener {
                    override fun changedUpdate(e: DocumentEvent) {
                        if(state.accessToken != accessTokenField!!.text) {
                            statusLabel?.text = ""
                        } else {
                            updateStatusLabelWithState()
                        }
                    }

                    override fun insertUpdate(e: DocumentEvent?) {
                        if(state.accessToken != accessTokenField!!.text) {
                            statusLabel?.text = ""
                        } else {
                            updateStatusLabelWithState()
                        }
                    }

                    override fun removeUpdate(e: DocumentEvent?) {
                        if(state.accessToken != accessTokenField!!.text) {
                            statusLabel?.text = ""
                        } else {
                            updateStatusLabelWithState()
                        }
                    }
                })

                it.add(accessTokenField)
            })
            .panel

        return JPanel(BorderLayout()).also {
                it.add(formPanel, BorderLayout.NORTH)
                it.add(JPanel(FlowLayout()).also {
                    it.add(statusLabel)
                }, BorderLayout.CENTER)
            }
    }


    override fun dispose() {
        accessTokenField = null
    }

    override fun isModified(): Boolean {
        return state.accessToken != accessTokenField!!.text
    }

    override fun apply() {
        state.accessToken = accessTokenField!!.text


        val client = OkHttpClient()

        statusLabel?.text = "Logging in..."

        if(state.accessToken?.isNotEmpty()!!) {
            val request = state.accessToken?.let {
                Request.Builder()
                    .url("${state.API_URL}/accounts/me")
                    .header("Authorization", it)
                    .get()
                    .build()
            }

            if (request != null) {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        println("Unexpected code $response")
                        statusLabel?.text = "Login failed"
                    } else {
                        val authorizationMap: Map<String, Any> = gson.fromJson(
                            response.body!!.string(),
                            object : TypeToken<Map<String, Any>>() {}.type
                        )

                        state.username = authorizationMap.get("username") as String?
                        state.email = authorizationMap.get("email") as String?
                        state.name = "${authorizationMap.get("firstName")} ${authorizationMap.get("lastName")}"

                        updateStatusLabelWithState()
                    }
                }
            }
        } else {
            // they've effectively logged out by clearing the accessToken
            state.accessToken = null
            state.username = null
            state.email = null
            state.name = null
            statusLabel?.text = "Not logged in."
        }
    }

    fun updateStatusLabelWithState() {
        statusLabel?.text = "<html><b>${state.username}</b> logged in with email: <b>${state.email}</b></html>"
    }

    override fun reset() {
        accessTokenField?.text = state.accessToken

        if(state.username?.isNotEmpty()!! && state.email?.isNotEmpty()!! && state.name?.isNotEmpty()!!) {
            updateStatusLabelWithState()
        }
    }
}
