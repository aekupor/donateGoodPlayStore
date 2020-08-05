package com.example.donategood.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.donategood.OnSwipeTouchListener;
import com.example.donategood.R;
import com.example.donategood.adapters.CommentAdapter;
import com.example.donategood.adapters.SmallOfferingAdapter;
import com.example.donategood.helperClasses.CommentLoader;
import com.example.donategood.helperClasses.FBQuery;
import com.example.donategood.helperClasses.LoadPost;
import com.example.donategood.helperClasses.NotificationLoader;
import com.example.donategood.helperClasses.Purchase;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.helperClasses.Recommend;
import com.example.donategood.models.Comment;
import com.example.donategood.models.Offering;
import com.facebook.share.widget.ShareButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment implements ComposeCommentFragment.ComposeCommentDialogListener {

    public static final String TAG = "DetailFragment";
    public static final String CHARITY_KEY = "charity";
    public static final String USER_KEY = "user";
    public static final String COMPOSE_KEY = "compose";

    private String offeringId;
    public Query query;
    public Offering offering;
    private LoadPost loadPost;
    private NotificationLoader notificationLoader;
    private Recommend recommend;
    private CommentLoader commentLoader;
    private Purchase purchase;
    private FBQuery fbQuery;

    private TextView tvTitle;
    private TextView tvPrice;
    private TextView tvUser;
    private TextView tvCharity;
    private ImageView ivCharityImage;
    private ImageView ivOfferingPhoto;
    private Button btnPurchase;
    private Button btnComment;
    private TextView tvQuantityLeft;
    public TextView tvCommentTitle;
    private RatingBar ratingBar;
    private TextView tvDescription;
    private Button btnEdit;
    private ImageView ivProfileImage;
    private Button btnNextPicture;
    private ArrayList<ParseFile> imagesArray;
    private Integer currImage;
    private Button btnPreviousPicture;
    private Integer numImages;

    private RecyclerView rvRecommendedOfferings;
    private SmallOfferingAdapter adapter;
    private List<Offering> recommendedOfferings;
    private RecyclerView rvComments;
    public CommentAdapter commentAdapter;
    public List<Comment> allComments;
    public Integer numComments;

    private ShareButton shareButton;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(String offeringId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString("offeringId", offeringId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offeringId = getArguments().getString("offeringId", "");
        Log.i(TAG, "post id: " + offeringId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //if user swipes right, go back to home screen
        view.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeRight() {
                Log.i(TAG, "onSwipeRight, going to home fragment");
                final FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
                Fragment fragment = new HomeFragment();
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.flContainer, fragment).commit();
            }
        });

        initializeVariables(view);
        findOffering();
    }

    private void initializeVariables(View view) {
        //find all variables
        tvTitle = view.findViewById(R.id.tvDetailTitle);
        tvPrice = view.findViewById(R.id.tvDetailPrice);
        tvCharity = view.findViewById(R.id.tvDetailCharity);
        tvUser = view.findViewById(R.id.tvDetailUser);
        ivCharityImage = view.findViewById(R.id.ivDetailCharityImage);
        btnPurchase = view.findViewById(R.id.btnPurchase);
        ivOfferingPhoto = view.findViewById(R.id.ivDetailOfferingPhoto);
        shareButton = (ShareButton)view.findViewById(R.id.fbShareButtonDetail);
        rvRecommendedOfferings = view.findViewById(R.id.rvRecommendOfferings);
        btnComment = view.findViewById(R.id.btnComment);
        tvQuantityLeft = view.findViewById(R.id.tvQuantityLeft);
        tvCommentTitle = view.findViewById(R.id.tvViewCommentsTitle);
        ratingBar = (RatingBar) view.findViewById(R.id.rbDetail);
        rvComments = view.findViewById(R.id.rvComments);
        tvDescription = view.findViewById(R.id.tvDetailDescription);
        btnEdit = view.findViewById(R.id.btnEditOffering);
        ivProfileImage = view.findViewById(R.id.ivDetailProfileImage);
        btnNextPicture = view.findViewById(R.id.btnNextPicture);
        btnPreviousPicture = view.findViewById(R.id.btnPreviousPicture);

        numComments = 0;
        loadPost = new LoadPost();
        query = new Query();
        notificationLoader = new NotificationLoader();
        recommend = new Recommend();
        commentLoader = new CommentLoader();
        purchase = new Purchase();
        fbQuery = new FBQuery();

        //set up recycler view and adapter for reccomended offerings
        recommendedOfferings = new ArrayList<>();
        adapter = new SmallOfferingAdapter(getContext(), recommendedOfferings);

        rvRecommendedOfferings.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvRecommendedOfferings.setLayoutManager(linearLayoutManager);

        //set up recycler view and adapter for comments
        allComments = new ArrayList<>();
        commentAdapter = new CommentAdapter(getContext(), allComments);

        rvComments.setAdapter(commentAdapter);
        LinearLayoutManager linearLayoutManagerComment = new LinearLayoutManager(getContext());
        rvComments.setLayoutManager(linearLayoutManagerComment);

        //set onClickListeners
        tvCharity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToOtherFragment(CHARITY_KEY);
            }
        });

        ivCharityImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToOtherFragment(CHARITY_KEY);
            }
        });

        tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToOtherFragment(USER_KEY);
            }
        });

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToOtherFragment(USER_KEY);
            }
        });

        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchaseItem();
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "comment button clicked");
                showEditDialog();
            }
        });

        btnNextPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnNextPicture clicked");

                //go to next image
                currImage++;
                if (currImage == numImages) {
                    //if reached end of images, go back to beginning
                    currImage = 0;
                }
                setCurrentImage();
            }
        });

        btnPreviousPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnPreviousPicture clicked");

                //go to previous image
                currImage--;
                if (currImage == -1) {
                    //if reached beginning of images, go to end
                    currImage = numImages - 1;
                }
                setCurrentImage();
            }
        });
    }

    //sets appropriate image based on where the user has scrolled to
    private void setCurrentImage() {
        Glide.with(getContext())
                .load(imagesArray.get(currImage).getUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivOfferingPhoto);
    }

    //finds the offering to display on detail page (offering that the user clicked on)
    private void findOffering() {
        query.findOffering(offeringId, new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting post", e);
                    return;
                }
                offering = objects.get(0);
                Log.i(TAG, "got offering with title: " + offering.getTitle());

                loadInformation();
            }
        });
    }

    //sets all variables to the appropriate values
    private void loadInformation() {
        loadPost.setTitlePrice(offering, tvTitle, tvPrice);
        loadPost.setUserFromOffering(offering, getContext(), tvUser, ivProfileImage);
        loadPost.setCharity(offering, getContext(), tvCharity, ivCharityImage);
        loadPost.setPostImage(offering.getImage(), getContext(), ivOfferingPhoto);
        fbQuery.setShareButton(shareButton, offering, this);
        recommend.queryRecommendedPosts(query, offering, adapter, recommendedOfferings);
        commentLoader.queryComments(this);

        //only show rating if offering has one
        if (offering.getRating() == 0) {
            ratingBar.setVisibility(View.INVISIBLE);
        } else {
            ratingBar.setNumStars(offering.getRating());
        }

        //only show description if offering has one
        if (offering.getDescription() == null) {
            tvDescription.setVisibility(View.INVISIBLE);
        } else {
            tvDescription.setText(offering.getDescription());
        }

        //hide purchase button if there is no quantity left
        tvQuantityLeft.setText("Quantity Left: " + offering.getQuantityLeft().toString());
        if (offering.getQuantityLeft() == 0) {
            btnPurchase.setVisibility(View.INVISIBLE);
        }

        //only show edit button if selling user of offering is the current signed in user
        if (offering.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "edit button clicked");
                    goToOtherFragment(COMPOSE_KEY);
                }
            });
        } else {
            btnEdit.setVisibility(View.INVISIBLE);
        }

        //create picture scrolling if offering has multiple images
        if (offering.hasMultipleImages()) {
            imagesArray = offering.getImagesArray();
            numImages = imagesArray.size();
            currImage = 0;
            setCurrentImage();
        } else {
            btnNextPicture.setVisibility(View.INVISIBLE);
            btnPreviousPicture.setVisibility(View.INVISIBLE);
        }
    }

    //navigate to a different specified fragment
    private void goToOtherFragment(String fragmentName) {
        final FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
        Fragment fragment = null;
        if (fragmentName.equals(CHARITY_KEY)) {
            //go to charity fragment
            fragment = CharityFragment.newInstance(offering.getCharity().getTitle());
        } else if (fragmentName.equals(USER_KEY)) {
            //go to other user profile fragment
            Bundle bundle = new Bundle();
            bundle.putParcelable(USER_KEY, offering.getUser());
            fragment = OtherUserProfileFragment.newInstance(bundle);
        } else if (fragmentName.equals(COMPOSE_KEY)) {
            // go to compose fragment to edit the offering
            Bundle bundle = new Bundle();
            bundle.putParcelable("offering", offering);
            fragment = ComposeFragment.newInstance(bundle);
        }

        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
    }

    //called after user has created a comment
    @Override
    public void onFinishEditDialog(String inputText, String rating) {
        Log.i(TAG, "got comment with text: " + inputText + " and rating: " + rating);

        //add comment to adapter
        allComments.add(commentLoader.saveComment(inputText, rating, getContext(), offering));
        commentAdapter.notifyDataSetChanged();
        tvCommentTitle.setVisibility(View.VISIBLE);

        purchase.updateOfferingRating(rating, offering, ratingBar, numComments);
    }

    //open ComposeCommentFragment
    private void showEditDialog() {
        FragmentManager fm = getFragmentManager();
        ComposeCommentFragment composeCommentFragment = (ComposeCommentFragment) ComposeCommentFragment.newInstance();
        //sets the target fragment for use later when sending results
        composeCommentFragment.setTargetFragment(DetailFragment.this, 300);
        composeCommentFragment.show(fm, "fragment_compose_comment");
    }

    private void purchaseItem() {
        //open venmo
        Intent implicit = new Intent(Intent.ACTION_VIEW, Uri.parse("venmo://paycharge?txn=pay&recipients="
                + offering.getUser().get("venmoName") + "&amount="
                + offering.getPrice().toString() + "&note=" + offering.getTitle()));

        //check if user has venmo installed
        if (implicit.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(implicit);
            purchase.purchaseItem(offering, btnPurchase, notificationLoader, getContext(), tvQuantityLeft);
        } else {
            Toast.makeText(getContext(), "You must have venmo installed", Toast.LENGTH_SHORT).show();
        }

    }
}