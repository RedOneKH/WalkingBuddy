package com.leoybkim.walkingbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by leo on 04/02/17.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    private AccessToken mAccessToken;
    private String mFacebookID;
    private static final int PLACE_PICKER_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        mCallbackManager = CallbackManager.Factory.create();

        mLoginButton = (LoginButton)findViewById(R.id.login_button);

        mLoginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends"));

        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Login Success!");

                getMyFacebookDetails(loginResult);
                getFriendList();

                // Open main activity
                //Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                //startActivity(mainIntent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

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

                                        String name = c.getString("name");
                                        Log.e(TAG, "JSON NAME :"+name);

                                        JSONObject phone = c.getJSONObject("picture");
                                        Log.e(TAG,""+phone.getString("data"));

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
}
