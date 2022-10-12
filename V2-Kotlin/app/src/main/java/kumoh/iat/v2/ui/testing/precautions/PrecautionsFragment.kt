package kumoh.iat.v2.ui.testing.precautions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kumoh.iat.v2.R
import kumoh.iat.v2.databinding.FragmentPrecautionsBinding

private const val NAVIGATE = "NAVIGATE"

class PrecautionsFragment : Fragment() {

    private var _binding: FragmentPrecautionsBinding? = null
    private val binding: FragmentPrecautionsBinding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrecautionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            initView(it.getSerializable(NAVIGATE) as () -> Unit)
        }
    }

    private fun initView(navigateToSurvey: () -> Unit) {
        binding.btnStart.setOnClickListener {
            navigateToSurvey()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            navigateToSurvey: () -> Unit
        ) = PrecautionsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(NAVIGATE, navigateToSurvey as java.io.Serializable)
            }
        }
    }
}