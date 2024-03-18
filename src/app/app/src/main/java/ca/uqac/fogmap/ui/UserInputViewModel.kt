package ca.uqac.fogmap.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class UserInputViewModel : ViewModel() {
    var uiState = mutableStateOf(UserInputScreenState())
}

data class UserInputScreenState(
    var nameEntered : String = "",
    var animatedSelected: String  = ""
)