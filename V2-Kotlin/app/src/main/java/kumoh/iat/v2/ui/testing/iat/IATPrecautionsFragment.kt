package kumoh.iat.v2.ui.testing.iat

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kumoh.iat.v2.R
import kumoh.iat.v2.databinding.FragmentIatPrecautionsBinding
import kumoh.iat.v2.ui.customadapter.CustomWordTableAdapter
import kumoh.iat.v2.ui.testing.TestingViewModel

private const val NAVIGATE = "NAVIGATE"

class IATPrecautionsFragment : Fragment() {

    private var _binding: FragmentIatPrecautionsBinding? = null
    private val binding: FragmentIatPrecautionsBinding get() = requireNotNull(_binding)

    private val viewModel: TestingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIatPrecautionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            initView(it.getSerializable(NAVIGATE) as () -> Unit)
        }
    }

    private fun initView(
        navigateToIAT: () -> Unit
    ) {
        binding.apply {
            viewModel.getNowIATContent()?.let { content ->
                ctvWords.setAdapter(
                    CustomWordTableAdapter(
                        requireContext(),
                        R.layout.item_word_row,
                        content.words.map { it.subject },
                        content.words.map { it.words }
                    )
                )
            }

            btnStart.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("안내")
                    .setMessage("마구잡이로 눌러 참여할 시 보상이 제공되지 않습니다. 신중하게 누르십시오.")
                    .setPositiveButton("신중히 참여하겠습니다") { _, _ ->
                        viewModel.setIATDone()
                        navigateToIAT()
                    }
                    .create()
                    .show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            navigateToIAT: () -> Unit
        ) = IATPrecautionsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(NAVIGATE, navigateToIAT as java.io.Serializable)
            }
        }
    }
}