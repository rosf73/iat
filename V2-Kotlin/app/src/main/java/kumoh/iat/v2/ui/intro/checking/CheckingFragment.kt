package kumoh.iat.v2.ui.intro.checking

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kumoh.iat.v2.databinding.FragmentCheckingBinding
import kumoh.iat.v2.ui.MainActivity
import kumoh.iat.v2.ui.testing.TestingActivity

class CheckingFragment : Fragment() {

    private var _binding: FragmentCheckingBinding? = null
    private val binding: FragmentCheckingBinding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.yesButton.setOnClickListener {
            Intent(requireContext(), TestingActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(this)
            }
        }
        binding.noButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("안내")
                .setMessage("진행 중인 공부가 없으시다면 다음 문항을 진행할 수 없습니다. 참여해주셔서 감사합니다.")
                .setPositiveButton("확인") { _, _ ->
                    Intent(requireContext(), MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(this)
                    }
                }
                .create()
                .show()
        }
    }
}