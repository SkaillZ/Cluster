package at.hsacevic.cluster;

import android.graphics.Movie;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.RawRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.InputStream;

/*
 * Cluster Music App - Sacevic Haris
 */

public class AboutFragment extends Fragment {

    VideoView video;

    public AboutFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.about, container, false);


        //
        return rootView;
    }
}
