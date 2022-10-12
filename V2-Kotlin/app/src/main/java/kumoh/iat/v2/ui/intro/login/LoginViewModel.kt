package kumoh.iat.v2.ui.intro.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kumoh.iat.v2.R
import kumoh.iat.v2.data.repository.ParticipantRepository
import kumoh.iat.v2.util.WhileUiSubscribed
import javax.inject.Inject

/**
 * UiState for the login screen
 */
data class UiState(
    val isLoading: Boolean = false,
    val toastMessage: Int? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val participantRepository: ParticipantRepository
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _toastMessage: MutableStateFlow<Int?> = MutableStateFlow(null)

    val uiState: StateFlow<UiState> = combine(
        _isLoading, _toastMessage
    ) { isLoading, toastMessage ->
        UiState(
            isLoading = isLoading,
            toastMessage = toastMessage
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = UiState()
        )

    var phoneNum = ""
    var gender: Int = 1
    var grade: Int = -1
    var major: Int = -1

    fun login(
        block: (String) -> Unit
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            participantRepository.login(phoneNum)
                .onSuccess { msg ->
                    block(msg)
                }
                .onFailure {
                    _toastMessage.value = R.string.msg_server_error
                }
            _isLoading.value = false
        }
    }

    fun signUp(
        age: Int,
        block: (String) -> Unit
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            participantRepository.signUp(phoneNum, age, gender, grade, major)
                .onSuccess { msg ->
                    block(msg)
                }
                .onFailure {
                    _toastMessage.value = R.string.msg_server_error
                }
            _isLoading.value = false
        }
    }

    fun toastMessageShown() {
        _toastMessage.value = null
    }
}