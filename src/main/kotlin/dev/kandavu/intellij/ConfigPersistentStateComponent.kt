package dev.kandavu.intellij

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

// the @State annotation helps IntelliJ automatically serialize and save our state
@State(
    name = "ConfigPersistentStateComponent",
    storages = [Storage("kandavu-plugin.xml")]
)
open class ConfigPersistentStateComponent  : PersistentStateComponent<ConfigPersistentStateComponent.State> {
    // this is how we're going to call the component from different classes
    companion object {
        val instance: ConfigPersistentStateComponent
            get() = ServiceManager.getService(ConfigPersistentStateComponent::class.java)
    }

    // the component will always keep our state as a variable
    private var state = State()

    // the POKO class that always keeps our state
    data class State(
        var accessToken: String? = null,
        var username: String? = null,
        var name: String? = null,
        var email: String? = null,
        var API_URL: String? = "https://api.kandavu.dev"
    )

    // just an obligatory override from PersistentStateComponent
    override fun getState(): State {
        return state
    }

    override fun loadState(stateLoadedFromPersistence: State) {
        state = stateLoadedFromPersistence
    }
}