package kumoh.iat.v2.ui.testing.survey

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kumoh.iat.v2.R
import kumoh.iat.v2.data.dto.QuestionDto
import kumoh.iat.v2.databinding.FragmentSurveyBinding
import kumoh.iat.v2.ui.customadapter.CustomListedAdapter
import kumoh.iat.v2.ui.customadapter.CustomNormalCaseAdapter
import kumoh.iat.v2.ui.customadapter.CustomTableCaseAdapter
import kumoh.iat.v2.ui.customadapter.CustomTableAdapter
import kumoh.iat.v2.ui.testing.TestingViewModel
import kumoh.iat.v2.util.visibility

private const val NAVIGATE_IAT = "NAVIGATE_IAT"
private const val NAVIGATE_FINISH = "NAVIGATE_FINISH"

@AndroidEntryPoint
class SurveyFragment : Fragment() {

    private var _binding: FragmentSurveyBinding? = null
    private val binding: FragmentSurveyBinding get() = requireNotNull(_binding)

    private val viewModel: TestingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSurveyBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            initView(
                it.getSerializable(NAVIGATE_IAT) as () -> Unit,
                it.getSerializable(NAVIGATE_FINISH) as () -> Unit
            )
            observeData()
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    if (state.toastMessage != null) {
                        Toast.makeText(requireContext(), state.toastMessage, Toast.LENGTH_LONG).show()
                        viewModel.toastMessageShown()
                    }

                    binding.pbSurvey.visibility = state.isLoading.visibility
                    binding.btnIat.isEnabled = !state.iatDone
                    if (!state.isLoading) {
                        setAllBlind()
                        setButtons()
                        viewModel.getNowQuestion()?.let {
                            initQuestion(it)
                        }
                    }
                }
            }
        }
    }

    private fun setButtons() {
        binding.btnPrev.isEnabled = !viewModel.isFirst()
        binding.btnNext.isEnabled = !viewModel.isLast()
    }

    private fun initView(
        navigateToIAT: () -> Unit,
        navigateToFinish: () -> Unit
    ) {
        binding.apply {
            btnPrev.setOnClickListener {
                viewModel.prev()
            }
            btnNext.setOnClickListener {
                viewModel.next()
            }
            btnIat.setOnClickListener {
                navigateToIAT()
            }
        }
    }

    private fun setAllBlind() {
        binding.apply {
            ctvTitle.visibility = false.visibility
            svSurvey.visibility = false.visibility
            ctvTable.visibility = false.visibility
            cllNormal.visibility = false.visibility
            cllListed.visibility = false.visibility
            btnIat.visibility = false.visibility
            etAssay.visibility = false.visibility
        }
    }

    private fun initQuestion(
        question: QuestionDto
    ) {
        binding.svSurvey.scrollTo(0, 0)
        when (question) {
            is QuestionDto.QuestionNormalDto -> initNormalQuestion(question)
            is QuestionDto.QuestionTableDto -> initTableQuestion(question)
            is QuestionDto.QuestionListedDto -> initListedQuestion(question)
            is QuestionDto.QuestionIATDto -> initIATQuestion(question)
            is QuestionDto.QuestionAssayDto -> initAssayQuestion(question)
        }
    }

    private fun initNormalQuestion(
        question: QuestionDto.QuestionNormalDto
    ) {
        binding.apply {
            svSurvey.visibility = true.visibility
            cllNormal.visibility = true.visibility

            tvNumber.text = question.number.toString()
            tvQuestion.text = question.question

            cllNormal.setAdapter(
                CustomNormalCaseAdapter(
                    context = requireContext(),
                    resource = R.layout.item_normal_case,
                    cases = question.cases,
                    isAssay = question.isAssay
                )
            )
        }
    }

    private fun initTableQuestion(
        question: QuestionDto.QuestionTableDto
    ) {
        binding.apply {
            ctvTitle.visibility = true.visibility
            svSurvey.visibility = true.visibility
            ctvTable.visibility = true.visibility

            tvNumber.text = question.number.toString()
            tvQuestion.text = question.question

            ctvTitle.removeAllViews()
            ctrFirst.removeAllViews()
            ctrFirst.addView(tvFirst)
            ctrFirst.setAdapter(
                CustomTableCaseAdapter(
                    context = requireContext(),
                    resource = R.layout.item_table_case,
                    cases = question.cases,
                    block = {}
                )
            )
            for (i in 1 until ctrFirst.childCount) {
                ctrFirst.getChildAt(i).layoutParams = TableRow.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
                )
            }
            ctvTitle.addView(ctrFirst)

            ctvTable.removeAllViews()
            ctvTable.setAdapter(
                CustomTableAdapter(
                    context = requireContext(),
                    resource = R.layout.item_sub_content,
                    questions = question.subContents,
                    caseCount = question.cases.size
                )
            )
        }
    }

    private fun initListedQuestion(
        question: QuestionDto.QuestionListedDto
    ) {
        binding.apply {
            svSurvey.visibility = true.visibility
            cllListed.visibility = true.visibility

            tvNumber.text = question.number.toString()
            tvQuestion.text = question.question

            cllListed.setAdapter(
                CustomListedAdapter(
                    context = requireContext(),
                    resource = R.layout.item_sub_content,
                    questions = question.subContents.map { it.content },
                    cases = question.subContents.map { it.cases }
                )
            )
        }
    }

    private fun initIATQuestion(
        question: QuestionDto.QuestionIATDto
    ) {
        binding.apply {
            btnIat.visibility = true.visibility

            tvNumber.text = question.number.toString()
            tvQuestion.text = question.question
        }
    }

    private fun initAssayQuestion(
        question: QuestionDto.QuestionAssayDto
    ) {
        binding.apply {
            etAssay.visibility = true.visibility

            tvNumber.text = question.number.toString()
            tvQuestion.text = question.question
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            navigateToIAT: () -> Unit,
            navigateToFinish: () -> Unit
        ) = SurveyFragment().apply {
            arguments = Bundle().apply {
                putSerializable(NAVIGATE_IAT, navigateToIAT as java.io.Serializable)
                putSerializable(NAVIGATE_FINISH, navigateToFinish as java.io.Serializable)
            }
        }
    }
}