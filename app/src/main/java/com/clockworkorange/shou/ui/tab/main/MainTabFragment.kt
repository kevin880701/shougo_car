package com.clockworkorange.shou.ui.tab.main

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.clockworkorange.repository.domain.NewCouponNotification
import com.clockworkorange.repository.domain.ShouCoupon
import com.clockworkorange.repository.domain.ShouPlace
import com.clockworkorange.repository.domain.VoiceStore
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.ItemCouponBinding
import com.clockworkorange.shou.databinding.ItemPlaceBinding
import com.clockworkorange.shou.databinding.MainTabFragmentBinding
import com.clockworkorange.shou.ext.*
import com.clockworkorange.shou.ui.MainActivity
import com.clockworkorange.shou.ui.MainViewModel
import com.clockworkorange.shou.ui.base.BaseFragment
import com.clockworkorange.shou.ui.tab.main.MainTabViewModel.DataSourceOption.*
import com.clockworkorange.shou.ui.tab.main.coupon.ShouCouponDetailFragment
import com.clockworkorange.shou.ui.tab.main.coupon.ShouCouponListFragment
import com.clockworkorange.shou.ui.tab.main.couponlistbyplace.CouponListByPlaceFragment
import com.clockworkorange.shou.ui.tab.main.place.ShouPlaceDetailFragment
import com.clockworkorange.shou.ui.tab.main.place.ShouPlaceListFragment
import com.clockworkorange.shou.ui.tab.main.store.VoiceStoreListFragment
import com.clockworkorange.shou.ui.tab.main.topmenu.SelectAreaFragment
import com.clockworkorange.shou.ui.tab.main.topmenu.SelectDestFragment
import com.clockworkorange.shou.ui.tab.main.travel.MyTravelFragment
import com.clockworkorange.shou.ui.tab.main.traveltoadd.SelectTravelToAddFragment
import com.clockworkorange.shou.util.DoubleArrayEvaluator
import com.clockworkorange.shou.util.RouteUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*


@FlowPreview
@ExperimentalCoroutinesApi
class MainTabFragment : BaseFragment(), OnMapReadyCallback {

    companion object {
        const val TAG = "MainTabFragment"
    }

    private val viewModel: MainTabViewModel by viewModels { viewModelFactory }
    private val mainViewModel: MainViewModel by activityViewModels { viewModelFactory }

    private var _binding: MainTabFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap

    private var currentZoomLevel: Float = 18f

    private var markerMe: Marker? = null

    private var markerStore1: Marker? = null
    private var markerStore2: Marker? = null
    private var markerStore3: Marker? = null
    private val otherStoreMarkerList = mutableListOf<Marker>()

    private var markerPlace1: Marker? = null
    private var markerPlace2: Marker? = null
    private var markerPlace3: Marker? = null
    private val otherPlaceMarkerList = mutableListOf<Marker>()

    private val voiceStoreMarkerList = mutableListOf<Marker>()

