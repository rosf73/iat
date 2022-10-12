package kumoh.iat.v2.ui.testing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kumoh.iat.v2.R
import kumoh.iat.v2.data.dto.QuestionDto
import kumoh.iat.v2.data.repository.QuestionRepository
import kumoh.iat.v2.util.WhileUiSubscribed
import java.net.ConnectException
import javax.inject.Inject

/**
 * UiState for the survey & testing screen
 */
data class UiState(
    val questions: List<QuestionDto> = emptyList(),
    val now: Int = 0,
    val isLoading: Boolean = true,
    val toastMessage: Int? = null
)

@HiltViewModel
class TestingViewModel @Inject constructor(
    private val questionRepository: QuestionRepository
): ViewModel() {

    private val _questions = MutableStateFlow(emptyList<QuestionDto>())
    private val _now = MutableStateFlow(0)
    private val _isLoading = MutableStateFlow(true)
    private val _toastMessage: MutableStateFlow<Int?> = MutableStateFlow(null)

    val uiState: StateFlow<UiState> = combine(
        _questions, _now, _isLoading, _toastMessage
    ) { questions, now, isLoading, toastMessage ->
        UiState(
            questions = questions,
            now = now,
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
            questionRepository.getAllQuestions()
                .onSuccess {
                    _questions.value = it
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

    fun getNowQuestion(): QuestionDto?
        = if (_questions.value.size <= _now.value) null else _questions.value[_now.value]

    fun prev() {
        _now.value--
    }

    fun next() {
        _now.value++
    }

    fun isFirst(): Boolean = _now.value == 0

    fun isLast(): Boolean = _now.value == _questions.value.size - 1
}