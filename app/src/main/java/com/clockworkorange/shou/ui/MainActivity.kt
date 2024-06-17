package com.clockworkorange.shou.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.clockworkorange.shou.APP
import com.clockworkorange.shou.BuildConfig
import com.clockworkorange.shou.CarLightReceiver
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.ActivityMainBinding
import com.clockworkorange.shou.ui.base.BaseActivity
import com.clockworkorange.shou.ui.tab.main.NavigationAction
import com.clockworkorange.shou.ui.tab.main.VoiceCommand
import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber


class MainActivity : BaseActivity() {

    companion object{
        fun start(context: Context){
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val viewModel: MainViewModel by viewModels{(applicationContext as APP).appContainer.viewModelFactory}

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    enum class APPTabItem{
        Account,
        Main,
        Favorite,
        Team,
        Package,
        Pocket,
        Setting
    }

    private var currentTab: APPTabItem = APPTabItem.Main

    private val tabAdapter by lazy { MainTabAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initListener()
        bindViewModel()
        selectTab(APPTabItem.Main)

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val token = it.result
                    viewModel.setFcmToken(token)
                    Timber.d("token: $token")
                }else{
                    Toast.makeText(this, "取得推播token fail", Toast.LENGTH_SHORT).show()
                }
            }

        CarLightReceiver.register(this)

        if (APP.navAction != null){
            viewModel.handleNavigationAction(APP.navAction!!)
            APP.navAction = null
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (APP.navAction != null){
            viewModel.handleNavigationAction(APP.navAction!!)
            APP.navAction = null
        }
    }

    private fun initView() {
        with(binding){
            binding.vpContainer.offscreenPageLimit = 2
            vpContainer.adapter = tabAdapter
            vpContainer.isUserInputEnabled = false
            vpContainer.setCurrentItem(1, false)
        }
    }

    override fun onNightModeChanged(mode: Int) {
        when (mode) {
            AppCompatDelegate.MODE_NIGHT_NO ->{
                APP.nightMode.value = false
            }
            AppCompatDelegate.MODE_NIGHT_YES -> {
                APP.nightMode.value = true
            }
        }

        with(binding){
            llTab.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.color_primary))
            val state = ContextCompat.getColorStateList(this@MainActivity, R.color.color_state_main_tab_tint)
            ivTabAccount.imageTintList = state
            ivTabMain.imageTintList = state
            ivTabHeart.imageTintList = state
            ivTabTeam.imageTintList = state
            ivTabPackage.imageTintList = state
            ivTabPocket.imageTintList = state
            ivTabSettings.imageTintList = state

            fun createState() = ContextCompat.getDrawable(this@MainActivity, R.drawable.bg_main_tab)

            ivSelectAccount.background = createState()
            ivSelectMain.background = createState()
            ivSelectHeart.background = createState()
            ivSelectTeam.background = createState()
            ivSelectPackage.background = createState()
            ivSelectPocket.background = createState()
            ivSelectSetting.background = createState()
        }
    }

    private fun initListener() {
        with(binding){
            flTabAccount.setOnClickListener { selectTab(APPTabItem.Account) }
            flTabMain.setOnClickListener { selectTab(APPTabItem.Main) }
            flTabFavorite.setOnClickListener { selectTab(APPTabItem.Favorite) }
            flTabTeam.setOnClickListener {
                if (BuildConfig.DEBUG) {
                    viewModel.handleNavigationAction(
                        NavigationAction(
                            VoiceCommand.RecommendCoupon,
                            mapOf("category" to "餐廳"),
                            ""
                        )
                    )
                } else {
                    selectTab(APPTabItem.Team)
                }
            }
            flTabTeam.setOnLongClickListener {
                if (BuildConfig.DEBUG) {
                    viewModel.handleNavigationAction(
                        NavigationAction(
                            VoiceCommand.RecommendCoupon,
                            mapOf("category" to "景點"),
                            ""
                        )
                    )
                    return@setOnLongClickListener true
                } else return@setOnLongClickListener false
            }
            flTabPackage.setOnClickListener {
                selectTab(APPTabItem.Package)
            }
            flTabPocket.setOnClickListener {
                if (BuildConfig.DEBUG) {
                    val intent = Intent("action_voice_input").apply {
                        putExtra("voice_control", "NEXT_PAGE")
                    }
                    sendBroadcast(intent)
                } else {
                    selectTab(APPTabItem.Pocket)
                }
            }
            flTabSettings.setOnClickListener { selectTab(APPTabItem.Setting) }
//            flLoading.setOnClickListener {  }
        }
    }

    private fun selectTab(tab: APPTabItem){
        currentTab = tab
        with(binding){

            flTabAccount.isSelected = false
            flTabMain.isSelected = false
            flTabFavorite.isSelected = false
            flTabTeam.isSelected = false
            flTabPackage.isSelected = false
            flTabPocket.isSelected = false
            flTabSettings.isSelected = false

            when(tab){
                APPTabItem.Account -> {
                    flTabAccount.isSelected = true
                    binding.vpContainer.setCurrentItem(0, false)
                }

                APPTabItem.Main -> {
                    flTabMain.isSelected = true
                    binding.vpContainer.setCurrentItem(1, false)
                }

                APPTabItem.Favorite -> {
                    flTabFavorite.isSelected = true
                    binding.vpContainer.setCurrentItem(2, false)
                }

                APPTabItem.Team -> {
                    flTabTeam.isSelected = true
                }

                APPTabItem.Package -> {
                    flTabPackage.isSelected = true
                }

                APPTabItem.Pocket -> {
                    flTabPocket.isSelected = true
                }

                APPTabItem.Setting -> {
                    flTabSettings.isSelected = true
                    binding.vpContainer.setCurrentItem(3, false)
                }
            }
        }
    }

    private fun bindViewModel() {
        viewModel.eventToast.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }

        viewModel.eventOpenTab.observe(this){ tab ->
            if (currentTab != tab) selectTab(tab)
        }

        viewModel.eventNightMode.observe(this){
            if (it) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

    }

    fun onMapLoaded(){
//        binding.vpContainer.offscreenPageLimit = 2
    }

    fun showLoading(){
//        binding.flLoading.isInvisible = false
    }

    fun hideLoading(){
//        binding.flLoading.isInvisible = true
    }

    override fun onDestroy() {
        CarLightReceiver.unregister(this)
        super.onDestroy()
    }
}