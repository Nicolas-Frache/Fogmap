package ca.uqac.fogmap.data.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class LoggedState(
    val isLogged: Boolean = false,
    val username: String = "",
    val userID: String = "",
)

class LoggedAccountViewModel : ViewModel(){
    var loggedState = mutableStateOf(LoggedState())
        private set

    fun logOut(){
        loggedState.value = LoggedState()
    }

    fun logIn(){
        loggedState.value = LoggedState(true, "toto", "ID")
    }

}

