package dev.kandavu.intellij

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import java.time.LocalDateTime

// the @State annotation helps IntelliJ automatically serialize and save our state
@State(
    name = "ConfigPersistentStateComponent",
    storages = [Storage("kandavu-plugin.xml")]
)
open class ConfigPersistentStateComponent  : PersistentStateComponent<ConfigPersistentStateComponent.ConfigState> {
    // this is how we're going to call the component from different classes
    companion object {
        val instance: ConfigPersistentStateComponent
            get() = ServiceManager.getService(ConfigPersistentStateComponent::class.java)
    }

    // the component will always keep our state as a variable
    var configState: ConfigState = ConfigState()

    // just an obligatory override from PersistentStateComponent
    override fun getState(): ConfigState {
        return configState
    }

    // after automatically loading our save state,  we will keep reference to it
    override fun loadState(state: ConfigState) {
        configState = state
    }

    // the POKO class that always keeps our state
    class ConfigState {
        var authorizationKey = null
    }
}