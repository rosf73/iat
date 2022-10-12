package kumoh.iat.v2.ui.intro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kumoh.iat.v2.databinding.ActivityIntroBinding
import kumoh.iat.v2.ui.intro.agreement.AgreementFragment
import kumoh.iat.v2.ui.intro.checking.CheckingFragment
import kumoh.iat.v2.ui.intro.login.LoginInfoFragment
import kumoh.iat.v2.ui.intro.login.LoginPhoneFragment

@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {

    private var _binding: ActivityIntroBinding? = null
    private val binding: ActivityIntroBinding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        supportFragmentManager.beginTransaction()
            .replace(binding.fcvIntro.id, AgreementFragment.newInstance(
                intent.getStringExtra(PROJECT_NAME) ?: "",
                intent.getStringExtra(DESCRIPTION) ?: "",
                intent.getStringExtra(AGREEMENT) ?: "",
                navigateToLogin
            ))
            .commit()
    }

    private val navigateToLogin: () -> Unit = {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(binding.fcvIntro.id, LoginPhoneFragment.newInstance(navigateToInfo, navigateToChecking))
            .commit()
    }

    private val navigateToInfo: () -> Unit = {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(binding.fcvIntro.id, LoginInfoFragment.newInstance(navigateToChecking))
            .commit()
    }

    private val navigateToChecking: () -> Unit = {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(binding.fcvIntro.id, CheckingFragment())
            .commit()
    }

    companion object {
        const val PROJECT_NAME = "PROJECT_NAME"
        const val DESCRIPTION = "DESCRIPTION"
        const val AGREEMENT = "AGREEMENT"
    }
}