package com.leoybkim.walkingbuddy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leoybkim.walkingbuddy.Util.DestinationActivity;
import com.leoybkim.walkingbuddy.Util.MyLocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by leo on 04/02/17.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    private String mFacebookID;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int REQUEST_LOCATION = 2;
    private static Location mLocation;

    public static Location getmLocation() {
        return mLocation;
    }

    public static void setmLocation(Location mLocation) {
        LoginActivity.mLocation = mLocation;
    }

    private LocationManager mLocationManager;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String mProvider;
    private MyLocationListener mLocationListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        mCallbackManager = CallbackManager.Factory.create();

        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        mProvider = mLocationManager.getBestProvider(new Criteria(), true);

        createLocationRequest();

        mLoginButton = (LoginButton)findViewById(R.id.login_button);
        mLoginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends"));
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Login Success!");

                getMyFacebookDetails(loginResult);
                getFriendList();
                updateLocation();
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(LoginActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Login Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Login Failed!");
            }
        });

    }

    private void updateLocation() {
        try{
            if(mProvider != null){
                mLocation = mLocationManager.getLastKnownLocation(mProvider);
            }
        }catch (SecurityException e){
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                askForLocationPermission();
                Place place = PlacePicker.getPlace(data, LoginActivity.this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Place returned successfully" );
//                Log.d(TAG, "PLACE_PICKER_REQUEST: " + Integer.toString(PLACE_PICKER_REQUEST));
//                Log.d(TAG, "REQUEST_LOCATION: " + Integer.toString(REQUEST_LOCATION));
//
                Intent intent = new Intent(this, DestinationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("destination", place.getLatLng());

                LatLng origin = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                bundle.putParcelable("origin", origin);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        }
    }

    private void initializeGoogleAPIClient() {
        Log.i(TAG, "Initialize Google API Client");
        if(mGoogleApiClient == null){

            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            Log.i(TAG, "Created Google API Client");
        }
        if(!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            Log.i(TAG, "Connected Google API Client");
        }
    }

    private void askForLocationPermission() {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            //Needs an explanation of why they should give us location permission
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            Log.i(TAG, "Permission Granted");
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                Toast.makeText(getApplicationContext(), "Location permission was granted!",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Permission was denied or request was cancelled
                Toast.makeText(getApplicationContext(), "Location permission was denied",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
    * FACEBOOK STUFF
    * */
    private void getMyFacebookDetails(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        mFacebookID = null;
                        try {
                            mFacebookID = object.getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void getFriendList() {

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object,GraphResponse response) {

                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        //"/me/friends",
                        "me/taggable_friends?limit=5000&height=300&type=\"large\"",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {

                                try {
                                    JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                    Log.e(TAG, response.toString());
                                    Log.e(TAG,"Json Array Length "+ rawName.length());
                                    Log.e(TAG,"Json Array "+ rawName.toString());

                                    for (int i = 0; i < rawName.length(); i++) {
                                        JSONObject c = rawName.getJSONObject(i);

                                        String id = c.getString("id");
                                        Log.e(TAG, "ID: " + id);

                                        String name = c.getString("name");
                                        Log.e(TAG, "JSON NAME :"+ name);

                                        JSONObject phone = c.getJSONObject("picture");
                                        Log.e(TAG,""+ phone.getString("data"));

                                        JSONObject jsonObject = phone.getJSONObject("data");

                                        String url = jsonObject.getString("url").toString();
                                        Log.e(TAG,"@@@@"+jsonObject.getString("url").toString());
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResume() {
        super.onResume();
        initializeGoogleAPIClient();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if(mLocationRequest != null && mLocationListener != null){
            try{
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
            }catch(SecurityException e){
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, mLocationListener);
        Log.d(TAG, "stopLocationUpdates");
    }
}
