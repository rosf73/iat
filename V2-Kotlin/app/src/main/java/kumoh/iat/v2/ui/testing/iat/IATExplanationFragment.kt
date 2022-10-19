package kumoh.iat.v2.ui.testing.iat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kumoh.iat.v2.R
import kumoh.iat.v2.databinding.FragmentIatExplanationBinding
import kumoh.iat.v2.ui.testing.TestingViewModel
import kumoh.iat.v2.util.visibility

private const val NAVIGATE_IAT = "NAVIGATE_IAT"
private const val BACK_SURVEY = "BACK_SURVEY"

class IATExplanationFragment : Fragment() {

    private var _binding: FragmentIatExplanationBinding? = null
    private val binding: FragmentIatExplanationBinding get() = requireNotNull(_binding)

    private val viewModel: TestingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIatExplanationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            if (viewModel.isIATDone()) {
                initFinishView(it.getSerializable(BACK_SURVEY) as () -> Unit)
            } else {
                initView(it.getSerializable(NAVIGATE_IAT) as () -> Unit)
            }
        }
    }

    private fun initView(
        navigateToIAT: () -> Unit
    ) {
        binding.apply {
            viewModel.getNowIATStep()?.let { step ->
                tvLeftTitle.text = step.leftTitle
                tvRightTitle.text = step.rightTitle

                tvLeftWords.text = step.leftWords.joinToString(", ")
                tvRightWords.text = step.rightWords.joinToString(", ")

                tvInfo.text = String.format(
                    requireContext().getString(R.string.iat_info), step.leftTitle, step.rightTitle
                )
            }

            btnStart.setOnClickListener {
                navigateToIAT()
            }
        }
    }

    private fun initFinishView(
        backToSurvey: () -> Unit
    ) {
        binding.apply {
            llNotice.visibility = false.visibility
            tvInfo.visibility = false.visibility
            btnStart.visibility = false.visibility

            tvFinish.visibility = true.visibility
            btnFinish.visibility = true.visibility

            btnFinish.setOnClickListener {
                viewModel.setIATDone()
                backToSurvey()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            navigateToIAT: () -> Unit,
            backToSurvey: () -> Unit
        ): Fragment = IATExplanationFragment().apply {
            arguments = Bundle().apply {
                putSerializable(NAVIGATE_IAT, navigateToIAT as java.io.Serializable)
                putSerializable(BACK_SURVEY, backToSurvey as java.io.Serializable)
            }
        }
    }
}