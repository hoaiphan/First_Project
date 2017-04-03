package com.example.gio.firstproject.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.gio.firstproject.MockMarker;
import com.example.gio.firstproject.R;
import com.example.gio.firstproject.adapter.ViewPagerMarkerAdapter;
import com.example.gio.firstproject.model.MyMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Copyright by Gio.
 * Created on 3/31/2017.
 */

public class MapActivity extends AppCompatActivity implements LocationListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener, ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private ArrayList<MyMarker> mMyMarkers = MockMarker.getData();
    private ArrayList<Marker> mListMarkers = new ArrayList<>();
    private GoogleMap myMap;
    private ProgressDialog myProgress;
    private Marker previousSelectedMarker;

    private static final String MYTAG = "MYTAG";

    // Request for location (***).
    // value 8bit (value < 256).
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map);

        // Tạo Progress Bar
        myProgress = new ProgressDialog(this);
        myProgress.setTitle("Map Loading ...");
        myProgress.setMessage("Please wait...");
        myProgress.setCancelable(true);

        // Hiển thị Progress Bar
        myProgress.show();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMap);

        // Sét đặt sự kiện thời điểm GoogleMap đã sẵn sàng.
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
            }
        });

        // Add Detail location
        mViewPager = (ViewPager) findViewById(R.id.viewpager_location);
        FragmentManager fragmentManager = getSupportFragmentManager();
        ViewPagerMarkerAdapter mAdapter = new ViewPagerMarkerAdapter(fragmentManager);
        mViewPager.setAdapter(mAdapter);

        // Set onPageChange
        mViewPager.setOnPageChangeListener(this);
    }

    private void onMyMapReady(GoogleMap googleMap) {

        // Lấy đối tượng Google Map ra:
        myMap = googleMap;

        // Thiết lập sự kiện đã tải Map thành công
        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                // Đã tải thành công thì tắt Dialog Progress đi
                myProgress.dismiss();

                // Hiển thị vị trí người dùng.
                askPermissionsAndShowMyLocation();
            }
        });
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.setMyLocationEnabled(true);

        // Add marker
        for (int i = 0; i < mMyMarkers.size(); i++) {
            MyMarker myMarker = mMyMarkers.get(i);
            MarkerOptions option = new MarkerOptions();
            option.title(myMarker.getMarkerTitle());
            option.snippet(myMarker.getMarkerLatitude() + ";" + myMarker.getMarkerLongitude());
            option.position(new LatLng(myMarker.getMarkerLatitude(), myMarker.getMarkerLongitude()));
            option.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
            Marker marker = myMap.addMarker(option);
            mListMarkers.add(marker);
            marker.showInfoWindow();
        }

        myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (int i = 0; i < mListMarkers.size(); i++) {
                    if (marker.equals(mListMarkers.get(i))) {
                        mListMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_selected_marker));
                        mViewPager.setCurrentItem(i, true);
                        if (previousSelectedMarker != null) {
                            previousSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
                        }
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_selected_marker));
                        previousSelectedMarker = marker;
                    }
                }
                return false;
            }
        });
    }

    private void askPermissionsAndShowMyLocation() {

        // Với API >= 23, bạn phải hỏi người dùng cho phép xem vị trí của họ.
        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED) {

                // Các quyền cần người dùng cho phép.
                String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION};

                // Hiển thị một Dialog hỏi người dùng cho phép các quyền trên.
                ActivityCompat.requestPermissions(this, permissions,
                        REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
                return;
            }
        }

        // Hiển thị vị trí hiện thời trên bản đồ.
        this.showMyLocation();
    }


    // Khi người dùng trả lời yêu cầu cấp quyền (cho phép hoặc từ chối).
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION: {

                // Chú ý: Nếu yêu cầu bị bỏ qua, mảng kết quả là rỗng.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();

                    // Hiển thị vị trí hiện thời trên bản đồ.
                    this.showMyLocation();
                }
                // Hủy bỏ hoặc từ chối.
                else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    // Tìm một nhà cung cấp vị trị hiện thời đang được mở.
    private String getEnabledLocationProvider() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        // Tiêu chí để tìm một nhà cung cấp vị trí.
        Criteria criteria = new Criteria();

        // Tìm một nhà cung vị trí hiện thời tốt nhất theo tiêu chí trên.
        // ==> "gps", "network",...
        String bestProvider = locationManager.getBestProvider(criteria, true);

        boolean enabled = locationManager.isProviderEnabled(bestProvider);

        if (!enabled) {
            Toast.makeText(this, "No location provider enabled!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "No location provider enabled!");
            return null;
        }
        return bestProvider;
    }

    // Chỉ gọi phương thức này khi đã có quyền xem vị trí người dùng.
    private void showMyLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String locationProvider = this.getEnabledLocationProvider();

        if (locationProvider == null) {
            return;
        }

        // Millisecond
        final long MIN_TIME_BW_UPDATES = 1000;
        // Met
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

        Location myLocation;
        try {
            // Đoạn code nay cần người dùng cho phép (Hỏi ở trên ***).
            locationManager.requestLocationUpdates(
                    locationProvider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            // Lấy ra vị trí.
            myLocation = locationManager.getLastKnownLocation(locationProvider);
        }

        // Với Android API >= 23 phải catch SecurityException.
        catch (SecurityException e) {
            Toast.makeText(this, "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(MYTAG, "Show My Location Error:" + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (myLocation != null) {
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

            final CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(16)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            // Thêm MyMarker cho Map:
            MarkerOptions option = new MarkerOptions();
            option.title("My Location!");
            option.snippet(myLocation.getLatitude() + "+" + myLocation.getLongitude());
            option.position(latLng);
            option.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location));
            final Marker currentMarker = myMap.addMarker(option);
            currentMarker.showInfoWindow();

            myMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if (previousSelectedMarker != null) {
                        previousSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
                    }
                    myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    currentMarker.showInfoWindow();
                    return true;
                }
            });
        } else {
            Toast.makeText(this, "Location not found!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPageSelected(int position) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(mListMarkers.get(position).getPosition().latitude, mListMarkers.get(position).getPosition().longitude))             // Sets the center of the map to location user
                .zoom(16)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if (previousSelectedMarker != null) {
            previousSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
        }
        mListMarkers.get(position).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_selected_marker));
        mListMarkers.get(position).showInfoWindow();
        previousSelectedMarker = mListMarkers.get(position);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMyLocationButtonClick() {

        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
