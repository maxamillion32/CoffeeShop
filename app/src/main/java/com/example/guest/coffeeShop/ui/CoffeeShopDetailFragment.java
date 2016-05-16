package com.example.guest.coffeeShop.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guest.coffeeShop.Constants;
import com.example.guest.coffeeShop.R;
import com.example.guest.coffeeShop.models.Coffee;
import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class CoffeeShopDetailFragment extends Fragment implements View.OnClickListener {
    private static final int MAX_WIDTH = 400;
    private static final int MAX_HEIGHT = 300;

    @Bind(R.id.coffeeShopsImageView) ImageView mImageLabel;
    @Bind(R.id.coffeeTextView) TextView mNameLabel;
    @Bind(R.id.ratingTextView) TextView mRatingLabel;
    @Bind(R.id.websiteTextView) TextView mWebsiteLabel;
    @Bind(R.id.phoneTextView) TextView mPhoneLabel;
    @Bind(R.id.addressTextView) TextView mAddressLabel;
    @Bind(R.id.menuTextView) TextView mMenuLabel;
    @Bind(R.id.saveCoffeeShopButton) TextView mSaveCoffeeShopButton;
    @Bind(R.id.snippetTextView) TextView mSnippetTextView;
    private SharedPreferences mSharedPreferences;
    private Coffee mCoffeeShop;
    private Integer mPosition;
    private ArrayList<Coffee> mCoffeeShops;

    public static CoffeeShopDetailFragment newInstance(ArrayList<Coffee> coffeeShops, Integer position) {
        CoffeeShopDetailFragment coffeeShopDetailFragment = new CoffeeShopDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("coffeeShop", Parcels.wrap(coffeeShops));
        coffeeShopDetailFragment.setArguments(args);
        return coffeeShopDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCoffeeShop = Parcels.unwrap(getArguments().getParcelable(Constants.EXTRA_KEY_COFFEESHOPS));
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPosition = getArguments().getInt(Constants.EXTRA_KEY_POSITION);
        mCoffeeShop = mCoffeeShops.get(mPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coffee_shop_detail, container, false);
        ButterKnife.bind(this, view);
        mSaveCoffeeShopButton.setOnClickListener(this);

        Picasso.with(view.getContext()).load(mCoffeeShop.getImage()).resize(MAX_WIDTH, MAX_HEIGHT).centerCrop().into(mImageLabel);
        mNameLabel.setText(mCoffeeShop.getName());
        mRatingLabel.setText("Rating: " + Double.toString(mCoffeeShop.getRating()) + "/5");
        mPhoneLabel.setText(mCoffeeShop.getPhone());
        mAddressLabel.setText(android.text.TextUtils.join(", ", mCoffeeShop.getAddress()));
        mMenuLabel.setText("Menu Provider: " + mCoffeeShop.getMenu());
//        mSnippetTextView.setText(mCoffeeShop.getSnippetText());
        mPhoneLabel.setOnClickListener(this);
        mAddressLabel.setOnClickListener(this);
//        mSnippetTextView.setOnClickListener(this);
        mWebsiteLabel.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == mPhoneLabel) {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL,
                    Uri.parse("tel:" + mCoffeeShop.getPhone()));
            startActivity(phoneIntent);
        }
        if (v == mAddressLabel) {
            Intent addressIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("geo:" + mCoffeeShop.getLatitude()
                            + "," + mCoffeeShop.getLongitude()
                            + "?q=(" + mCoffeeShop.getName() + ")"));
            startActivity(addressIntent);
        }
//        if (v == mSnippetTextView) {
//            Intent snippetTextIntent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse(mCoffeeShop.getSnippetText()));
//            startActivity(snippetTextIntent);
//        }
        if (v == mWebsiteLabel) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(mCoffeeShop.getWebsite()));
            startActivity(webIntent);
        }

        if (v == mSaveCoffeeShopButton) {
            String userUid = mSharedPreferences.getString(Constants.KEY_UID, null);
            Firebase userCoffeeShopsFirebaseRef = new Firebase(Constants.FIREBASE_URL_COFFEESHOPS).child(userUid);
            Firebase pushRef = userCoffeeShopsFirebaseRef.push();
            String coffeeShopPushId = pushRef.getKey();
            mCoffeeShop.setPushId(coffeeShopPushId);
            pushRef.setValue(mCoffeeShop);
            Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
        }
    }
}
