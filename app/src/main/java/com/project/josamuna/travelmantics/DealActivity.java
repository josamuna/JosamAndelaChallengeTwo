package com.project.josamuna.travelmantics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DealActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private EditText mTxtTitle;
    private EditText mTxtDescription;
    private EditText mTxtPrice;
    private TravelDeal td;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        FirebaseUtil.getFirebaseInstance(FirebaseUtil.CURRENT_PATH);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        mTxtTitle = (EditText) findViewById(R.id.tv_deal_title);
        mTxtDescription = (EditText) findViewById(R.id.tv_description);
        mTxtPrice = (EditText) findViewById(R.id.tv_deal_price);

        Intent intent = getIntent();
        TravelDeal td = (TravelDeal) intent.getSerializableExtra(TravelDeal.TRAVELDEAL_EXTRA);

        if(td == null){
            td = new TravelDeal();
        }

        this.td = td;
        mTxtTitle.setText(td.getTitle());
        mTxtPrice.setText(td.getPrice());
        mTxtDescription.setText(td.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_deal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_deal:
                saveDeal();
                Toast.makeText(this, "Deal saved.", Toast.LENGTH_SHORT).show();
                clear();
                backToList();
                return true;
            case R.id.delete_deal:
                delete();
                Toast.makeText(this, "Deal deleted.", Toast.LENGTH_SHORT).show();
                clear();
                backToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void backToList() {
        Intent intent = new Intent(DealActivity.this, ListActivity.class);
        startActivity(intent);
    }

    private void delete() {
        if(td == null){
            Toast.makeText(this, "Please first save the deal before delete it.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUtil.mDatabaseReference.child(td.getId()).removeValue();
    }

    private void clear() {
        mTxtTitle.setText("");
        mTxtDescription.setText("");
        mTxtPrice.setText("");
        mTxtTitle.requestFocus();
    }

    private void saveDeal() {
        TravelDeal deal = new TravelDeal();

        deal.setId(td.getId());
        deal.setTitle(mTxtTitle.getText().toString());
        deal.setPrice(mTxtPrice.getText().toString());
        deal.setDescription(mTxtDescription.getText().toString());

        if(deal.getId() == null){
            FirebaseUtil.mDatabaseReference.push().setValue(deal);
        }else{
            FirebaseUtil.mDatabaseReference.child(td.getId()).setValue(deal);
        }
    }
}
