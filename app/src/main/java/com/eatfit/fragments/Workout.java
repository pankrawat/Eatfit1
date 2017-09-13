package com.eatfit.fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.Space;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.activity.Home;
import com.eatfit.adapter.VideosAdapter;
import com.eatfit.constants.Constants;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.eatfit.model.VideosModel;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Workout extends Fragment {

    ViewPagerAdapter adapter;
    Beginners beginners = new Beginners();
    Intermediate intermediate = new Intermediate();
    Advanced advanced = new Advanced();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context context;
    private RelativeLayout upperPanel;
    private SharedPreference spMain;
    private View spacer;
    private int actionBarHeight;
    /*public ArrayList<VideosModel> beginnersVideosArrayList, advancedVideosArrayList, intermediateVideosArrayList,
            beginnersExercisesArrayList, advancedExercisesArrayList, intermediateExercisesArrayList;*/

    public Workout() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        //spacerHeight = spacer.getHeight();
        /*if (Utils.isConnectingToInternet(context)) {
            ((Home) context).getVideoList();
        } else {
            Toast.makeText(context, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
        }*/
        upperPanel.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (2 * actionBarHeight)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        spacer = (View) view.findViewById(R.id.spacer);
        upperPanel = (RelativeLayout) view.findViewById(R.id.upperPanel);

        context = getActivity();
        spMain = spMain.getInstance(context);

        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }


        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        /*beginnersVideosArrayList = new ArrayList<>();
        beginnersExercisesArrayList = new ArrayList<>();
        intermediateVideosArrayList = new ArrayList<>();
        intermediateExercisesArrayList = new ArrayList<>();
        advancedVideosArrayList = new ArrayList<>();
        advancedExercisesArrayList = new ArrayList<>();


        if (Utils.isConnectingToInternet(context)) {
            getVideoList();
        } else {
            Toast.makeText(context, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
        }*/

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(beginners, "BEGINNERS");
        adapter.addFragment(intermediate, "INTERMEDIATE");
        adapter.addFragment(advanced, "ADVANCED");
        viewPager.setAdapter(adapter);
    }

    // ViewPager Adapter Classes
    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        FragmentManager manager;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            this.manager = manager;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            FragmentManager manager = this.manager;
            if (position <= getCount() && object != null) {
                FragmentTransaction trans = manager.beginTransaction();
                trans.remove((Fragment) object);
                trans.commit();
            }
        }
    }

}
