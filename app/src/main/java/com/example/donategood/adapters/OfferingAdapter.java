package com.example.donategood.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.donategood.models.Offering;
import com.example.donategood.R;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;

public class OfferingAdapter extends RecyclerView.Adapter<OfferingAdapter.ViewHolder> {

    public static final String TAG = "OfferingAdapter";

    private Context context;
    private List<Offering> offerings;

    public OfferingAdapter(Context context, List<Offering> offerings) {
        this.context = context;
        this.offerings = offerings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_offering, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Offering offering = offerings.get(position);
        holder.bind(offering);
    }

    @Override
    public int getItemCount() {
        return offerings.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTitle;
        private TextView tvPrice;
        private TextView tvUser;
        private ImageView ivOfferingPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvUser = itemView.findViewById(R.id.tvUser);
            ivOfferingPhoto = itemView.findViewById(R.id.ivOfferingPhoto);

            itemView.setOnClickListener(this);
        }

        public void bind(Offering offering) {
            tvTitle.setText(offering.getTitle());
            tvPrice.setText(Integer.toString(offering.getPrice()));
            try {
                tvUser.setText(offering.getUser().fetchIfNeeded().getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ParseFile image = offering.getImage();
            if (image != null) {
                Glide.with(context)
                        .load(offering.getImage().getUrl())
                        .into(ivOfferingPhoto);
            }
        }

        public void onClick(View v) {
            // get item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the offering at the position
                Offering offering = offerings.get(position);
                Log.i(TAG, "offering clicked: " + offering.getTitle());

                //go to detail fragment
            }
        }
    }
}