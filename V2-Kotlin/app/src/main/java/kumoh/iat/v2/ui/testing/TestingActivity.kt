package kumoh.iat.v2.ui.testing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import kumoh.iat.v2.databinding.ActivityTestingBinding
import kumoh.iat.v2.ui.testing.precautions.PrecautionsFragment
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
            .replace(binding.fcvTesting.id, PrecautionsFragment.newInstance(navigateToSurvey))
            .commit()
    }

    private val navigateToSurvey: () -> Unit = {
        supportFragmentManager.beginTransaction()
            .replace(binding.fcvTesting.id, SurveyFragment.newInstance(
                navigateToIAT,
                navigateToFinish
            ))
            .commit()
    }

    private val navigateToIAT: () -> Unit = {

    }

    private val navigateToFinish: () -> Unit = {

    }
}