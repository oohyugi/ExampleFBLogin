package com.yogiputra.examplefblogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


public class LoginFragment extends Fragment {
    private CallbackManager callbackManager=null;
    private AccessTokenTracker mtTracker=null;
    private ProfileTracker mProfileTracker=null;
    public static final String PARCEL_KEY="parcel_key";
    private LoginButton loginButton;

    FacebookCallback<LoginResult>callback=new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Profile profile=Profile.getCurrentProfile();
            homeFragment(profile);

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        callbackManager=CallbackManager.Factory.create();
        mtTracker=new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.v("AccessTokenTracker", "oldAccessToken=" + oldAccessToken + "||" + "CurrentAccessToken" + currentAccessToken);
            }
        };

        mProfileTracker=new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.v("Session Tracker", "oldProfile=" + oldProfile + "||" + "currentProfile" + currentProfile);
                homeFragment(currentProfile);
            }
        };
        mtTracker.startTracking();
        mProfileTracker.startTracking();
    }

    private void homeFragment(Profile profile) {
if (profile!=null){
    Bundle bundle=new Bundle();
    bundle.putParcelable(PARCEL_KEY, profile);
    HomeFragment homeFragment= new HomeFragment();
    homeFragment.setArguments(bundle);

    android.support.v4.app.FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
    android.support.v4.app.FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.mainContainer,new HomeFragment());
    fragmentTransaction.commit();


}
    }
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_login,container,false);

    }
    public void onViewCreated(View view,Bundle savedInstanceStated){
        super.onViewCreated(view, savedInstanceStated);
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        // If using in a fragment
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, callback);

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public void onStop() {
        super.onStop();
        mtTracker.stopTracking();
        mProfileTracker.stopTracking();
    }


    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


    public void onResume() {
        super.onResume();

        if (isLoggedIn()) {
            loginButton.setVisibility(View.INVISIBLE);
            Profile profile = Profile.getCurrentProfile();
            homeFragment(profile);
        }

    }
}