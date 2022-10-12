package kumoh.iat.v2.ui.intro.agreement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kumoh.iat.v2.databinding.FragmentAgreementBinding

private const val PROJECT_NAME = "PROJECT_NAME"
private const val DESCRIPTION = "DESCRIPTION"
private const val AGREEMENT = "AGREEMENT"
private const val NAVIGATE = "NAVIGATE"

class AgreementFragment : Fragment() {

    private var _binding: FragmentAgreementBinding? = null
    private val binding: FragmentAgreementBinding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgreementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            initView(it)
        }
    }

    private fun initView(arg: Bundle) {
        binding.apply {
            tvProject.text = arg.getString(PROJECT_NAME)
            tvDescription.text = arg.getString(DESCRIPTION)
            tvAgreement.text = arg.getString(AGREEMENT)
            btnAgree.setOnClickListener {
                (arg.getSerializable(NAVIGATE) as () -> Unit)()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            projectName: String,
            description: String,
            agreement: String,
            navigateToLogin: () -> Unit
        ) = AgreementFragment().apply {
                arguments = Bundle().apply {
                    putString(PROJECT_NAME, projectName)
                    putString(DESCRIPTION, description)
                    putString(AGREEMENT, agreement)
                    putSerializable(NAVIGATE, navigateToLogin as java.io.Serializable)
                }
            }
    }
}