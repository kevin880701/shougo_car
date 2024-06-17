package com.clockworkorange.shou.ui.redirection

import android.content.Intent
import android.os.Bundle
import com.clockworkorange.repository.UserRepository
import com.clockworkorange.shou.APP
import com.clockworkorange.shou.R
import com.clockworkorange.shou.ui.MainActivity
import com.clockworkorange.shou.ui.base.BaseActivity
import com.clockworkorange.shou.ui.login.LoginActivity
import com.clockworkorange.shou.ui.tab.main.NavigationAction
import timber.log.Timber

/**
 * 重導向參數
 * command: 指令 目前有(recommend_coupon、recommend_area、favorite_list)
 * arg: 指令參數(見下方說明)
 * speak_text:使用者語音輸入的文字
 *
 * 情境:
 * 1.嗨小莫 推薦店家的優惠
 *
 * 參數:
 * command: recommend_coupon
 * arg:category:餐廳  category 請參考 [IShouPlace.categoryMap]
 * speak_text:嗨小莫 推薦店家的優惠
 *
 * deeplink:
 * cwo://com.clockworkorange.shou?command=recommend_coupon&arg=category:餐廳&speak_text=嗨小莫 推薦店家的優惠
 *
 * 2.嗨小莫 [台中市烏日區、台北市內湖區] 的推薦
 *
 * 參數:
 * command: recommend_area
 * arg: area:台中市烏日區
 * speak_text: 嗨小莫 台中市烏日區 的推薦
 *
 * deeplink:
 * cwo://com.clockworkorange.shou?command=recommend_area&arg=area:台中市烏日區&speak_text=嗨小莫 台中市烏日區 的推薦
 *
 * 3.嗨小莫 [優惠券、商家、景點] 的收藏清單
 *         [coupon, store, place]
 * ex1:
 * command:favorite_list
 * arg: type:coupon
 * speak_text:嗨小莫 優惠券 的收藏清單
 *
 * deeplink:
 * cwo://com.clockworkorange.shou?command=favorite_list&arg=type:coupon&speak_text=嗨小莫 優惠券 的收藏清單
 *
 * ex2:
 * command:favorite_list
 * arg: type:store
 * speak_text:嗨小莫 商家 的收藏清單
 *
 * deeplink:
 * cwo://com.clockworkorange.shou?command=favorite_list&arg=type:store&speak_text=嗨小莫 商家 的收藏清單
 *
 *
 * test deeplink:
 * adb shell am start "cwo://com.clockworkorange.shou?command=recommend_area\&arg=area:台中市烏日區\&speak_text=嗨小莫\ 台中市烏日區\ 的推薦"
 */
class RedirectionActivity : BaseActivity() {

    private val repository: UserRepository by lazy { (applicationContext as APP).appContainer.userRepository }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent){
        try {
            val action = NavigationAction.fromIntent(intent)
            APP.navAction = action
            if (repository.isAppLogin()){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }catch (e: Exception){
            Timber.e(e)
        }
    }



}