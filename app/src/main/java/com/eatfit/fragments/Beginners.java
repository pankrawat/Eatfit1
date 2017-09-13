package com.eatfit.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eatfit.R;
import com.eatfit.activity.Home;
import com.eatfit.adapter.VideosAdapter;
import com.eatfit.constants.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class Beginners extends Fragment implements View.OnClickListener {
    private RecyclerView videosList;
    private Context context;
    private VideosAdapter videosAdapter;
    private Workout workouts;
    private TextView videos, exercise;


    @Override
    public void onResume() {
        super.onResume();
        if (videosAdapter != null) {
            videosAdapter.notifyDataSetChanged();
        }
    }

    public Beginners() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beginners, container, false);
        context = getActivity();

        videosList = (RecyclerView) view.findViewById(R.id.videosList);
        videos = (TextView) view.findViewById(R.id.videoBeginners);
        exercise = (TextView) view.findViewById(R.id.exerciseBeginners);

        videosList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        videosAdapter = new VideosAdapter(context, ((Home) context).beginnersVideosArrayList, Constants.Beginners);
        videosList.setAdapter(videosAdapter);

        videos.setOnClickListener(this);
        exercise.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.videoBeginners:

                videos.setBackground(getResources().getDrawable(R.drawable.capsule_bgleft));
                exercise.setBackground(getResources().getDrawable(R.drawable.capsule_bg_right));
                videosAdapter = new VideosAdapter(context, ((Home) context).beginnersVideosArrayList, Constants.Beginners);
                videosList.setAdapter(videosAdapter);
                break;

            case R.id.exerciseBeginners:

                exercise.setBackground(getResources().getDrawable(R.drawable.capsule_bgright));
                videos.setBackground(getResources().getDrawable(R.drawable.capsule_bg_left));
                videosAdapter = new VideosAdapter(context, ((Home) context).beginnersExercisesArrayList, Constants.Beginners);
                videosList.setAdapter(videosAdapter);

                break;
        }

    }
}
