package kumoh.iat.v2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kumoh.iat.v2.R
import kumoh.iat.v2.data.dto.ProjectInfoDto
import kumoh.iat.v2.data.repository.ProjectRepository
import kumoh.iat.v2.util.WhileUiSubscribed
import java.net.ConnectException
import javax.inject.Inject

/**
 * UiState for the project information screen
 */
data class UiState(
    val projectInfo: ProjectInfoDto? = null,
    val isLoading: Boolean = true,
    val toastMessage: Int? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
): ViewModel() {

    private val _projectInfo: MutableStateFlow<ProjectInfoDto?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(true)
    private val _toastMessage: MutableStateFlow<Int?> = MutableStateFlow(null)

    val uiState: StateFlow<UiState> = combine(
        _projectInfo, _isLoading, _toastMessage
    ) { projectInfo, isLoading, toastMessage ->
        UiState(
            projectInfo = projectInfo,
            isLoading = isLoading,
            toastMessage = toastMessage
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = UiState()
        )

    init {
        viewModelScope.launch {
            projectRepository.getProjectInfo()
                .onSuccess {
                    _projectInfo.value = it
                }
                .onFailure {
                    when (it) {
                        is ConnectException -> showToastMessage(R.string.msg_server_error)
                        else -> showToastMessage(R.string.msg_network_error)
                    }
                }
            _isLoading.value = false
        }
    }

    fun toastMessageShown() {
        _toastMessage.value = null
    }

    private fun showToastMessage(msg: Int) {
        _toastMessage.value = msg
    }
}