    private var markerHighlight: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainTabFragmentBinding.inflate(layoutInflater)
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {

        binding.viewTouch.setOnTouchListener { v, event ->
            val x = event.x
            val y = event.y

            if (binding.ilTopMenuContent.root.isVisible && !binding.ilTopMenuContent.root.getViewRect().contains(x, y)){
                binding.ilTopMenuContent.root.isVisible = false
                return@setOnTouchListener  true
            }

            return@setOnTouchListener false
        }

        with(binding) {
            ilCoupon1.root.setOnClickListener { viewModel.clickCoupon(0) }

            ilCoupon2.root.setOnClickListener { viewModel.clickCoupon(1) }

            ilCoupon3.root.setOnClickListener { viewModel.clickCoupon(2) }

            ilCouponMore.root.setOnClickListener { viewModel.clickCouponMore() }

            ilPlace1.root.setOnClickListener { viewModel.clickPlace(0) }

            ilPlace2.root.setOnClickListener { viewModel.clickPlace(1) }

            ilPlace3.root.setOnClickListener { viewModel.clickPlace(2) }

            ilPlaceMore.root.setOnClickListener { viewModel.clickPlaceMore() }

            ilCoupon1.btLike.setOnClickListener {
                forceCouponLikeState(0)
                viewModel.likeCoupon(0)
            }
            ilCoupon2.btLike.setOnClickListener {
                forceCouponLikeState(1)
                viewModel.likeCoupon(1)
            }
            ilCoupon3.btLike.setOnClickListener {
                forceCouponLikeState(2)
                viewModel.likeCoupon(2)
            }

            ilPlace1.btLike.setOnClickListener {
                forcePlaceLikeState(0)
                viewModel.likePlace(0)
            }
            ilPlace2.btLike.setOnClickListener {
                forcePlaceLikeState(1)
                viewModel.likePlace(1)
            }
            ilPlace3.btLike.setOnClickListener {
                forcePlaceLikeState(2)
                viewModel.likePlace(2)
            }

            ilTravelState.root.setOnClickListener { viewModel.clickMyTravel() }

            ilTopMenu.root.setOnClickListener { viewModel.clickTopMenu() }

            ilTopMenuContent.flByArea.setOnClickListener { viewModel.clickSelectDataSource(Area) }

            ilTopMenuContent.flByLocation.setOnClickListener {
                viewModel.clickSelectDataSource(
                    Location
                )
            }

            ilTopMenuContent.flByDest.setOnClickListener { viewModel.clickSelectDataSource(Dest) }

            ivEditSource.setOnClickListener { viewModel.clickEditDataSource() }

            tvCheckNewCoupon.setOnClickListener {
                viewModel.checkNewCoupon()
                binding.flNewCouponNotification.isVisible = false
                viewModel.newCouponNotificationHandled()
            }

            tvCancelNewCoupon.setOnClickListener {
                binding.flNewCouponNotification.isVisible = false
                viewModel.newCouponNotificationHandled()
            }
        }

    }


    private fun forceCouponLikeState(order: Int){
        val isLike = viewModel.isCouponInFavList(order)
        val imgRes = if (isLike) R.drawable.ic_heart_unlike else R.drawable.ic_heart_like
        when(order){
            0 -> binding.ilCoupon1.btLike.setImageResource(imgRes)
            1 -> binding.ilCoupon2.btLike.setImageResource(imgRes)
            2 -> binding.ilCoupon3.btLike.setImageResource(imgRes)
        }

    }

    private fun forcePlaceLikeState(order: Int){
        val isLike = viewModel.isPlaceInFavList(order)
        val imgRes = if (isLike) R.drawable.ic_heart_unlike else R.drawable.ic_heart_like
        when(order){
            0 -> binding.ilPlace1.btLike.setImageResource(imgRes)
            1 -> binding.ilPlace2.btLike.setImageResource(imgRes)
            2 -> binding.ilPlace3.btLike.setImageResource(imgRes)
        }
    }

