package com.canto.simpleredditbrowser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.canto.simpleredditbrowser.RSS.CommentsDownloader;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private static String entryLink;
    private static String entryTitle;
    private static String entryAuthor;
    private static String entryUpdated;
    private static String entryThumbnail;

    private int defaultImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        setupImageLoader();

        Intent incomingIntent = getIntent();
        entryLink = incomingIntent.getStringExtra("@string/entry_link");
        entryTitle = incomingIntent.getStringExtra("@string/entry_title");
        entryAuthor = incomingIntent.getStringExtra("@string/entry_author");
        entryUpdated = incomingIntent.getStringExtra("@string/entry_updated");
        entryThumbnail = incomingIntent.getStringExtra("@string/entry_thumbnail");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView textTitle = (TextView) findViewById(R.id.entryTitle);
        TextView textAuthor = (TextView) findViewById(R.id.entryAuthor);
        TextView textUpdated = (TextView) findViewById(R.id.entryDateTime);
        ImageView thumbnail = (ImageView) findViewById(R.id.entryThumbnail);
        Button btnReply = (Button) findViewById(R.id.postReplyBtn);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarEntry);
        ProgressBar commentsProgressBar = (ProgressBar) findViewById(R.id.progressBarComments);
        ListView commentsListView = (ListView) findViewById(R.id.commentsListView);

        textTitle.setText(entryTitle);
        toolbar.setTitle(entryTitle);
        textAuthor.setText(entryAuthor);
        textUpdated.setText(entryUpdated);
        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommentActivity.this, WebActivity.class);
                intent.putExtra("@string/web_url", entryLink);
                CommentActivity.this.startActivity(intent);
            }
        });

        displayImage(entryThumbnail, thumbnail, progressBar);

        CommentsDownloader downloader = new CommentsDownloader(entryLink, CommentActivity.this, commentsListView, commentsProgressBar);
        downloader.execute();

    }

    private void displayImage(String imageURL, ImageView imageView, final ProgressBar progressBar){

        //Création de l'ImageLoader
        ImageLoader imageLoader = ImageLoader.getInstance();

        //Création des options d'affichage
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build();

        //Téléchargement et affichage des images à partir de leur url
        imageLoader.displayImage(imageURL, imageView, options , new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(View.GONE);
            }

        });

    }

    //Fonction servant à mettre en place Universal Image Loader
    private void setupImageLoader(){

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                CommentActivity.this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        defaultImage = CommentActivity.this.getResources().getIdentifier("@drawable/reddit_alien",null,CommentActivity.this.getPackageName());

    }
}
