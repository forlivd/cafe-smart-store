package com.ssafy.smartstore

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.ssafy.smartstore.databinding.ActivityMapBinding
import com.ssafy.smartstore.network.BeaconResultActivity
import kotlinx.coroutines.delay
import org.altbeacon.beacon.*
import java.io.IOException
import java.util.*


private const val TAG = "MapActivityTAG"

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    BeaconConsumer {
    private lateinit var binding: ActivityMapBinding

    private var src_lat = 0.0f
    private var src_lng = 0.0f

    private var mMap: GoogleMap? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var mCurrentLocation: Location
    private lateinit var currentPosition: LatLng
    private var storeMarker: Marker? = null

    //beacon
    private lateinit var bt: Button
    private var beaconManager: BeaconManager? = null
    private val region = Region("altbeacon", null, null, null)
    private var finish = 0
    private val TAG = "bc"

    //Permissions
    private val PERMISSIONS_CODE = 100

    // 모든 퍼미션 관련 배열
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    // 감지된 비콘들을 임시로 담을 리스트
    private val beaconList: MutableList<Beacon> = ArrayList()
    var textView: TextView? = null

    //bluetooth
    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var needBLERequest = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buttonInit()

        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0L
            smallestDisplacement = 5.0f
            fastestInterval = 0L
        }

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnFind.setOnClickListener {
            val url =
                "nmap://route/public?dlat=36.10830144233874&dlng=128.41827450414362&dname=${
                    getAddress(
                        LatLng(36.10830144233874, 128.41827450414362)
                    )
                }&appname=com.ssafy.smartstore"
            // 목적지 주소만 입력
            // 출발지 주소는 네이버지도에서 자동으로 현재위치 지정

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addCategory(Intent.CATEGORY_BROWSABLE)

            val list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

            // 네이버지도 앱이 없다면
//            if (list == null || list.isEmpty()) {
//                context.startActivity(
//                    Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse("market://details?id=com.nhn.android.nmap")
//                    ).addFlags(FLAG_ACTIVITY_NEW_TASK)
//                )
//            }
//            // 네이버지도 앱이 있다면
//            else {
            context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK))// 네이버지도가 설치되어 있다면 이 코드만 있으면 됨
