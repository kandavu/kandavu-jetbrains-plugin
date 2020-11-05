package dev.kandavu.intellij

import com.intellij.openapi.Disposable
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.Configurable.NoScroll
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.text.NumberFormat
import java.util.*
import javax.swing.*
import javax.swing.text.NumberFormatter

class ConfigPersistentStateConfigurable : Configurable, NoScroll, Disposable {
    // configurations for the text fields
    private val hourFormatter = NumberFormatter(NumberFormat.getIntegerInstance()).also {
        it.minimum = 0
        it.maximum = 23
        it.allowsInvalid = true
    }

    private val minutesFormatter = NumberFormatter(NumberFormat.getIntegerInstance()).also {
        it.minimum = 0
        it.maximum = 59
        it.allowsInvalid = true
    }

    // this is the persistent component we can read or write to
    private val configState
        get() = ConfigPersistentStateComponent.instance.state

    // ui components
    private var showToolbarCheckbox: JCheckBox? = JCheckBox("Show Toolbar Button")
    private var showRemindersCheckbox: JCheckBox? = JCheckBox("Show Reminder Notifications")

    private var usernameField: JTextField? = JTextField()
    private var passwordField: JPasswordField? = JPasswordField()

    private var hourField: JFormattedTextField? =
        JFormattedTextField(hourFormatter).also { it.text = "" }

    private var minutesField: JTextField? =
        JFormattedTextField(minutesFormatter).also { it.text = "" }

    private var loginButton = JButton("Login").apply {
        actionCommand = "Login"
        addActionListener(LoginClickListener())
    }

    private inner class LoginClickListener : ActionListener {
        override fun actionPerformed(e: ActionEvent) {
//            val passwordData = Arrays.toString(passwordField?.password)

            val password: String? = passwordField?.password?.concatToString()
            System.out.println("username: ${usernameField?.text}")
            System.out.println("password: $password")
            System.out.println("Login clicked")
        }
    }

    // providing a title
    override fun getDisplayName(): String = "Kandavu Plugin Configuration"

    // creating the ui
    override fun createComponent(): JComponent? {

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


    override fun dispose() {
        hourField = null
        minutesField = null
        showToolbarCheckbox = null
        showRemindersCheckbox = null
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
//        hourField!!.text = configState.reminderHour.toString()
//        minutesField!!.text = configState.reminderMinutes.toString()
//        showToolbarCheckbox!!.isSelected = configState.showToolbarIcon
//        showRemindersCheckbox!!.isSelected = configState.showReminders
    }
}