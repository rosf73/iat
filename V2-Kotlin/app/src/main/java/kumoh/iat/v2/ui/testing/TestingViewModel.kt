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
import kumoh.iat.v2.data.dto.QuestionIATContentDto
import kumoh.iat.v2.data.dto.QuestionIATStepDto
import kumoh.iat.v2.data.repository.QuestionRepository
import kumoh.iat.v2.util.WhileUiSubscribed
import java.net.ConnectException
import java.util.Random
import javax.inject.Inject

/**
 * UiState for the survey & testing screen
 */
data class UiState(
    val questions: List<QuestionDto> = emptyList(),
    val now: Int = 0,
    val iatDone: Boolean = false,
    val isLoading: Boolean = true,
    val toastMessage: Int? = null
)

@HiltViewModel
class TestingViewModel @Inject constructor(
    private val questionRepository: QuestionRepository
): ViewModel() {

    private val _questions = MutableStateFlow(emptyList<QuestionDto>())
    private val _now = MutableStateFlow(0)
    private val _iatDone = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(true)
    private val _toastMessage: MutableStateFlow<Int?> = MutableStateFlow(null)

    val uiState: StateFlow<UiState> = combine(
        _questions, _now, _iatDone, _isLoading, _toastMessage
    ) { questions, now, iatDone, isLoading, toastMessage ->
        UiState(
            questions = questions,
            now = now,
            iatDone = iatDone,
            isLoading = isLoading,
            toastMessage = toastMessage
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = UiState()
        )

    private var _nowContent = 0
    private var _nowStep = 0

    private var _prevRand = -1

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

    fun getNowQuestion(): QuestionDto? {
        return if (_questions.value.size > _now.value) _questions.value[_now.value] else null
    }

    fun getNowIATContent(): QuestionIATContentDto? {
        val nowQ = getNowQuestion()
        return if (nowQ is QuestionDto.QuestionIATDto) nowQ.subContents[_nowContent] else null
    }

    fun getNowIATStep(): QuestionIATStepDto? {
        getNowIATContent()?.let { return it.steps[_nowStep] }
        return null
    }

    fun isIATDone(): Boolean {
        val nowQ = getNowQuestion()
        return if (nowQ is QuestionDto.QuestionIATDto)
            nowQ.subContents.size <= _nowContent
        else
            true
    }

    fun setIATDone() {
        _iatDone.value = true
    }

    fun getRandomWord(): String {
        val stepWords = getNowIATStep()!!.run { leftWords + rightWords }
        val end = stepWords.size - 1
        var rand = _prevRand

        while (rand == _prevRand) {
            rand = Random(System.currentTimeMillis()).nextInt(end)
        }

        return stepWords[rand]
    }

    fun isLeftWord(w: String): Boolean {
        getNowIATStep()?.let { step ->
            return step.leftWords.contains(w)
        }
        return false
    }

    fun isRightWord(w: String): Boolean {
        getNowIATStep()?.let { step ->
            return step.rightWords.contains(w)
        }
        return false
    }

    fun prev() { _now.value-- }

    fun next() { _now.value++ }

    fun isFirst(): Boolean = _now.value == 0

    fun isLast(): Boolean = _now.value == _questions.value.size - 1

    fun nextIATContent(): Int {
        _nowStep = 0
        return ++_nowContent
    }

    fun nextIATStep(): Int = ++_nowStep
}