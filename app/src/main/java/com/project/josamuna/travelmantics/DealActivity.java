package com.project.josamuna.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {

    public static final int PICTURE_RESULT = 42;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private EditText mTxtTitle;
    private EditText mTxtDescription;
    private EditText mTxtPrice;
    private TravelDeal td;
    private ImageView mImgImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

//        FirebaseUtil.getFirebaseInstance(FirebaseUtil.CURRENT_PATH, ListActivity.class);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        mTxtTitle = (EditText) findViewById(R.id.tv_deal_title);
        mTxtDescription = (EditText) findViewById(R.id.tv_description);
        mTxtPrice = (EditText) findViewById(R.id.tv_deal_price);
        mImgImage = (ImageView) findViewById(R.id.image_picture_load);

        Intent intent = getIntent();
        TravelDeal td = (TravelDeal) intent.getSerializableExtra(TravelDeal.TRAVELDEAL_EXTRA);

        if(td == null){
            td = new TravelDeal();
        }

        this.td = td;
        mTxtTitle.setText(td.getTitle());
        mTxtPrice.setText(td.getPrice());
        mTxtDescription.setText(td.getDescription());
        showImage(td.getImageUrl());

        Button btnSelectImage = (Button) findViewById(R.id.btn_load_image);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

                startActivityForResult(intent.createChooser(intent, "Select picture"), PICTURE_RESULT);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_deal, menu);

        if(FirebaseUtil.isAdmin){
            menu.findItem(R.id.delete_deal).setVisible(true);
            menu.findItem(R.id.save_deal).setVisible(true);

            enabledEditText(true);
        }
        else{
            menu.findItem(R.id.delete_deal).setVisible(false);
            menu.findItem(R.id.save_deal).setVisible(false);

            enabledEditText(false);
        }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICTURE_RESULT && resultCode == RESULT_OK){
            Uri imageUrl = data.getData();
            StorageReference reference = FirebaseUtil.mStorageReference.child(imageUrl.getLastPathSegment());
            reference.putFile(imageUrl).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String url = taskSnapshot.getUploadSessionUri().toString();
                    String pictureName = taskSnapshot.getStorage().getPath();

                    td.setImageUrl(url);
                    td.setImageName(pictureName);
                    showImage(url);
                }
            });
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

        if(td.getImageName() != null && !td.getImageName().isEmpty()){
            StorageReference refPicture = FirebaseUtil.mStorageReference.child(td.getImageName());
            refPicture.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Delete Image", "Image deleted successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Delete Image", e.getMessage());
                }
            });
        }
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

    private void enabledEditText(boolean isEnabled){
        mTxtTitle.setEnabled(isEnabled);
        mTxtDescription.setEnabled(isEnabled);
        mTxtPrice.setEnabled(isEnabled);
    }

    private void showImage(String imageUrl){
        if(imageUrl != null && !imageUrl.isEmpty()){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(imageUrl)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(mImgImage);
        }
    }
}