//            }
        }

        //beacon-----------------------------------------------------------------------------------------------------------------
        //권한 설정
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                PERMISSIONS_CODE
            )
        }

        //블루투스 매니저
        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        // 실제로 비콘을 탐지하기 위한 비콘매니저 객체를 초기화
        beaconManager = BeaconManager.getInstanceForApplication(this)

        // 기기에 따라서 setBeaconLayout 안의 내용을 바꿔줘야 하는듯
        beaconManager!!.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"))

        // 블루투스 Enable 확인
        if (!isEnableBLEService()) { // ble 사용 중인지 확인
            requestEnableBLE() // ble 사용 하도록 요청
            Log.d(TAG, "startScan: 블루투스가 켜지지 않았습니다.")
            return
        } else {
            startScan()
        }

    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        if (checkPermission()) { // 1. 위치 퍼미션을 가지고 있는지 확인
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식)
            startLocationUpdates() // 3. 위치 업데이트 시작

        } else {  //2. 권한이 없다면
            // 3-1. 사용자가 권한이 없는 경우에는
            val permissionListener = object : PermissionListener {
                // 권한 얻기에 성공했을 때 동작 처리
                override fun onPermissionGranted() {
                    startLocationUpdates()
                }

                // 권한 얻기에 실패했을 때 동작 처리
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(
                        this@MapActivity,
                        "위치 권한이 거부되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            TedPermission.create()
                .setPermissionListener(permissionListener)
                .setDeniedMessage("[설정] 에서 위치 접근 권한을 부여해야만 사용이 가능합니다.")
                .setPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .check()
        }

        // 상점 위치 마커 표시 (메가박스 구미 근처)
        val storeLatLng = LatLng(36.10830144233874, 128.41827450414362)

        val markerOptions = MarkerOptions()
        markerOptions.position(storeLatLng)
        markerOptions.draggable(true)

        storeMarker = mMap!!.addMarker(markerOptions)
        mMap!!.setOnMarkerClickListener(this)
    }

    fun setCurrentLocation(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 11f)
        mMap!!.animateCamera(cameraUpdate)
    }

    // 현재위치 찾아서 마커찍는 함수
    private fun startLocationUpdates() {
        if (checkPermission()) {
            mFusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()!!
            )
            // 현재 위치 찾는 버튼 활성화
            if (mMap != null) mMap!!.isMyLocationEnabled = true
            // +.- 줌 버튼 활성화
            if (mMap != null) mMap!!.uiSettings.isZoomControlsEnabled = true
        }
    }

    // 콜백 함수
    var locationCallback: LocationCallback = object : LocationCallback() {
        // 위치가 바뀔때마다, 일정 시간마다 호출됨
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            // 위치 정보들이 리스트에 담김
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                // 리스트에서 가장 최근 위치 가져옴
                val location = locationList[locationList.size - 1]
                currentPosition = LatLng(location.latitude, location.longitude)
                src_lat = location.latitude.toFloat()
                src_lng = location.latitude.toFloat()

                var srcLoc = Location("src")
                srcLoc.latitude = src_lat.toDouble()
                srcLoc.longitude = src_lng.toDouble()

                Log.d("거리", "$src_lat, $src_lng")

                var destLoc = Location("dest")
                destLoc.latitude = 36.10830144233874
                destLoc.longitude = 128.41827450414362

                var distance = srcLoc.distanceTo(destLoc).toInt()
                var msg = ""

                if (distance > 1000) {
                    distance /= 1000
                    msg = "싸피벅스까지의 거리는 ${distance}km입니다."
                } else {
                    msg = "싸피벅스까지의 거리는 ${distance}m입니다."
                }
                binding.tvDistance.text = msg

                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location)
                mCurrentLocation = location

            }
        }
    }

    private fun checkPermission(): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    fun getAddress(latlng: LatLng): String {
        //지오코더: GPS를 주소로 변환
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        try {
            addresses = geocoder.getFromLocation(
                latlng.latitude,
                latlng.longitude,
                1
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }

        return if (addresses == null || addresses.isEmpty()) {
            Toast.makeText(this, "주소 발견 불가", Toast.LENGTH_LONG).show()
            "주소 발견 불가"
        } else {
            val address = addresses[0]
            address.getAddressLine(0).toString()
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        val mDialog = LayoutInflater.from(this).inflate(R.layout.map_dialog, null)
        val mBuilder = AlertDialog.Builder(this).setView(mDialog)
        val mAlertDialog = mBuilder.show()
        return true
    }

    private fun buttonInit() {
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.none, R.anim.horizon_exit)
        }

        // 홈버튼
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.horizon_enter, R.anim.none)
        }
    }


    //beacon --------------------------------------------------------------------------------------------
    // Beacon Scan 시작
    private fun startScan() {
        Log.d(TAG, "startScan")
        // 블루투스 Enable 확인
        if (!isEnableBLEService()) { // ble 사용 중인지 확인
            requestEnableBLE() // ble 사용 하도록 요청
            Log.d(TAG, "startScan: 블루투스가 켜지지 않았습니다.")
            return
        }

        // 위치 정보 권한 허용 및 GPS Enable 여부 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                PERMISSIONS_CODE
            )
        }
        Log.d(TAG, "startScan: beacon Scan start")

        // Beacon Service bind
        beaconManager!!.bind(this@MapActivity)
    }


    // 블루투스 켰는지 확인
    private fun isEnableBLEService(): Boolean {
        if (!bluetoothAdapter!!.isEnabled) {
            return false
        }
        return true
    }

    // 블루투스 ON/OFF 여부 확인 및 키도록 하는 함수
    private fun requestEnableBLE() {
        val callBLEEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        requestBLEActivity.launch(callBLEEnableIntent)
        Log.d(TAG, "requestEnableBLE: ")
    }

    // 블루투스 요청 액티비티
    private val requestBLEActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // 사용자의 블루투스 사용이 가능한지 확인
        if (isEnableBLEService()) {
            needBLERequest = false
            startScan()
        }
    }

    // 위치 정보 권한 요청 결과 콜백 함수
    // ActivityCompat.requestPermissions 실행 이후 실행
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_CODE -> {
                if (grantResults.isNotEmpty()) {
                    for ((i, permission) in permissions.withIndex()) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.i(TAG, "$permission 권한 획득에 실패하였습니다.")
                        }
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        Log.d("bc2", "becaon2 on destroy")
        beaconManager!!.stopMonitoringBeaconsInRegion(region)
        beaconManager!!.stopRangingBeaconsInRegion(region)
        beaconManager!!.unbind(this)
        beaconManager!!.removeAllRangeNotifiers()
        beaconManager!!.disableForegroundServiceScanning()
        super.onDestroy()
    }

    override fun onStop() {
        Log.d("bc2", "becaon2 on onStop")
        beaconManager!!.unbind(this)
        beaconManager!!.removeAllRangeNotifiers()
        beaconManager!!.disableForegroundServiceScanning()
        super.onStop()
    }

    override fun onBeaconServiceConnect() {
        Log.d("bc2", "becaon2 onBeaconServiceConnect")
        beaconManager!!.setRangeNotifier { beacons, region ->
            Log.d("bc2", "becaon2 비컨 감지")

            // 비콘이 감지되면 해당 함수가 호출된다. Collection<Beacon> beacons에는 감지된 비콘의 리스트가,
            // region에는 비콘들에 대응하는 Region 객체가 들어온다.
            if (beacons.size > 0) {
                beaconList.clear()
                for (beacon in beacons) {
                    if (isYourBeacon(beacon)) {
                        // 한번만 띄우기 위한 조건
                        beaconList.add(beacon)
                        Log.d(
                            "bc",
                            "distance: " + beacon.distance + " Major : " + beacon.id2 + ", Minor" + beacon.id3
                        )
                        Log.d("bc", "becaon2 onBeaconServiceConnectin")
//                        OnStartTrace()
                        startActivity(Intent(this@MapActivity, BeaconResultActivity::class.java))
                        beaconManager!!.unbind(this@MapActivity)
                    }
                }
            }

            if (beacons.isEmpty()) {
                Log.d("bc", "beacons.isEmpty()")

            }
        }
        try {
            beaconManager!!.startRangingBeaconsInRegion(
                Region(
                    "myRangingUniqueId",
                    null,
                    null,
                    null
                )
            )
        } catch (e: RemoteException) {
        }

    }

    // 찾고자 하는 Beacon이 맞는지, 정해둔 거리 내부인지 확인
    private fun isYourBeacon(beacon: Beacon): Boolean {
        Log.d("bc", "isYourBeacon")
//        return (beacon.id2.toString() == BEACON_MAJOR &&
//                beacon.id3.toString() == BEACON_MINOR &&
//                beacon.distance <= STORE_DISTANCE
//                )
        return (beacon.distance <= 0.2)
    }


    // 버튼이 클릭되면 textView 에 비콘들의 정보를 뿌린다.
    fun OnStartTrace() {
        // 아래에 있는 handleMessage를 부르는 함수.
        Log.d("bc", "버튼 클릭")
        handler.sendEmptyMessage(0)
        if (finish == 1) {
            handler.removeMessages(1)
            finish = 1
            startActivity(Intent(this@MapActivity, BeaconResultActivity::class.java))
            return
        }
    }

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            var handfinish = 0

            // 비콘의 아이디와 거리를 측정하여 textView에 넣는다.
            for (beacon in beaconList) {
                Log.d("bc", " ${beacon.distance}")
                if (beacon.distance > 0 && beacon.distance <= 0.2) {
                    finish = 1
//                    startActivity(Intent(this@MapActivity, BeaconResultActivity::class.java))
//                    finish()
                    return
                }
            }

        }
    }
}