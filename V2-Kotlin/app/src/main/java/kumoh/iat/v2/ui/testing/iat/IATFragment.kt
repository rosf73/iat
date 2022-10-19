package kumoh.iat.v2.ui.testing.iat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import kumoh.iat.v2.data.dto.QuestionDto
import kumoh.iat.v2.databinding.FragmentIatBinding
import kumoh.iat.v2.ui.testing.TestingViewModel
import kumoh.iat.v2.util.visibility

private const val NAVIGATE_EXPLAIN = "NAVIGATE_EXPLAIN"
private const val NAVIGATE_PRECAUTION = "NAVIGATE_PRECAUTION"

class IATFragment : Fragment() {

    private var _binding: FragmentIatBinding? = null
    private val binding: FragmentIatBinding get() = requireNotNull(_binding)

    private val viewModel: TestingViewModel by activityViewModels()

    private lateinit var navigateToExplanation: () -> Unit
    private lateinit var navigateToPrecautions: () -> Unit

    private var count = 0
    private var trial = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIatBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            navigateToExplanation = it.getSerializable(NAVIGATE_EXPLAIN) as () -> Unit
            navigateToPrecautions = it.getSerializable(NAVIGATE_PRECAUTION) as () -> Unit
            initView()
        }
    }

    private fun initView() {
        binding.apply {
            viewModel.getNowIATStep()?.let { step ->
                tvLeftTitle.text = step.leftTitle
                tvRightTitle.text = step.rightTitle

                trial = step.trial
            }

            tvWord.text = viewModel.getRandomWord()

            btnLeft.setOnClickListener {
                setView(viewModel.isLeftWord(tvWord.text.toString()))
            }
            btnRight.setOnClickListener {
                setView(viewModel.isRightWord(tvWord.text.toString()))
            }
        }
    }

    private fun setView(isCorrect: Boolean) {
        binding.apply {
            if (isCorrect) {
                count++
                if (count >= trial) {
                    val stepSize = viewModel.getNowIATContent()!!.steps.size
                    if (viewModel.nextIATStep() >= stepSize) {
                        viewModel.nextIATContent()
                        navigateToPrecautions()
                    } else {
                        navigateToExplanation()
                    }
                } else {
                    tvWord.text = viewModel.getRandomWord()
                }
                ivFail.visibility = false.visibility
            } else {
                ivFail.visibility = true.visibility
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            navigateToExplanation: () -> Unit,
            navigateToPrecautions: () -> Unit
        ): Fragment = IATFragment().apply {
            arguments = Bundle().apply {
                putSerializable(NAVIGATE_EXPLAIN, navigateToExplanation as java.io.Serializable)
                putSerializable(NAVIGATE_PRECAUTION, navigateToPrecautions as java.io.Serializable)
            }
        }
    }
}