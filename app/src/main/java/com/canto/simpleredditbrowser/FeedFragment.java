package com.canto.simpleredditbrowser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.canto.simpleredditbrowser.RSS.Downloader;

public class FeedFragment extends Fragment {

    private final String TAG = "FeedFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home , container, false);
        ListView listView = (ListView) view.findViewById(R.id.cardList);

        Downloader downloader = new Downloader(this.getContext(), listView);
        downloader.execute("");

        return view;
    }

}
