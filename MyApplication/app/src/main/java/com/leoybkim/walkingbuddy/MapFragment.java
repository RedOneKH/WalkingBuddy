package com.leoybkim.walkingbuddy;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leoybkim.walkingbuddy.Util.MyLocationListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Dan on 04/02/2017.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static String TAG = MapFragment.class.getName();

    private View view;
    private FragmentManager fragmentManager;
    private SupportMapFragment supportMapFragment;
    private LocationManager locationManager;
    private Location location;
    private LocationRequest locationRequest;
    private MyLocationListener myLocationListener;

    //Key is used to retrieve the fragment from fragment manager
    private String mapFragmentKey = "map";

    private Button placePickerButton;

    /**
     *  Map stuff
     */
    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private Marker destinationMarker;
    private Circle destinationCircle;

    private static GoogleApiClient mGoogleAPIClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        try {
            view = inflater.inflate(R.layout.map_fragment, container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");

        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();

            fragmentManager = getFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.container, supportMapFragment, MapFragment.TAG)
                    .commit();
        }

        supportMapFragment.getMapAsync(this);
        initializeGoogleAPIClient();
        askForLocationPermission();
        createLocationRequest();
        initializeButtons();
    }

    private void initializeButtons() {
        placePickerButton = (Button) view.findViewById(R.id.searchPlace);
        placePickerButton.setOnClickListener(startPlacePickerListener);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady");
        initializeGoogleAPIClient();
        mMap = googleMap;

        mUiSettings = mMap.getUiSettings();

        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMapToolbarEnabled(true);


        //Get location manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);

        //get location if available
        try{
            if(provider != null){
                location = locationManager.getLastKnownLocation(provider);
            }
        }catch(SecurityException e){
            e.printStackTrace();
        }

        //Zoom to location
        if(location !=null){
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition( CameraPosition.fromLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 15)
            ));
        }

        try {
             mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Snackbar snackbar = Snackbar.make(view, "Cannot set up locationButton", Snackbar.LENGTH_SHORT);
            snackbar.show();
            e.printStackTrace();
        }


        /**
         * Not map-related
         */

    }


    /** Map modifiers
     *
     */
    private void clearRedundant() {
        mMap.clear();
    }

    /**
     * Callbacks
     */

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                String toastMsg = String.format("Place: %s", place.getName());
                Snackbar snackbar = Snackbar.make(view, "Picked " + place.getName(), Snackbar.LENGTH_SHORT);
                snackbar.show();

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14));


                destinationMarker = mMap.addMarker(new
                        MarkerOptions()
                        .position(place.getLatLng())
                        .title((String) place.getAddress()));
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_xxl)));
            }
        }
    }


    /**
     * Listeners
     */

    private PlaceSelectionListener placeSelectionListener = new PlaceSelectionListener() {
        @Override
        public void onPlaceSelected(Place place) {
            Log.d(TAG, "Selected place Add is : " + place.getAddress().toString());
            Log.d(TAG, "Selected place LatLng is : " + place.getLatLng().toString());
            Log.d(TAG, "Selected place Name is : " + place.getName().toString());
            clearRedundant();
            destinationMarker = mMap.addMarker(new MarkerOptions()
                    .position(place.getLatLng())
                    .title(place.getName().toString()));
            destinationCircle = mMap.addCircle(new CircleOptions()
                    .center(place.getLatLng())
                    .radius(200)
                    .strokeColor(Color.BLACK)
                    .fillColor(0x00000000));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
        }

        @Override
        public void onError(Status status) {

        }
    };

    int PLACE_PICKER_REQUEST = 1;

    private View.OnClickListener startPlacePickerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            }
        }
    };

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to

                Snackbar snackbar = Snackbar.make(view, "Location permission was granted!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else {
                // Permission was denied or request was cancelled
                Snackbar snackbar = Snackbar.make(view, "Location permission was denied", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
    }



    /**
     * Initialization stuff
     */

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(80000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private int REQUEST_LOCATION = 2;

    private void askForLocationPermission() {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            //Needs an explanation of why they should give us location permission

            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

            }

        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            Log.i(TAG, "Permission Granted");
        }
    }



    private void initializeGoogleAPIClient() {
        Log.i(TAG, "Initialize Google API Client");
        if (mGoogleAPIClient == null) {

            mGoogleAPIClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            Log.i(TAG, "Created Google API Client");
        }
        if (!mGoogleAPIClient.isConnected()) {
            mGoogleAPIClient.connect();
            Log.i(TAG, "Connected Google API Client");
        }
    }

    private void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates");
        if(locationRequest != null && myLocationListener != null){
            try{
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleAPIClient, locationRequest, myLocationListener);
            }catch(SecurityException e){
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleAPIClient, myLocationListener);
        Log.d(TAG, "stopLocationUpdates");
    }

    /**
     * Lifecycle stuff
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        if (mGoogleAPIClient != null) mGoogleAPIClient.disconnect();

        android.app.Fragment fragment = getActivity().getFragmentManager().findFragmentByTag(mapFragmentKey);
        if (fragment != null) {
            Log.d(TAG, "Fragment is null");
            getActivity().getFragmentManager().beginTransaction()
                    .remove(fragment);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach()");

    }


    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView()");
        super.onDestroyView();


    }

    /** ConnectionCallbacks
     *
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectedSuspended");
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


