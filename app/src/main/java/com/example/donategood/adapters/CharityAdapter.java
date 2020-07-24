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

import com.example.donategood.R;
import com.example.donategood.fragments.CharityFragment;
import com.example.donategood.helperClasses.LoadPost;
import com.example.donategood.models.Charity;

import java.util.List;

public class CharityAdapter extends RecyclerView.Adapter<CharityAdapter.ViewHolder> {

    public static final String TAG = "CharityAdapter";

    private Context context;
    private List<Charity> charities;

    public CharityAdapter(Context context, List<Charity> charities) {
        this.context = context;
        this.charities = charities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_charity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Charity charity = charities.get(position);
        holder.bind(charity);
    }

    @Override
    public int getItemCount() {
        return charities.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        charities.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Charity> list) {
        charities.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvCharityName;
        ImageView ivProfileImage;

        LoadPost loadPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCharityName = itemView.findViewById(R.id.tvCharityCharityName);
            ivProfileImage = itemView.findViewById(R.id.ivCharityCharityImage);

            loadPost = new LoadPost();

            itemView.setOnClickListener(this);
        }

        public void bind(Charity charity) {
            loadPost.setCharityWithCharity(charity, context, tvCharityName, ivProfileImage);
        }

        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                Charity charity = charities.get(position);
                Log.i(TAG, "charity clicked: " + charity.getTitle());

                //go to that charities's profile fragment
                final FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                Fragment fragment = CharityFragment.newInstance(charity.getTitle());
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        }
    }
}