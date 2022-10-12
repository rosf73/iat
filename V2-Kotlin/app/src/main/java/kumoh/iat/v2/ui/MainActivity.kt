package kumoh.iat.v2.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kumoh.iat.v2.databinding.ActivityMainBinding
import kumoh.iat.v2.ui.intro.IntroActivity
import kumoh.iat.v2.util.visibility

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = requireNotNull(_binding)

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeData()
    }

    private fun observeData() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collectLatest { state ->
                        if (state.toastMessage != null) {
                            Toast.makeText(this@MainActivity, state.toastMessage, Toast.LENGTH_LONG).show()
                            viewModel.toastMessageShown()
                        }

                        binding.apply {
                            btnIntro.visibility = (!(state.isLoading)).visibility
                            pbMain.visibility = state.isLoading.visibility
                        }
                        if (!state.isLoading) {
                            initView()
                        }
                    }
                }
            }
        }
    }

    private fun initView() {
        binding.tvMainGift.text = viewModel.uiState.value.projectInfo?.giftNotice
        binding.btnIntro.setOnClickListener {
            viewModel.uiState.value.projectInfo?.let { info ->
                Intent(applicationContext, IntroActivity::class.java).apply {
                    putExtra(IntroActivity.PROJECT_NAME, info.projectName)
                    putExtra(IntroActivity.DESCRIPTION, info.description)
                    putExtra(IntroActivity.AGREEMENT, info.agreement)
                    startActivity(this)
                }
            }
        }
    }
}