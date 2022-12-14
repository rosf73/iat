package kumoh.iat.v2.ui.testing

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.replace
import dagger.hilt.android.AndroidEntryPoint
import kumoh.iat.v2.databinding.ActivityTestingBinding
import kumoh.iat.v2.ui.MainActivity
import kumoh.iat.v2.ui.testing.iat.IATExplanationFragment
import kumoh.iat.v2.ui.testing.iat.IATFragment
import kumoh.iat.v2.ui.testing.iat.IATPrecautionsFragment
import kumoh.iat.v2.ui.testing.survey.PrecautionsFragment
import kumoh.iat.v2.ui.testing.survey.SurveyFragment

@AndroidEntryPoint
class TestingActivity : AppCompatActivity() {

    private var _binding: ActivityTestingBinding? = null
    private val binding: ActivityTestingBinding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTestingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        supportFragmentManager.beginTransaction()
            .replace(binding.fcvTesting.id, PrecautionsFragment.newInstance(
                navigateToSurvey
            ))
            .commit()
    }

    private val navigateToSurvey: () -> Unit = {
        supportFragmentManager.beginTransaction()
            .replace(binding.fcvTesting.id, SurveyFragment.newInstance(
                {
                    supportFragmentManager.beginTransaction()
                        .addToBackStack(null)
                    navigateToIATPrecautions()
                },
                navigateToFinish
            ), "SURVEY")
            .commit()
    }

    private val navigateToIATPrecautions: () -> Unit = {
        supportFragmentManager.beginTransaction()
            .add(binding.fcvTesting.id, IATPrecautionsFragment.newInstance(
                navigateToIATExplanation
            ))
            .commit()
    }

    private val navigateToIATExplanation: () -> Unit = {
        supportFragmentManager.beginTransaction()
            .replace(binding.fcvTesting.id, IATExplanationFragment.newInstance(
                navigateToIAT,
                backToSurvey
            ))
            .commit()
    }

    private val navigateToIAT: () -> Unit = {
        supportFragmentManager.beginTransaction()
            .replace(binding.fcvTesting.id, IATFragment.newInstance(
                navigateToIATExplanation,
                navigateToIATPrecautions
            ))
            .commit()
    }

    private val backToSurvey: () -> Unit = {
        supportFragmentManager.findFragmentByTag("SURVEY")?.let { fragment ->
            supportFragmentManager.beginTransaction()
                .replace(binding.fcvTesting.id, fragment)
                .commit()
        }
    }

    private val navigateToFinish: () -> Unit = {
        //TODO ?????? ?????? ???????????? ??????
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("??????")
            .setMessage("?????? ???????????? ?????? ????????? ???????????? ????????????. ????????? ????????? ?????????????????????????")
            .setPositiveButton("??????") { _, _ ->
                Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(this)
                }
            }
            .setNegativeButton("??????", null)
            .create()
            .show()
    }
}