package dev.kandavu.intellij

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class AddStatusAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        // replace the URL with whatever you like :)
        BrowserUtil.browse("https://kandavu.dev/")
    }

}