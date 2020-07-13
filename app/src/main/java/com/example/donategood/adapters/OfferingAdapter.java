package com.example.donategood.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.donategood.fragments.DetailFragment;
import com.example.donategood.models.Charity;
import com.example.donategood.models.Offering;
import com.example.donategood.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
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
        try {
            holder.bind(offering);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return offerings.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        offerings.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Offering> list) {
        offerings.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTitle;
        private TextView tvPrice;
        private TextView tvUser;
        private TextView tvCharity;
        private ImageView ivOfferingPhoto;
        private ImageView ivCharityProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvCharity = itemView.findViewById(R.id.tvCharity);
            ivOfferingPhoto = itemView.findViewById(R.id.ivOfferingPhoto);
            ivCharityProfile = itemView.findViewById(R.id.ivCharityProfile);

            itemView.setOnClickListener(this);
        }

        public void bind(Offering offering) throws ParseException {
            tvTitle.setText(offering.getTitle());
            tvPrice.setText(Integer.toString(offering.getPrice()));
            tvUser.setText(offering.getUser().fetchIfNeeded().getUsername());

            Charity charity = offering.getCharity().fetchIfNeeded();
            tvCharity.setText(charity.getTitle());

            ivOfferingPhoto.setImageDrawable(null);
            ParseFile image = offering.getImage();
            if (image != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .into(ivOfferingPhoto);
            }

            ivCharityProfile.setImageDrawable(null);
            ParseFile charityImage = offering.getCharity().getImage();
            if (charityImage != null) {
                Glide.with(context)
                        .load(charityImage.getUrl())
                        .into(ivCharityProfile);
            }

            //TODO: change to ParseFiles and get an array of images
            /*
            ArrayList<ParseUser> images = offering.getImages();
            if (images != null) {
                for (ParseUser img : images) {
                    try {
                        Log.i(TAG, "image" + img.fetchIfNeeded().getUsername());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
             */
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
                final FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                Fragment fragment = DetailFragment.newInstance(offering.getObjectId());
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        }
    }
}