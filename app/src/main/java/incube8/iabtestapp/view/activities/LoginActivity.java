package incube8.iabtestapp.view.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import incube8.iabtestapp.R;
import incube8.iabtestapp.util.CommonUtils;
import incube8.iabtestapp.util.Constants;

/**
 * Created by shiv on 28/3/17.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    CallbackManager callbackManager;
    private Context mContext;
    private Button mBtFacebook;
    private RelativeLayout mLayParent;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            checkPermission();
        }
        init();
    }

    /**
     * This method initialize the views and facebook sdk
     */
    private void init() {
        mBtFacebook = (Button) findViewById(R.id.bt_normal);
        mLayParent = (RelativeLayout) findViewById(R.id.lay_parent);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        mBtFacebook.setOnClickListener(this);
    }

    /**
     * This method start the login via facebook process using facebook sdk
     */
    private void startFBLoginProcess() {
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                CommonUtils.showSnackBar(mLayParent, Constants.FACEBOOK_SUCESS);
                fetchUserInfoFromFacebook(loginResult);
            }

            @Override
            public void onCancel() {
                CommonUtils.showSnackBar(mLayParent, Constants.FACEBOOK_CANCEL_ERROR);
            }

            @Override
            public void onError(FacebookException error) {
                CommonUtils.showSnackBar(mLayParent, Constants.FACEBOOK_ERROR);

            }
        });
    }

    /**
     * This method called after user logins via facebook and provide user details like name,email,facebook id
     *
     * @param loginResult A argument of type LoginResult holding  acess token
     */
    private void fetchUserInfoFromFacebook(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken().getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            String userID = object.getString("id");
                            String userName = object.getString("name");
                            String email = "";
                            if (object.has("email")) {
                                email = object.getString("email");
                            }
                            String url = "https://graph.facebook.com/" + userID + "/picture?type=normal";
                            navigateToSubscriptionActivity(userName, email, url);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,link");
        request.setParameters(parameters);
        request.executeAsync();
    }

    /**
     * This method used for storing user information fetched from facebook in bundle and then pass it to subscription activity via explicit intent
     *
     * @param userName A variable of type String holding name of the user
     * @param email    A variable of type String holding email address of the user
     * @param url      A variable of type String holding image url of the user
     */
    private void navigateToSubscriptionActivity(String userName, String email, String url) {
        Bundle bundle = new Bundle();
        bundle.putString("name", userName);
        bundle.putString("email", email);
        bundle.putString("img_url", url);
        Intent intent = new Intent(mContext, SubscriptionActivity.class);
        intent.putExtra("user_info", bundle);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        if (CommonUtils.isNetworkAvailable(mContext))
            startFBLoginProcess();
        else
            CommonUtils.showSnackBar(mLayParent, Constants.NO_INTERNET_MESSAGE);
    }

    /**
     * @return true if already have permission in marshmallow
     */
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE);

        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermission();
            return false;
        }
    }

    /**
     * If network state is not enabled, it requests for
     * the network state permission to be enabled
     */
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_NETWORK_STATE)) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE}, PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CommonUtils.showSnackBar(mLayParent, getString(R.string.permission_granted));
                } else {
                    CommonUtils.showSnackBar(mLayParent, getString(R.string.permission_denied));
                    checkPermission();
                }
                break;
        }
    }
}
