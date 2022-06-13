package kim.chopper.direction

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.kakao.sdk.navi.NaviClient
import kim.chopper.direction.data.model.Route
import kim.chopper.direction.databinding.FragmentMapsBinding
import kim.chopper.direction.ui.login.LoginActivity
import net.daum.mf.map.api.*
import net.daum.mf.map.api.MapPOIItem.ImageOffset

class MapsFragment : Fragment(), MapView.MapViewEventListener {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private var _mapView: MapView? = null
    private val mapView get() = _mapView!!

    private var viewFlag: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        _mapView = binding.mapView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (NaviClient.instance.isKakaoNaviInstalled(requireContext())) {
            Log.i(TAG, "카카오내비 앱으로 길 안내 가능")
        } else {
            Log.i(TAG, "카카오내비 미설치")
        }

        binding.endText.setOnEditorActionListener { _, action, _ ->
            var handled = false
            if (action == EditorInfo.IME_ACTION_DONE) {
                displayRoute(binding.startText.text.toString(), binding.endText.text.toString())
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.endText.windowToken, 0)
                handled = true
            }
            handled
        }
        binding.fab.setOnClickListener {
            val tempStr = binding.startText.text
            binding.startText.text = binding.endText.text
            binding.endText.text = tempStr
        }
        mapView.setMapViewEventListener(this)

    }

    companion object {
        private const val TAG = "MapsFragment"
        private val routeDefault: List<Route> = mutableListOf(
            Route(R.drawable.custom_poi_marker_start, "대전시청", "", "", ""),
            Route(R.drawable.custom_kick_scooter, "킥보드", "대전역", "10분", "1,200원"),
            Route(R.drawable.custom_railway, "기차", "대전 → 서울", "1시간 20분", "15,800원"),
            Route(R.drawable.ic_kick_scooter, "킥보드", "서울시청", "4분", "7,800원"),
            Route(R.drawable.custom_poi_marker_end, "서울시청", "", "총금액", "24,800원"),
            )
        private val route1: List<Route> = mutableListOf(
            Route(R.drawable.custom_poi_marker_start, "울산시청", "", "", ""),
            Route(R.drawable.custom_kick_scooter, "킥보드", "울산시청 → 울산터미널", "10분", "1,200원"),
            Route(R.drawable.custom_bus, "시외버스", "울산터미널 → 진주터미널", "2시간 30분", "15,800원"),
            Route(R.drawable.custom_bus, "시외버스", "진주터미널 → 함양터미널", "49분", "7,800원"),
            Route(R.drawable.custom_rental_car, "렌터카(쏘카)", "함양터미널 → 대봉산자연휴양림", "10분", "1,500원"),
            Route(R.drawable.custom_poi_marker_end, "대봉산자연휴양림", "", "총금액", "26,300원"),
        )
        private val route2: List<Route> = mutableListOf(
            Route(R.drawable.custom_poi_marker_start, "", "대전시청", "", ""),
            Route(R.drawable.custom_subway, "지하철", "대전시청역 → 서대전역", "6분", "1,250원"),
            Route(R.drawable.custom_kick_scooter, "킥보드", "서대전역 → 서대전KTX", "6분", "12,000원"),
            Route(R.drawable.custom_railway, "기차", "서대전KTX → 정읍KTX", "1시간 28분", "12,100원"),
            Route(R.drawable.custom_rental_car, "렌터카(롯데카)", "정읍KTX → 법성포굴비정식", "50분", "8,400원"),
            Route(R.drawable.custom_poi_marker_end, "법성포굴비정식", "", "총금액", "33,750원"),
        )
        private val route3: List<Route> = mutableListOf(
            Route(R.drawable.custom_poi_marker_start, "메가박스 대전중앙로", "", "", ""),
            Route(R.drawable.custom_kick_scooter, "킥보드", "메가박스 → 대전KTX", "3분", "1,200원"),
            Route(R.drawable.custom_railway, "기차", "대전KTX → 부산KTX", "1시간 29분", "30,400원"),
            Route(R.drawable.custom_kick_scooter, "킥보드", "부산KTX → 초량밀면", "1분", "1,200"),
            Route(R.drawable.custom_poi_marker_end, "초량밀면", "", "총금액", "32,800원"),
        )
    }

    private fun displayRoute(start: String?, end: String?) {
        // 이미 지정된 장소의 좌표
        val route11 = getMapPOIItem("Route11", 10001, 35.537315, 129.326751, R.drawable.custom_kick_scooter)
        val route12 = getMapPOIItem("Route12", 10002, 35.357143, 128.744979, R.drawable.custom_bus)
        val route13 = getMapPOIItem("Route13", 10003, 35.328777, 127.923149, R.drawable.custom_bus)
        val route14 = getMapPOIItem("Route14", 10004, 35.551906, 127.725799, R.drawable.custom_rental_car)
        val route21 = getMapPOIItem("Route21", 20001, 36.335002, 127.399304, R.drawable.custom_subway)
        val route22 = getMapPOIItem("Route22", 20002, 36.322281, 127.408258, R.drawable.custom_kick_scooter)
        val route23 = getMapPOIItem("Route23", 20003, 35.974136, 127.151580, R.drawable.custom_railway)
        val route24 = getMapPOIItem("Route24", 20004, 35.466026, 126.653182, R.drawable.custom_rental_car)
        val route31 = getMapPOIItem("Route31", 30001, 36.329936, 127.429847, R.drawable.custom_kick_scooter)
        val route32 = getMapPOIItem("Route32", 30002, 35.762645, 128.170166, R.drawable.custom_railway)
        val route33 = getMapPOIItem("Route33", 30003, 35.116325, 129.040069, R.drawable.custom_kick_scooter)

        mapView.removeAllPolylines()
        mapView.removeAllPOIItems()

        val geocoder = Geocoder(requireContext())

        val startList = geocoder.getFromLocationName(start, 1)
        if (startList.size == 0) {
            Toast.makeText(requireContext(), "입력하신 출발지의 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            binding.startText.requestFocus()
            return
        }
        val poiItemStart = getMapPOIItem("Start", 1001, startList[0].latitude, startList[0].longitude, R.drawable.custom_poi_marker_start)
        mapView.addPOIItem(poiItemStart)

        val endList = geocoder.getFromLocationName(end, 1)
        if (endList.size == 0) {
            Toast.makeText(requireContext(), "입력하신 목적지의 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            binding.endText.requestFocus()
            return
        }
        val poiItemEnd = getMapPOIItem("End", 1002, endList[0].latitude, endList[0].longitude, R.drawable.custom_poi_marker_end)
        mapView.addPOIItem(poiItemEnd)

        val polyline = MapPolyline(21)
        polyline.tag = 2000
        polyline.lineColor = Color.argb(128, 0, 0, 255)

        var polylinePoints: Array<MapPoint> = when {
            binding.startText.text.toString().contains("울산") && binding.endText.text.toString().contains("대봉산") -> arrayOf(
                poiItemStart.mapPoint,
                MapPoint.mapPointWithGeoCoord(35.536582, 129.339690),//울산시외버스터미널
                MapPoint.mapPointWithGeoCoord(35.191255, 128.089376),//진주시외버스터미널
                MapPoint.mapPointWithGeoCoord(35.521099, 127.732956),//함양시외버스터미널
                poiItemEnd.mapPoint
            )
            binding.startText.text.toString().contains("대전") && binding.endText.text.toString().contains("법성포") -> arrayOf(
                poiItemStart.mapPoint,
                MapPoint.mapPointWithGeoCoord(36.322271, 127.411664),//서대전역
                MapPoint.mapPointWithGeoCoord(36.322593, 127.403062),//서대전KTX
                MapPoint.mapPointWithGeoCoord(35.575861, 126.843018),//정읍KTX
                poiItemEnd.mapPoint
            )
            binding.startText.text.toString().contains("메가박스") && binding.endText.text.toString().contains("초량밀면") -> arrayOf(
                poiItemStart.mapPoint,
                MapPoint.mapPointWithGeoCoord(36.332303, 127.434267),//대전KTX
                MapPoint.mapPointWithGeoCoord(35.114973, 129.039700),//부산KTX
                poiItemEnd.mapPoint
            )
            else -> arrayOf(
                poiItemStart.mapPoint,
                poiItemEnd.mapPoint
            )
        }

        polyline.addPoints(polylinePoints)
        mapView.addPolyline(polyline)
        val mapPointBounds = MapPointBounds(polylinePoints)
        val padding = 180 // px

        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding))

        val animation =
            TranslateAnimation(0f, 0f, 0f, -binding.searchLayout.height.toFloat())
        animation.duration = 500
        animation.fillAfter = true
        binding.searchLayout.startAnimation(animation)

        val result = CustomResult(requireContext())
        val myRoute = when {
            binding.startText.text.toString().contains("울산") && binding.endText.text.toString().contains("대봉산") -> {
                result.setRouteName("추천경로")
                result.setTime("3시간 39분")
                result.setPrice("26,300원")
                mapView.addPOIItem(route11)
                mapView.addPOIItem(route12)
                mapView.addPOIItem(route13)
                mapView.addPOIItem(route14)
                route1
            }
            binding.startText.text.toString().contains("대전") && binding.endText.text.toString().contains("법성포") -> {
                result.setRouteName("추천경로")
                result.setTime("2시간 20분")
                result.setPrice("33,750원")
                mapView.addPOIItem(route21)
                mapView.addPOIItem(route22)
                mapView.addPOIItem(route23)
                mapView.addPOIItem(route24)
                route2
            }
            binding.startText.text.toString().contains("메가박스") && binding.endText.text.toString().contains("초량밀면") -> {
                result.setRouteName("추천경로")
                result.setTime("1시간 33분")
                result.setPrice("32,800원")
                mapView.addPOIItem(route31)
                mapView.addPOIItem(route32)
                mapView.addPOIItem(route33)
                route3
            }
            else -> {
                result.setRouteName("추천경로")
                result.setTime("준비중")
                result.setPrice("준비중")
                routeDefault
            }
        }
        binding.resultLayout.removeAllViews()
        binding.resultLayout.addView(result)

        result.setOnClickListener {
            if (myRoute != routeDefault) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("상세경로")
                    .setAdapter(RouteAdapter(requireContext(), myRoute), null)
                    .setPositiveButton("예약", DialogInterface.OnClickListener { _, _ ->
                        val confirm = AlertDialog.Builder(requireContext())
                        confirm.setTitle("공지")
                            .setMessage("예약을 하시려면 로그인이 필요합니다. 로그인하시겠습니까?")
                            .setPositiveButton("로그인", DialogInterface.OnClickListener { _, _ ->
                                startActivity(Intent(requireContext(), LoginActivity::class.java))
                                reset()
                            })
                            .setNegativeButton("취소", null)
                        confirm.create()
                        confirm.show()
                    })
                    .setNegativeButton("취소", null)
                builder.create()
                builder.show()
            }
        }

        binding.resultLayout.visibility = View.VISIBLE
        viewFlag = true

    }

    private fun reset() {
        binding.startText.setText("")
        binding.endText.setText("")

        mapView.removeAllPOIItems()
        mapView.removeAllPolylines()

        binding.resultLayout.visibility = View.GONE
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(36.366516, 127.344342),
            3,
            true)
    }

    override fun onMapViewInitialized(p0: MapView?) {
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        if (viewFlag) {
            val animation =
                TranslateAnimation(0f, 0f, -binding.searchLayout.height.toFloat(), 0f)
            animation.duration = 500
            animation.fillAfter = true
            binding.searchLayout.startAnimation(animation)
            viewFlag = false
        } else {
            val animation =
                TranslateAnimation(0f, 0f, 0f, -binding.searchLayout.height.toFloat())
            animation.duration = 500
            animation.fillAfter = true
            binding.searchLayout.startAnimation(animation)
            viewFlag = true
        }
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
        Log.d(TAG, "${p1!!.mapPointGeoCoord.latitude}, ${p1!!.mapPointGeoCoord.longitude}")
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
    }

    private fun getMapPOIItem(itemName: String, tag: Int, latitude: Double, longitude: Double, imageResource: Int): MapPOIItem {
        val item = MapPOIItem() // 대전시청 - 서대전역 가운데
        item.itemName = itemName
        item.tag = tag
        item.mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
        item.markerType = MapPOIItem.MarkerType.CustomImage
        item.showAnimationType = MapPOIItem.ShowAnimationType.SpringFromGround
        item.isShowCalloutBalloonOnTouch = false
        item.customImageResourceId = imageResource
        item.customImageAnchorPointOffset = ImageOffset(29, 2)
        return item
    }

}