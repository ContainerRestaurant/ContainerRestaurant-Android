package container.restaurant.android.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import container.restaurant.android.R
import container.restaurant.android.presentation.onBoarding.OnBoardingActivity
import container.restaurant.android.util.SharedPrefUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val TIME: Long = 1500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        CoroutineScope(Dispatchers.IO).launch {
            delay(TIME)
            // 앱 설치 시 처음 한 번만 온보딩 화면이 보이도록 설정
            if(SharedPrefUtil.getBoolean(this@SplashActivity) { IS_ON_BOARDING_FIRST }){
                SharedPrefUtil.setBoolean(this@SplashActivity, { IS_ON_BOARDING_FIRST }, false)

                val intent = Intent(applicationContext, OnBoardingActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        }
    }
}