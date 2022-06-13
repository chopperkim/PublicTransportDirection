package kim.chopper.direction

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kako SDK 초기화
        KakaoSdk.init(this, "6cd6dfcfeea854841e75681f3d338cf6")
    }
}