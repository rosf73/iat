package kumoh.iat.v2.ui.intro.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kumoh.iat.v2.R
import kumoh.iat.v2.databinding.FragmentLoginPhoneBinding
import kumoh.iat.v2.util.checkNumberRegex
import kumoh.iat.v2.util.visibility

private const val NAVIGATE_INFO = "NAVIGATE_INFO"
private const val NAVIGATE_CHECK = "NAVIGATE_CHECK"

@AndroidEntryPoint
class LoginPhoneFragment : Fragment() {

    private var _binding: FragmentLoginPhoneBinding? = null
    private val binding: FragmentLoginPhoneBinding get() = requireNotNull(_binding)

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            observeData(
                it.getSerializable(NAVIGATE_INFO) as () -> Unit,
                it.getSerializable(NAVIGATE_CHECK) as () -> Unit
            )
        }
    }

    private fun observeData(
        navigateToInfo: () -> Unit,
        navigateToChecking: () -> Unit
    ) {
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
                            pbPhone.visibility = state.isLoading.visibility
                        }
                        if (!state.isLoading) {
                            initView(navigateToInfo, navigateToChecking)
                        }
                    }
                }
            }
        }
    }

    private fun initView(
        navigateToInfo: () -> Unit,
        navigateToChecking: () -> Unit
    ) {
        binding.btnNext.setOnClickListener {
            val phoneNum = binding.etPhone.text.toString()
            if (checkNumberRegex(phoneNum.replace(Regex("\\D"), ""))) {
                viewModel.phoneNum = phoneNum
                viewModel.login { msg ->
                    when (msg) {
                        "NO_EXIST" -> {
                            navigateToInfo()
                        }
                        "EXIST" -> {
                            navigateToChecking()
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), R.string.msg_phone_notice, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            navigateToInfo: () -> Unit,
            navigateToChecking: () -> Unit
        ) = LoginPhoneFragment().apply {
            arguments = Bundle().apply {
                putSerializable(NAVIGATE_INFO, navigateToInfo as java.io.Serializable)
                putSerializable(NAVIGATE_CHECK, navigateToChecking as java.io.Serializable)
            }
        }
    }
}