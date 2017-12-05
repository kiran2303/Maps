package com.bhailal.sony.newmap;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {
    private static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private EditText city,state,subarea,pincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        city = (EditText)findViewById(R.id.city);
        state = (EditText)findViewById(R.id.state);
        subarea = (EditText)findViewById(R.id.plotNo);
        pincode = (EditText)findViewById(R.id.pincode);

        clientConnect();
    }
    private void clientConnect() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQUEST_CODE);
        }
        getLocation();
    }
  private void getLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, getLocationRequest(), new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        Toast.makeText(MapsActivity.this, "location refreshed", Toast.LENGTH_SHORT).show();

                        mLastLocation = location;



                        double latitude = mLastLocation.getLatitude();
                        double longitude = mLastLocation.getLongitude();
//                        Toast.makeText(MapsActivity.this, "local", Toast.LENGTH_SHORT).show();
//
//                        Toast.makeText(MapsActivity.this, "current", Toast.LENGTH_SHORT).show();
                        Log.d("myapp",String.valueOf(latitude));
                        Log.d("myapp",String.valueOf(longitude));
                        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                        List<Address> address = null;
                        try {
                            address = geocoder.getFromLocation(latitude,longitude,1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Log.d("myapp",address.toString());
                        if(address!=null) {
                            String block1 = address.get(0).getPremises();
                            String cityName = address.get(0).getLocality();
                            String stateName = address.get(0).getAdminArea();
                            String pincode1 = address.get(0).getPostalCode().toString();
                            String localarea = address.get(0).getSubLocality();



                            subarea.setText(localarea+"   "+block1);

                            city.setText(cityName);
                            state.setText(stateName);
                            pincode.setText(pincode1);
                        }

                    }
                }
            });
//            if(mLastLocation != null){
//                double latitude = mLastLocation.getLatitude();
//                double longitude = mLastLocation.getLongitude();
//                Toast.makeText(this, "local", Toast.LENGTH_SHORT).show();
//
//                Toast.makeText(this, "current", Toast.LENGTH_SHORT).show();
//                Log.d("myapp",String.valueOf(latitude));
//                Log.d("myapp",String.valueOf(longitude));
//                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//                List<Address> address = null;
//                try {
//                    address = geocoder.getFromLocation(latitude,longitude,1);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                Log.d("myapp",address.toString());
//                if(address!=null){
//
//                    Toast.makeText(this, ""+address.get(0).getLocality(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(this, ""+address.get(0).getAdminArea(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(this, ""+address.get(0).getPostalCode(), Toast.LENGTH_SHORT).show();
//                    String cityName = address.get(0).getLocality();
//                    String stateName = address.get(0).getAdminArea();
//
//                    city.setText(cityName);
//                    state.setText(stateName);
//
//
//
//                }
//            }
         }
     }
    private LocationRequest getLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1500L);
        locationRequest.setFastestInterval(1500L);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onClick(View v) {

    }
}
