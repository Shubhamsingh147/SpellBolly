package com.example.shubhamsingh.spellbolly;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends Activity implements OnClickListener{
    LoginButton loginButton;
    CallbackManager callbackManager;
    LinearLayout gameLayout;
    SharedPreferences sharedpreferences,preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        gameLayout = (LinearLayout) findViewById(R.id.game);
        preferences = getSharedPreferences("SpellBolly", 0);
        String id = preferences.getString("id",null);
        if (id == null) {
            loginButton.setReadPermissions(Arrays.asList("public_profile"));
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
                                    // Application code
                                    Log.v("LoginActivity", response.getRawResponse());
                                    //RawResponseFormat  {"id":"10206351166570658","name":"Ajeet Kumar","gender":"male"}
                                    sharedpreferences = getSharedPreferences("SpellBolly", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    try {
                                        editor.putString("id", response.getJSONObject().getString("id").toString());
                                        editor.putString("name", response.getJSONObject().getString("name").toString());
                                        editor.putString("gender", response.getJSONObject().getString("gender").toString());
                                        editor.commit();
                                        gameLayout.setVisibility(View.VISIBLE);
                                        Toast.makeText(MainActivity.this,"Login Successful", Toast.LENGTH_LONG).show();
                                    }
                                    catch (JSONException e)
                                    {
                                        Toast.makeText(MainActivity.this,"Login UnSuccessful, Try Again", Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,gender");
                    request.setParameters(parameters);
                    request.executeAsync();

                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException e) {

                }
            });

        } else {
            gameLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.login_button){
            if(loginButton.getText().toString().equals("Log out"))
                recreate();
        }
    }
}