    private fun bindViewModel() {
        viewModel.eventToast.observe(viewLifecycleOwner) { toast(it) }

        viewModel.eventMessage.observe(viewLifecycleOwner) { showMessageDialog(it) }

        viewModel.currentLocation.observe(viewLifecycleOwner) {
            if (viewModel.isCouponDetailVisible.value != null ||
                viewModel.isPlaceDetailVisible.value != null
            ) {
                return@observe
            }
            if (viewModel.currentDataSource.value == MainTabViewModel.DataSource.Location) {
                updateLocation(it)
//
//                val sharedPref = requireContext().getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
//                val isFirstInstall = sharedPref.getBoolean("first_install", true)
//                Timber.d("AAAAAAAAAAAAAAAAAAAAAAAA:" + isFirstInstall)
//
//                if (!isFirstInstall) {
//                    // 这是第一次安装应用程序
//                    // 执行您希望在第一次安装时执行的操作
//
//                    // 将值更改为 TRUE，以便下次不再执行
//                    val editor = sharedPref.edit()
//                    editor.putBoolean("first_install", true)
//                    editor.apply()
//                    // 例如，启动登录页
//                    val intent = Intent(requireContext(), SplashActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                    startActivity(intent)
//
//                }
            }
        }

        viewModel.displayCoupon.observe(viewLifecycleOwner) { updateCoupon(it) }

        viewModel.displayPlace.observe(viewLifecycleOwner) { updatePlace(it) }

        viewModel.shortcutCoupon1LikeState.observe(viewLifecycleOwner) {
            binding.ilCoupon1.btLike.setImageResource(if (it) R.drawable.ic_heart_like else R.drawable.ic_heart_unlike)
        }

        viewModel.shortcutCoupon2LikeState.observe(viewLifecycleOwner) {
            binding.ilCoupon2.btLike.setImageResource(if (it) R.drawable.ic_heart_like else R.drawable.ic_heart_unlike)
        }

        viewModel.shortcutCoupon3LikeState.observe(viewLifecycleOwner) {
            binding.ilCoupon3.btLike.setImageResource(if (it) R.drawable.ic_heart_like else R.drawable.ic_heart_unlike)
        }

        viewModel.shortcutPlace1LikeState.observe(viewLifecycleOwner) {
            binding.ilPlace1.btLike.setImageResource(if (it) R.drawable.ic_heart_like else R.drawable.ic_heart_unlike)
        }

        viewModel.shortcutPlace2LikeState.observe(viewLifecycleOwner) {
            binding.ilPlace2.btLike.setImageResource(if (it) R.drawable.ic_heart_like else R.drawable.ic_heart_unlike)
        }

        viewModel.shortcutPlace3LikeState.observe(viewLifecycleOwner) {
            binding.ilPlace3.btLike.setImageResource(if (it) R.drawable.ic_heart_like else R.drawable.ic_heart_unlike)
        }

        viewModel.displayCouponPlaceBounds.observe(viewLifecycleOwner) { updateMapBounds(it) }

        viewModel.eventShowCouponList.observe(viewLifecycleOwner) {
            childFragmentManager.commit {
                setCustomAnimations(R.anim.fade_int, R.anim.fade_out)
                add(R.id.fl_coupon_list, ShouCouponListFragment(), ShouCouponListFragment.TAG)
            }
        }

        viewModel.eventShowCouponDetail.observe(viewLifecycleOwner) {
            childFragmentManager.commit {
                setCustomAnimations(R.anim.fade_int, R.anim.fade_out)
                add(
                    R.id.fl_coupon_detail,
                    ShouCouponDetailFragment.newInstance(it),
                    ShouCouponDetailFragment.TAG
                )
            }
        }

        viewModel.eventShowPlaceList.observe(viewLifecycleOwner) {
            childFragmentManager.commit {
                setCustomAnimations(R.anim.fade_int, R.anim.fade_out)
                add(R.id.fl_place_list, ShouPlaceListFragment(), ShouPlaceListFragment.TAG)
            }
        }

        viewModel.eventShowPlaceDetail.observe(viewLifecycleOwner) {
            childFragmentManager.commit {
                setCustomAnimations(R.anim.fade_int, R.anim.fade_out)
                add(
                    R.id.fl_place_list,
                    ShouPlaceDetailFragment.newInstance(it),
                    ShouPlaceDetailFragment.TAG
                )
            }
        }

        viewModel.currentTravelCount.observe(viewLifecycleOwner) { count ->
            binding.ilTravelState.flTravelCount.isVisible = count > 0
            binding.ilTravelState.tvTravelCount.text = "$count"
        }

        viewModel.eventShowMyTravel.observe(viewLifecycleOwner) {
            childFragmentManager.commit {
                setCustomAnimations(R.anim.fade_int, R.anim.fade_out)
                add(R.id.fl_full_tab, MyTravelFragment(), MyTravelFragment.TAG)
            }
        }

        viewModel.eventRouteTravel.observe(viewLifecycleOwner) { (location, waypoints) ->
            location ?: return@observe
            if (waypoints.isNullOrEmpty()) return@observe
            RouteUtil.startRoute(requireActivity(), location, waypoints)
        }

        mainViewModel.eventOpenNearStore.observe(viewLifecycleOwner) { tag ->
            Timber.d("eventOpenNearStore")
            var frag = childFragmentManager.findFragmentByTag(VoiceStoreListFragment.TAG) as? VoiceStoreListFragment
            if (frag == null) {
                childFragmentManager.commit {
                    // 若使用 replace 會使得 showShortCut 狀態改變，使得優惠券或商家閃一下
                    add(
                        R.id.fl_voice_store_list,
                        VoiceStoreListFragment.newInstance(tag),
                        VoiceStoreListFragment.TAG
                    )
                }
            } else {
                frag.setVoiceTag(tag)
            }
        }

        mainViewModel.eventSearchAreaCoupon.observe(viewLifecycleOwner) { cityTown ->
            viewModel.setDataSource(MainTabViewModel.DataSource.Area(cityTown))
        }

        viewModel.eventShowRequireLogin.observe(viewLifecycleOwner) {
            childFragmentManager.commit {
                val fragment =
                    RequireLoginFragment.newInstance(l = object : RequireLoginFragment.Listener {
                        override fun noLogin() {
                            viewModel.onRequireLoginResultNoLogin()
                        }
                    })
                add(R.id.fl_full_tab, fragment, RequireLoginFragment.TAG)
                addToBackStack("")
            }
        }

        viewModel.eventShowRequireNavigationParking.observe(viewLifecycleOwner) { waypoint ->
            childFragmentManager.commit {
                val fragment = RequireNavigationParkFragment.newInstance(
                    waypoint.name,
                    object : RequireNavigationParkFragment.Listener {
                        override fun onRequireNavigationParkResult(requirePark: Boolean) {
                            viewModel.addTravel(waypoint, requirePark)
                        }
                    })
                add(R.id.fl_full_tab, fragment, RequireNavigationParkFragment.TAG)
            }
        }

        viewModel.isTopMenuContentVisible.observe(viewLifecycleOwner) {
            binding.ilTopMenuContent.root.isVisible = it
        }

        viewModel.eventShowSelectDataSource.observe(viewLifecycleOwner) { source ->
            when (source) {
                Area -> {
                    childFragmentManager.removeByTag(SelectDestFragment.TAG)
                    childFragmentManager.removeByTag(VoiceStoreListFragment.TAG)
                    childFragmentManager.commit {
                        add(R.id.fl_full_tab, SelectAreaFragment(), SelectAreaFragment.TAG)
                    }
                }
                Location -> {
                    childFragmentManager.removeByTag(SelectDestFragment.TAG)
                    childFragmentManager.removeByTag(SelectAreaFragment.TAG)
                    viewModel.setDataSource(MainTabViewModel.DataSource.Location)
                }
                Dest -> {
                    childFragmentManager.removeByTag(SelectAreaFragment.TAG)
                    childFragmentManager.commit {
                        add(R.id.fl_full_tab, SelectDestFragment(), SelectDestFragment.TAG)
                    }
                }
                null -> {
                }
            }
        }

        viewModel.currentDataSource.observe(viewLifecycleOwner) { dataSource ->
            binding.ilTopMenuContent.ivByArea.isVisible = false
            binding.ilTopMenuContent.ivByLocation.isVisible = false
            binding.ilTopMenuContent.ivByDest.isVisible = false

            childFragmentManager.removeByTag(SelectDestFragment.TAG)
            childFragmentManager.removeByTag(SelectAreaFragment.TAG)

            when (dataSource) {
                is MainTabViewModel.DataSource.Area -> {
                    binding.ilTopMenuContent.ivByArea.isVisible = true
                    binding.ilTopMenu.tvMore.text = "地區優惠推薦"
                    binding.llDatasourceNote.isVisible = true
                    binding.tvDataSourceNote.text =
                        "${dataSource.cityTown.city}${dataSource.cityTown.town}"
                }

                MainTabViewModel.DataSource.Location -> {
                    binding.ilTopMenuContent.ivByLocation.isVisible = true
                    binding.ilTopMenu.tvMore.text = "附近優惠推薦"
                    binding.llDatasourceNote.isVisible = false
                    binding.tvDataSourceNote.text = ""
                }

                is MainTabViewModel.DataSource.Dest -> {
                    binding.ilTopMenuContent.ivByDest.isVisible = true
                    binding.ilTopMenu.tvMore.text = "目的地優惠推薦"
                    binding.llDatasourceNote.isVisible = true
                    binding.tvDataSourceNote.text =
                        "${dataSource.cityTown.city}${dataSource.cityTown.town}${dataSource.address}"
                }
            }

        }

        viewModel.isCouponDetailVisible.observe(viewLifecycleOwner) {
            if (it != null) {
                markerHighlight = googleMap.addCouponMarker(it, true)
                val latlng = LatLng(it.place.lat, it.place.lng)
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
                googleMap.moveCamera(CameraUpdateFactory.scrollBy(-200f, 0f))
                childFragmentManager.removeByTag(ShouPlaceDetailFragment.TAG)
            } else {
                markerHighlight?.remove()
            }
        }

        viewModel.isPlaceDetailVisible.observe(viewLifecycleOwner) {
            if (it != null) {
                markerHighlight = googleMap.addPlaceMarker(it, true)
                val latlng = LatLng(it.lat, it.lng)
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
                googleMap.moveCamera(CameraUpdateFactory.scrollBy(200f, 0f))
                childFragmentManager.removeByTag(ShouCouponDetailFragment.TAG)
            } else {
                markerHighlight?.remove()
            }
        }

        viewModel.notificationNewCoupon.observe(viewLifecycleOwner) { notification ->
            notification ?: return@observe
            showNewCouponNotification(notification)
        }

        viewModel.isShowShortCut.observe(viewLifecycleOwner) { show ->
            binding.run {
                val alpha = if (show) 1f else 0f
                llCoupon.isVisible = show
                llCoupon.animate()
                    .setDuration(500)
                    .alpha(alpha)
                    .start()

                llPlace.isVisible = show
                llPlace.animate()
                    .setDuration(500)
                    .alpha(alpha)
                    .start()
            }
        }

        viewModel.eventShowCouponsByPlaceId.observe(viewLifecycleOwner) { list ->
            childFragmentManager.commit {
                val fragment = CouponListByPlaceFragment.newInstance(list)
                setCustomAnimations(R.anim.fade_int, R.anim.fade_out)
                add(R.id.fl_full_tab, fragment, CouponListByPlaceFragment.TAG)
            }
        }

        viewModel.eventShowSelectTravelToAdd.observe(viewLifecycleOwner) { waypoint ->
            childFragmentManager.commit {
                val fragment = SelectTravelToAddFragment.newInstance(waypoint)
                setCustomAnimations(R.anim.fade_int, R.anim.fade_out)
                add(R.id.fl_full_tab, fragment, SelectTravelToAddFragment.TAG)
            }
        }

        viewModel.showLoading.observe(viewLifecycleOwner){
            if (it) showLoading() else hideLoading()
        }
    }

