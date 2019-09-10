package com.example.kidspotz;

import android.view.View;
import android.net.Uri;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPoint;
import com.google.maps.android.data.geojson.GeoJsonPointStyle;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;

import android.location.Location;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderApi;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.HashMap;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
public class MapsMarkerActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, OnMapReadyCallback, LocationListener {

    GoogleApiClient mGoogleApiClient;
    GoogleMap map;
    Location mLastLocation;
    LatLng userLoc;
    LocationRequest mLocationRequest;
    private FusedLocationProviderApi mFusedLocationApi;
    static int REQUEST_LOCATION = 2;
    Marker myMarker;
    static int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationApi = LocationServices.FusedLocationApi;

        // Create an instance of GoogleAPIClient. to find user location
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getBaseContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        /*
         * Connect the client. Don't re-start any requests here; instead, wait
         * for onResume()
         */
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        if (mGoogleApiClient != null) {
           mFusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("connected");

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        runLocation();
    }

    public void runLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else {
            mFusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = mFusedLocationApi.getLastLocation(mGoogleApiClient);
            userLoc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            onMapReady(map);
        }
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                mLastLocation = mFusedLocationApi.getLastLocation(mGoogleApiClient);
                userLoc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            } else {
                System.out.println("permission denied");
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        userLoc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        onMapReady(map);

    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("failed");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker
        // and move the map's camera to the same location.
        //
        map = googleMap;
        if (userLoc != null) {
            myMarker = googleMap.addMarker(new MarkerOptions().position(userLoc)
                    .title("You are here").snippet("Learn more: website"));
            googleMap.setMaxZoomPreference(30.0f);
            googleMap.setMinZoomPreference(5.0f);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 15.0f));

            GeoJsonPoint point = new GeoJsonPoint(userLoc);
            HashMap<String, String> properties = new HashMap<String, String>();
            properties.put("Ocean", "South Atlantic");
            GeoJsonFeature pointFeature = new GeoJsonFeature(point, "Origin", properties, null);

        }
        else {
            LatLng sydney = new LatLng(-33.852, 151.211);
            googleMap.addMarker(new MarkerOptions().position(sydney)
                    .title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }

    }

    public void onSearchNearby(View view) {
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(intentBuilder.build(this), PLACE_PICKER_REQUEST);

            // Hide the pick option in the UI to prevent users from starting the picker
            // multiple times.
            //showPickAction(false);

        } catch (GooglePlayServicesRepairableException e) {
            Toast.makeText(this, "repairable exception", Toast.LENGTH_LONG).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "not available exception", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                String address1 = place.getLatLng().toString();
                String address2 = userLoc.toString();

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + address2 + "&daddr=" + address1));
                startActivity(intent);


            }
        }
    }
}
