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

import com.canto.simpleredditbrowser.model.Comment;
import com.canto.simpleredditbrowser.model.Entry;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

public class CommentListAdapter extends ArrayAdapter<Comment> {

    private static final String TAG = "CommentListAdapter";

    private Context mContext;
    private int mRessource;
    private int lastPosition = -1;

    private static class ViewHolder{
        TextView comment;
        TextView authorName;
        TextView updated;
    }

    //Constructeur

    public CommentListAdapter(Context c, int ressource, List<Comment> comments){
        super(c, ressource, comments);
        mContext = c;
        mRessource = ressource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Get les informations du commentaire
        String comment = getItem(position).getComment();
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
                holder.comment = (TextView) convertView.findViewById(R.id.comment);
                holder.authorName = (TextView) convertView.findViewById(R.id.author);
                holder.updated = (TextView) convertView.findViewById(R.id.updated);

                result = convertView;

                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
                result = convertView;
            }

            lastPosition = position;

            holder.comment.setText(comment);
            holder.authorName.setText(authorName);
            holder.updated.setText(updated);

            return convertView;
        }catch (IllegalArgumentException e){
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
            return convertView;
        }

    }
}