    override fun onNightModeChange(isNight: Boolean) {
        //設定地圖深色模式
        if (this::googleMap.isInitialized){
            val styleOptions = when(isNight){
                true -> MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style_dark)
                false -> null
            }
            googleMap.setMapStyle(styleOptions)
            markerMe?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_me))
        }

        binding.ilTopMenu.refresh()
        binding.ilTopMenuContent.refresh()

        binding.tvDataSourceNote.setTextColor(getColor(R.color.color_secondary))
        binding.ivEditSource.setImageResource(R.drawable.ic_edit)

        binding.flNewCouponNotification.background = getDrawable(R.drawable.bg_notification)
        binding.tvNewCouponTitle.setTextColor(getColor(R.color.color_primary))

    }

    private fun showNewCouponNotification(notification: NewCouponNotification) {
        binding.tvNewCouponTitle.text = notification.msg
        binding.flNewCouponNotification.isVisible = true
        lifecycleScope.launch {
            delay(5000L)
            if (binding.flNewCouponNotification.isVisible) {
                binding.tvCancelNewCoupon.callOnClick()
            }
        }
    }

    private fun updateMapBounds(locations: List<LatLng>?) {
        locations ?: return
        if (locations.isEmpty()) return

        if (!this::googleMap.isInitialized) return

        if (locations.size > 1) {
            val boudBuilder = LatLngBounds.builder()

            locations.forEach {
                boudBuilder.include(it)
            }

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boudBuilder.build(), 10))

        } else {
            val latlng = locations.first()!!
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f))
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        googleMap.setMinZoomPreference(10f)
        googleMap.setMaxZoomPreference(17f)

        googleMap.setOnMapLoadedCallback {
            (requireActivity() as? MainActivity)?.onMapLoaded()
        }

        when (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.map_style_dark
                    )
                )
            }
            Configuration.UI_MODE_NIGHT_NO -> {
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
            }
        }

        googleMap.showTaiwan()
        googleMap.setOnCameraMoveListener {
            if (initLocationFinish){
                currentZoomLevel = googleMap.cameraPosition.zoom
            }
        }

        viewModel.currentLocation.value?.let { updateLocation(it) }

        viewModel.displayCoupon.value?.let { updateCoupon(it) }

        viewModel.displayPlace.value?.let { updatePlace(it) }

    }

    private var firstInitLocation = true
    private var initLocationFinish = false
    private var lastLocation: Location? = null
    private var lastSetBearing: Float = 0.0f

    private fun updateLocation(location: Location) {
        if (!this::googleMap.isInitialized) return

        if (firstInitLocation) {
            googleMap.centerCamera(location, zoomLevel = 16f, withAnim = false)
            currentZoomLevel = 16f
            firstInitLocation = false
            initLocationFinish = true
        } else {
            val mapPositionBuilder = CameraPosition.builder()
                .target(location.toLatLng())
                .zoom(currentZoomLevel)

            if (lastLocation == null){
                mapPositionBuilder.bearing(location.bearing)
                lastSetBearing = location.bearing
            }else{
                val distance = lastLocation!!.distanceTo(location)
                if (distance > 4 && location.bearingAccuracyDegrees <= 8 ){
                    mapPositionBuilder.bearing(location.bearing)
                    lastSetBearing = location.bearing
                }else{
                    mapPositionBuilder.bearing(lastSetBearing)
                }
            }

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(mapPositionBuilder.build()))
        }

        if (markerMe != null) {
            markerMe?.rotation = lastSetBearing
            animateMarker(markerMe!!.position, location)
        } else {
            markerMe = googleMap.addMeMarker(location)
            markerMe!!.setAnchor(0.5f, 0.5f)
            markerMe!!.isFlat = true
        }
        lastLocation = location
    }

    private fun animateMarker(start: LatLng, end: Location) {
        (markerMe!!.tag as? Animator)?.let { it.cancel() }
        val startValues = doubleArrayOf(start.latitude, start.longitude)
        val endValues = doubleArrayOf(end.latitude, end.longitude)
        val animator = ValueAnimator.ofObject(DoubleArrayEvaluator(), startValues, endValues)
        animator.duration = 2000
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            (it.animatedValue as DoubleArray).let { animatedValue ->
                markerMe!!.position = LatLng(animatedValue[0], animatedValue[1])
            }
        }
        markerMe!!.tag = animator
        animator.start()
    }

    private fun updateCoupon(coupons: List<ShouCoupon>) {

        if (!this::googleMap.isInitialized) return

        binding.ilNoOtherCoupon.root.isVisible = coupons.size < 3 && coupons.isNotEmpty()

        binding.ilCoupon1.root.isVisible = false
        binding.ilCoupon2.root.isVisible = false
        binding.ilCoupon3.root.isVisible = false
        binding.ilCouponMore.root.isVisible = false

        otherStoreMarkerList.forEach { it.remove() }
        otherStoreMarkerList.clear()

        coupons.forEachIndexed { index, coupon ->

            when (index) {
                0 -> {
                    binding.ilCoupon1.root.isVisible = true
                    binding.ilCoupon1.set(coupon)
                    markerStore1?.remove()
                    markerStore1 = googleMap.addCouponMarker(coupon)
                }
                1 -> {
                    binding.ilCoupon2.root.isVisible = true
                    binding.ilCoupon2.set(coupon)
                    markerStore2?.remove()
                    markerStore2 = googleMap.addCouponMarker(coupon)
                }
                2 -> {
                    binding.ilCoupon3.root.isVisible = true
                    binding.ilCoupon3.set(coupon)
                    markerStore3?.remove()
                    markerStore3 = googleMap.addCouponMarker(coupon)
                }
                else -> {
                    otherStoreMarkerList.add(googleMap.addCouponMarker(coupon))
                }
            }

        }

        binding.ilCouponMore.root.isVisible = coupons.size > 3
    }

    private fun updatePlace(places: List<ShouPlace>) {

        if (!this::googleMap.isInitialized) return

        binding.ilNoOtherPlace.root.isVisible = places.size < 3 && places.isNotEmpty()

        binding.ilPlace1.root.isVisible = false
        binding.ilPlace2.root.isVisible = false
        binding.ilPlace3.root.isVisible = false
        binding.ilPlaceMore.root.isVisible = false

        otherPlaceMarkerList.forEach { it.remove() }
        otherPlaceMarkerList.clear()

        places.forEachIndexed { index, shouPlace ->

            when (index) {
                0 -> {
                    binding.ilPlace1.root.isVisible = true
                    binding.ilPlace1.set(shouPlace)
                    markerPlace1?.remove()
                    markerPlace1 = googleMap.addPlaceMarker(shouPlace)
                }
                1 -> {
                    binding.ilPlace2.root.isVisible = true
                    binding.ilPlace2.set(shouPlace)
                    markerPlace2?.remove()
                    markerPlace2 = googleMap.addPlaceMarker(shouPlace)
                }
                2 -> {
                    binding.ilPlace3.root.isVisible = true
                    binding.ilPlace3.set(shouPlace)
                    markerPlace3?.remove()
                    markerPlace3 = googleMap.addPlaceMarker(shouPlace)
                }
                else -> {
                    otherStoreMarkerList.add(googleMap.addPlaceMarker(shouPlace))
                }
            }

        }

        binding.ilPlaceMore.root.isVisible = places.size > 3
    }

    private fun addVoiceStoreMarkers(stores: List<VoiceStore>) {
        if (!this::googleMap.isInitialized) return

        voiceStoreMarkerList.forEach { it.remove() }
        voiceStoreMarkerList.clear()
        stores.forEach { voiceStoreMarkerList.add(googleMap.addVoiceStoreMarker(it)) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private fun ItemCouponBinding.set(coupon: ShouCoupon) {
//    Timber.d("setCoupon id:${coupon.couponId}, name:${coupon.name}")
    root.tag = coupon

    ivCoupon.loadImage(coupon.image)
    tvTitle.text = coupon.place.name
    tvDesc.text = coupon.name
}

private fun ItemPlaceBinding.set(place: ShouPlace) {
//    Timber.d("setPlace id:${place.placeId}, name:${place.name}")

    root.tag = place

    ivCoupon.loadImage(place.image)
    tvTitle.text = place.name

}

