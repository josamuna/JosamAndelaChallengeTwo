package com.project.josamuna.travelmantics;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FirebaseUtil {
    public static final String CURRENT_PATH = "traveldeals";
    private static final int RC_SIGN_IN = 123;
    private static FirebaseUtil instance;
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;

    //Supporting Firebase Authentication
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;

    //Supporing Firebase Storage
    public static FirebaseStorage mFirebaseStorage;
    public static StorageReference mStorageReference;

    public static ArrayList<TravelDeal> mDeals;

    private static ListActivity caller;

    public static boolean isAdmin;

    public static void getFirebaseInstance(String reference, final ListActivity callerActivity)
    {
        if(instance == null){
            instance = new FirebaseUtil();

            //Get FirbaseDatabase instance
            mFirebaseDatabase = FirebaseDatabase.getInstance();

            mFirebaseAuth = FirebaseAuth.getInstance();
//            caller = callerActivity;

            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(firebaseAuth.getCurrentUser() == null){
                        FirebaseUtil.signIn();
                    }else{
                        String userId = firebaseAuth.getUid();
                        checkAdmin(userId);
                    }

                    Toast.makeText(callerActivity.getBaseContext(), "Welcome back", Toast.LENGTH_LONG).show();
                }
            };
            FirebaseUtil.connectStorage();
        }

        //Initialise the ArraList
        mDeals = new ArrayList<TravelDeal>();

        //Get the Reference to the Firebase Database
        mDatabaseReference = mFirebaseDatabase.getReference().child(reference);
    }

    private static void checkAdmin(String userId) {
        FirebaseUtil.isAdmin = false;
        DatabaseReference reference = mFirebaseDatabase.getReference().child("administrators").child(userId);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin = true;
                caller.showMenu();
                Log.d("Admin", "User is a Administrator");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        reference.addChildEventListener(listener);
    }

    private static void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

       // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void attacheListener(){
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public static void detacheListener(){
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    public static void connectStorage(){
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("deals_picture");
    }
}
