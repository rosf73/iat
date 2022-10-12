package kumoh.iat.v2.ui.intro.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kumoh.iat.v2.R
import kumoh.iat.v2.databinding.FragmentLoginInfoBinding
import kumoh.iat.v2.util.visibility

private const val NAVIGATE = "NAVIGATE"

class LoginInfoFragment : Fragment() {

    private var _binding: FragmentLoginInfoBinding? = null
    private val binding: FragmentLoginInfoBinding get() = requireNotNull(_binding)

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            observeData(it.getSerializable(NAVIGATE) as () -> Unit)
        }
    }

    private fun observeData(navigateToChecking: () -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collectLatest { state ->
                        if (state.toastMessage != null) {
                            Toast.makeText(requireContext(), state.toastMessage, Toast.LENGTH_LONG).show()
                            viewModel.toastMessageShown()
                        }

                        binding.apply {
                            btnNext.visibility = (!(state.isLoading)).visibility
                            pbInfo.visibility = state.isLoading.visibility
                        }
                        if (!state.isLoading) {
                            initView(navigateToChecking)
                        }
                    }
                }
            }
        }
    }

    private fun initView(navigateToChecking: () -> Unit) {
        binding.apply {
            initGenderButtons()
            initGradeSpinner()
            initYearSpinner()
            initMajorSpinner()

            btnNext.setOnClickListener {
                if (etAge.text.isNullOrBlank()
                    || etAge.text.isNullOrEmpty()
                    || viewModel.grade < 0) {
                    Toast.makeText(requireContext(), R.string.msg_info_notice, Toast.LENGTH_SHORT).show()
                } else if (viewModel.grade == 3 && viewModel.major < 0) {
                    Toast.makeText(requireContext(), R.string.msg_major_notice, Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.signUp(etAge.text.toString().toInt()) {
                        navigateToChecking()
                    }
                }
            }
        }
    }

    private fun initGenderButtons() {
        binding.apply {
            btnMan.setOnClickListener {
                btnMan.setBackgroundColor(requireContext().getColor(R.color.radio_selected))
                btnWoman.setBackgroundColor(requireContext().getColor(R.color.radio_unselected))
                viewModel.gender = 1
            }
            btnWoman.setOnClickListener {
                btnMan.setBackgroundColor(requireContext().getColor(R.color.radio_unselected))
                btnWoman.setBackgroundColor(requireContext().getColor(R.color.radio_selected))
                viewModel.gender = 2
            }
        }
    }

    private fun initGradeSpinner() {
        binding.apply {
            spnGrade.onItemSelectedListener = ItemSelectedListener { pos ->
                when (pos) {
                    0 -> { // 중학생
                        viewModel.grade = 10
                        spnYear.isEnabled = true
                        viewModel.major = -1
                        llMajor.visibility = false.visibility
                        spnYear.adapter = ArrayAdapter.createFromResource(
                            requireContext(),
                            R.array.middle_array,
                            android.R.layout.simple_spinner_item
                        ).apply {
                            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        }
                    }
                    1 -> { // 고등학생
                        viewModel.grade = 20
                        spnYear.isEnabled = true
                        viewModel.major = -1
                        llMajor.visibility = false.visibility
                        spnYear.adapter = ArrayAdapter.createFromResource(
                            requireContext(),
                            R.array.high_array,
                            android.R.layout.simple_spinner_item
                        ).apply {
                            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        }
                    }
                    2 -> { // 대학생
                        viewModel.grade = 30
                        spnYear.isEnabled = true
                        viewModel.major = 1
                        llMajor.visibility = true.visibility
                        spnYear.adapter = ArrayAdapter.createFromResource(
                            requireContext(),
                            R.array.univ_array,
                            android.R.layout.simple_spinner_item
                        ).apply {
                            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        }
                    }
                    else -> { // 기타
                        viewModel.grade = 40
                        spnYear.isEnabled = false
                        viewModel.major = -1
                        llMajor.visibility = false.visibility
                        spnYear.adapter = null
                    }
                }
            }
        }
    }

    private fun initYearSpinner() {
        binding.apply {
            spnYear.onItemSelectedListener = ItemSelectedListener { pos ->
                when (pos) {
                    0 -> viewModel.grade -= viewModel.grade%10 - 1
                    1 -> { // 고등학생 2학년 부터 major 선택 가능
                        if (viewModel.grade in 20..29) {
                            viewModel.major = 1
                            llMajor.visibility = true.visibility
                        }
                        viewModel.grade -= viewModel.grade%10 - 2
                    }
                    2 -> {
                        if (viewModel.grade in 20..29) {
                            viewModel.major = 1
                            llMajor.visibility = true.visibility
                        }
                        viewModel.grade -= viewModel.grade%10 - 3
                    }
                    3 -> viewModel.grade -= viewModel.grade%10 - 4
                    4 -> viewModel.grade -= viewModel.grade%10 - 5
                    else -> viewModel.grade -= viewModel.grade%10 - 6
                }
            }
        }
    }

    private fun initMajorSpinner() {
        binding.spnMajor.onItemSelectedListener = ItemSelectedListener { pos ->
            when (pos) {
                0 -> viewModel.major = 1
                1 -> viewModel.major = 2
                else -> viewModel.major = 3
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(navigateToChecking: () -> Unit) =
            LoginInfoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(NAVIGATE, navigateToChecking as java.io.Serializable)
                }
            }
    }
}