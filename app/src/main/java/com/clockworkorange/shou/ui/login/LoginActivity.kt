package com.clockworkorange.shou.ui.login

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.clockworkorange.shou.APP
import com.clockworkorange.shou.R
import com.clockworkorange.shou.ShouGoService
import com.clockworkorange.shou.ShouGoService.Companion.isGpsSignal
import com.clockworkorange.shou.databinding.ActivityLoginBinding
import com.clockworkorange.shou.ui.MainActivity
import com.clockworkorange.shou.ui.MessageDialogFragment
import com.clockworkorange.shou.ui.base.BaseActivity
import com.clockworkorange.shou.util.QRCodeUtil
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : BaseActivity(), RequirePermissionFragment.Listener, PrivacyFragment.Listener {

    private val viewModel: LoginViewModel by viewModels { (applicationContext as APP).appContainer.viewModelFactory }

    private var _binding: ActivityLoginBinding? = null

    private var listener: RequirePermissionFragment.Listener? = null
    var isFirstStart = true
    private val binding get() = _binding!!
    private var qrCodeBitmap: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        initView()
        bindViewModel()

        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                if (ActivityCompat.checkSelfPermission(
                        this@LoginActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@LoginActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    withContext(Dispatchers.Main) {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 0L, 500F,
                            ShouGoService.locationListener
                        )
                    }

                    if (isGpsSignal) {
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(this@LoginActivity, "GPS訊號優良", Toast.LENGTH_SHORT).show()
//                    }
                        viewModel.checkLocationPermission()
                        break
                    } else {
                        if (isFirstStart) {
                            delay(1000)
                            isFirstStart = false
                        } else {
                            withContext(Dispatchers.Main) {

//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                                    // Android 10及以上的使用
//                                    Toast.makeText(
//                                        this@LoginActivity,
//                                        "GPS訊號不良",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//                                } else {
                                // Android 10以下的使用
                                val inflater = LayoutInflater.from(this@LoginActivity)
                                val layout = inflater.inflate(R.layout.toast_check, null)

                                val toast = Toast(this@LoginActivity)
                                toast.setGravity(Gravity.BOTTOM, 0, 0)
                                toast.duration = Toast.LENGTH_LONG
                                toast.view = layout
                                layout.findViewById<TextView>(R.id.textToast).text =
                                    "GPS訊號不良"
                                toast.show()
//                                }
                            }
                        }
                    }
                    delay(5000)
                } else {
                    if (ContextCompat.checkSelfPermission(
                            this@LoginActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // 如果權限尚未授予，請求 ACCESS_COARSE_LOCATION 權限
                        ActivityCompat.requestPermissions(
                            this@LoginActivity,
                            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                            200
                        )
                    }
                }
            }
        }
    }

    private fun initView() {
        binding.btNoLogin.setOnClickListener {
//            if(isGpsSignal){
            FirebaseCrashlytics.getInstance().setUserId("")
            binding.flLoading.isVisible = true
            MainActivity.start(this)
            finish()
//            }
        }
    }

    private fun bindViewModel() {
        viewModel.showRequireLocationPermission.observe(this) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.fl_container,
                    RequirePermissionFragment.newInstance(this),
                    RequirePermissionFragment.TAG
                )
                .addToBackStack(null)
                .commit()
        }

        viewModel.showPrivacy.observe(this) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fl_container, PrivacyFragment.newInstance(this), PrivacyFragment.TAG)
                .addToBackStack(null)
                .commit()
        }

        viewModel.navigateMainScreen.observe(this) {
            // 有GPS訊號才可登入
            if (isGpsSignal) {
                binding.flLoading.isVisible = true
                MainActivity.start(this)
                finish()
            }
        }

        viewModel.loginQRCodeContent.observe(this) { deviceId ->
            qrCodeBitmap = QRCodeUtil.createBitmap(deviceId)
            binding.ivQrCode.setImageBitmap(qrCodeBitmap)
        }

        viewModel.eventStartShouService.observe(this) {
            // when location permissions check pass
            ShouGoService.start(this.applicationContext)
        }

        viewModel.eventToast.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.skipLoginEnable.observe(this) {
            binding.btNoLogin.isEnabled = it
        }

        viewModel.eventNetError.observe(this) {
            MessageDialogFragment.newInstance("網路異常", "請檢查網路設定") {
                startService(ShouGoService.createStopIntent(this))
                finish()
                android.os.Process.killProcess(android.os.Process.myPid())
            }.show(supportFragmentManager, "dialog")
        }
    }

    override fun onPermissionGranted() {
        viewModel.setLocationPermissionGranted()
        supportFragmentManager.findFragmentByTag(RequirePermissionFragment.TAG)?.let {
            supportFragmentManager.beginTransaction()
                .remove(it)
                .commit()
        }
    }

    override fun onPrivacyRead() {
        viewModel.setPrivacyRead()
        supportFragmentManager.findFragmentByTag(PrivacyFragment.TAG)?.let {
            supportFragmentManager.beginTransaction()
                .remove(it)
                .commit()
        }
    }


    private fun showNeedPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("需要定位權限才能執行APP")
            .setPositiveButton("確定") { _, _ ->
                finish()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        qrCodeBitmap?.recycle()
        qrCodeBitmap = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ACCESS_COARSE_LOCATION 權限已授予
                } else {
                    // 用戶拒絕了權限請求
                    showNeedPermissionDialog()
                }
            }

            200 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ACCESS_COARSE_LOCATION 權限已授予
                } else {
                    // 用戶拒絕了權限請求
//                    showNeedPermissionDialog()
                }
            }
        }
    }
}