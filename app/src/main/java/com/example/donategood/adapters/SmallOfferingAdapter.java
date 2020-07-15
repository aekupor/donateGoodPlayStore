package com.example.donategood.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.donategood.R;
import com.example.donategood.models.Offering;

import java.util.List;

public class SmallOfferingAdapter extends OfferingAdapter {

    public SmallOfferingAdapter(Context context, List<Offering> offerings) {
        super(context, offerings);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.item_offering, parent, false);
        return new ViewHolder(view);
    }
}
