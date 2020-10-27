package com.example.andrewhawkins.login;

/**
 * Created by riana on 12/22/2016.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class Left extends android.support.v4.app.Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.middle, container, false);
        LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.ll);
        ll.setBackgroundColor(Color.GRAY);
        TextView tv = new TextView(getActivity());
        tv.setText("Left");
        return rootView;
    }
    public static Left newInstance(int sectionNumber) {
        Left fragment = new Left();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
}
