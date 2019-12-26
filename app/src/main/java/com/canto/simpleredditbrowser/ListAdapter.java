package com.canto.simpleredditbrowser;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.canto.simpleredditbrowser.model.Author;
import com.canto.simpleredditbrowser.model.Entry;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends ArrayAdapter<Entry> {

    private static final String TAG = "ListAdapter";

    private Context mContext;
    private int mRessource;
    private int lastPosition = -1;

    private static class ViewHolder{
        TextView title;
        TextView authorName;
        TextView updated;
        ImageView thumbnail;
        ProgressBar mProgressBar;
    }

    //Constructeur

    public ListAdapter(Context c, int ressource, List<Entry> entrys){
        super(c, ressource, entrys);
        mContext = c;
        mRessource = ressource;

        setupImageLoader();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Get les informations du post
        String title = getItem(position).getTitle();
        String imgUrl = getItem(position).getThumbnail();
        String authorName = getItem(position).getAuthor().getName();
        String updated = getItem(position).getUpdated();


        try{

            //Créé la vue finale pour afficher l'animation
            final View result;

            //Créé le ViewHolder qui contiendra les informations du post
            final ViewHolder holder;

            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(mRessource, parent, false);
                holder= new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.cardTitle);
                holder.thumbnail = (ImageView) convertView.findViewById(R.id.cardImage);
                holder.authorName = (TextView) convertView.findViewById(R.id.cardAuthor);
                holder.updated = (TextView) convertView.findViewById(R.id.cardUpdated);
                holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.cardProgressDialog);

                result = convertView;

                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
                result = convertView;
            }

            lastPosition = position;

            holder.title.setText(title);
            holder.authorName.setText(authorName);
            holder.updated.setText(updated);

            //Création de l'ImageLoader
            ImageLoader imageLoader = ImageLoader.getInstance();

            int defaultImage = mContext.getResources().getIdentifier("@drawable/reddit_alien",null,mContext.getPackageName());

            //Création des options d'affichage
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();

            //Téléchargement et affichage des images à partir de leur url
            imageLoader.displayImage(imgUrl, holder.thumbnail, options , new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                }
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    holder.mProgressBar.setVisibility(View.GONE);
                }
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.mProgressBar.setVisibility(View.GONE);
                }
                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    holder.mProgressBar.setVisibility(View.GONE);
                }

            });

            return convertView;
        }catch (IllegalArgumentException e){
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
            return convertView;
        }

    }


    //Fonction servant à mettre en place Universal Image Loader
    private void setupImageLoader(){

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

    }
}
