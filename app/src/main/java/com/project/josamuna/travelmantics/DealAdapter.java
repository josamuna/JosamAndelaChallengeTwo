package com.project.josamuna.travelmantics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.ViewHolder> {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ArrayList<TravelDeal> deals;
    private ChildEventListener mChildEventListener;

    public DealAdapter() {
        FirebaseUtil.getFirebaseInstance(FirebaseUtil.CURRENT_PATH);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        deals = FirebaseUtil.mDeals;

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
                td.setId(dataSnapshot.getKey());

                deals.add(td);
                notifyItemInserted(deals.size() - 1);
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
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.rv_list_item, parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TravelDeal td = deals.get(position);
        holder.bind(td);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {

        private final TextView mTxtTitle;
        private final TextView mTxtDescription;
        private final TextView mTxtPrice;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            mTxtTitle = (TextView) itemView.findViewById(R.id.text_title_deal);
            mTxtDescription = (TextView) itemView.findViewById(R.id.text_deal_description);
            mTxtPrice = (TextView) itemView.findViewById(R.id.text_deal_price);

            itemView.setOnClickListener(this);
        }

        private void bind(TravelDeal td){
            mTxtTitle.setText(td.getTitle());
            mTxtDescription.setText(td.getDescription());
            mTxtPrice.setText(td.getPrice());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            TravelDeal dealSelected = deals.get(position);
            Log.d("Deal_ID", dealSelected.getId());
            Intent intent = new Intent(view.getContext(), DealActivity.class);
            intent.putExtra(TravelDeal.TRAVELDEAL_EXTRA, dealSelected);

            view.getContext().startActivity(intent);
        }
    }
}
