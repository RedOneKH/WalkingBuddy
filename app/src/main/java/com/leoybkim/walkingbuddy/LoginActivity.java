package com.leoybkim.walkingbuddy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLng;
import com.leoybkim.walkingbuddy.Map.MyLocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import static com.google.android.gms.location.places.ui.PlacePicker.getPlace;

/**
 * Created by leo on 04/02/17.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    private String mFacebookID;
    private String mFacebookName;
    private Bitmap mBitmap;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int REQUEST_LOCATION = 2;
    private static Location mLocation;
    private User mUser;

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
        askForLocationPermission();
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
//                mBitmap = getFacebookProfilePicture(mFacebookID);

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
                Log.d(TAG, "Login Failed! " + error.toString());
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
                // Retrieve place from place picker
                Place place = getPlace(LoginActivity.this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();

                Log.d(TAG, "Place returned successfully" );

                // Compute user's values
                LatLng src = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                LatLng dest = place.getLatLng();
                Drawable pic = ResourcesCompat.getDrawable(getResources(), R.drawable.placeholder200x200, null);
                // TODO: Get the actual picture, now it' a placeholder
                // Put all user's shit together
                mUser = new User(mFacebookName, pic, mFacebookID, src, dest, null, null);

                // Initialize the intent for DestinationActivity
                Intent intent = new Intent(this, DestinationActivity.class);
                // Put the user info in the intent
                intent.putExtra("user", mUser);
                // start DestinationActivity with the info of the user
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
    public void getMyFacebookDetails(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            mFacebookID = object.getString("id");
                            mFacebookName = object.getString("name");

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

    public static Bitmap getFacebookProfilePicture(String userID){
        URL imageURL = null;
        try {
            imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    private void getFriendList() {

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object,GraphResponse response) {

                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),

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

                                } catch (Exception e) {
                                    Log.e(TAG, "I am awful and have no friends");
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
