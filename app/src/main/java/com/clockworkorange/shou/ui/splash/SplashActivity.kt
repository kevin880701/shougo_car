package com.clockworkorange.shou.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.clockworkorange.shou.R
import com.clockworkorange.shou.ext.addTo
import com.clockworkorange.shou.ui.base.BaseActivity
import com.clockworkorange.shou.ui.login.LoginActivity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPref = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val isFirstInstall = sharedPref.getBoolean("first_install", true)

        if (isFirstInstall) {
            // 这是第一次安装应用程序
            // 执行初始化操作
            // 例如，将值更改为 TRUE
            val editor = sharedPref.edit()
            editor.putBoolean("first_install", false)
            editor.apply()
        }

        Single.timer(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            },{
                Timber.e(it)
            }).addTo(disposable)
    }
}