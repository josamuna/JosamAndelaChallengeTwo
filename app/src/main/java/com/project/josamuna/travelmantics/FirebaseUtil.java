package com.project.josamuna.travelmantics;

import android.app.Activity;
import android.widget.Toast;

//import com.firebase.ui.auth.AuthUI;
//import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

public class FirebaseUtil {
    public static final String CURRENT_PATH = "traveldeals";
    private static final int RC_SIGN_IN = 123;
    private static FirebaseUtil instance;
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    public static ArrayList<TravelDeal> mDeals;

    private static Activity caller;

    //Supporting Firebase Authentication
//    public static FirebaseAuth mFirebaseAuth;
//    public static FirebaseAuth.AuthStateListener mAuthStateListener;

    public static void getFirebaseInstance(String reference)
    {
        if(instance == null){
            instance = new FirebaseUtil();

            //Get FirbaseDatabase instance
            mFirebaseDatabase = FirebaseDatabase.getInstance();

//            mFirebaseAuth = FirebaseAuth.getInstance();
//            caller = callerActivity;

//            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
//                @Override
//                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                    signIn();
//                    Toast.makeText(callerActivity.getBaseContext(), "Welcome back", Toast.LENGTH_LONG).show();
//                }
//            };
        }

        //Initialise the ArraList
        mDeals = new ArrayList<TravelDeal>();

        //Get the Reference to the Firebase Database
        mDatabaseReference = mFirebaseDatabase.getReference().child(reference);
    }

    private static void signIn() {
        // Choose authentication providers
//        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build());
//
//        // Create and launch sign-in intent
//        caller.startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAvailableProviders(providers)
//                        .build(),
//                RC_SIGN_IN);
    }

    public static void signin(){

    }

    public static void attacheListener(){
//        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public static void detacheListener(){
//        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
}
