package com.canto.simpleredditbrowser;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.canto.simpleredditbrowser.RSS.Downloader;
import com.canto.simpleredditbrowser.model.Entry;
import com.canto.simpleredditbrowser.model.Author;

import java.util.List;

public class HomeFragment extends Fragment {

    private final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home , container, false);
        ListView listView = (ListView) view.findViewById(R.id.cardList);

        Downloader downloader = new Downloader(getContext(), "http://www.reddit.com/.rss", listView);
        downloader.execute();

        return view;
    }

}
