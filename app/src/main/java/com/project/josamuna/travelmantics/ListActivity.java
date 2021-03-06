package com.project.josamuna.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

//import com.google.firebase.auth.FirebaseAuth;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list_deal, menu);

        MenuItem insertMenu = menu.findItem(R.id.insert_deal);

        if(FirebaseUtil.isAdmin){
            insertMenu.setVisible(true);
        }else{
            insertMenu.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.insert_deal:
                Intent intent = new Intent(ListActivity.this, DealActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout_deal:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Logout", "User logged out");
                                FirebaseUtil.attacheListener();
                            }
                        });
                FirebaseUtil.detacheListener();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detacheListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUtil.getFirebaseInstance(FirebaseUtil.CURRENT_PATH, this);

//        //Call RecycleView
        RecyclerView rvDeals = (RecyclerView) findViewById(R.id.list_deal);
        final DealAdapter adapter = new DealAdapter();
        rvDeals.setAdapter(adapter);

        //Apply Manager
        LinearLayoutManager dealsLayoutManager =
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        rvDeals.setLayoutManager(dealsLayoutManager);

        FirebaseUtil.attacheListener();
    }

    public void showMenu(){
        invalidateOptionsMenu();
    }
}
