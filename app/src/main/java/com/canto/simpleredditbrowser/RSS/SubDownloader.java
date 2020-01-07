package com.canto.simpleredditbrowser.RSS;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.canto.simpleredditbrowser.CommentActivity;
import com.canto.simpleredditbrowser.ListAdapter;
import com.canto.simpleredditbrowser.R;
import com.canto.simpleredditbrowser.model.Entry;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SubDownloader extends AsyncTask<String, Integer, List<Entry>> {

    private final String TAG = "SubDownloader";
    private final String BASE_URL = "https://www.reddit.com/";
    private final String END_URL = ".rss";
    private List<Entry> entrys;
    private Context context;
    private ListView lv;

    public SubDownloader(Context c, ListView lv){
        this.context = c;
        this.lv = lv;
    }

    @Override
    protected List<Entry> doInBackground(String... strings) {
        String urlString = BASE_URL + strings[0] + END_URL;
        InputStream is = null;

        try{
            //Initialisation de la connexion
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            //Récupération du code de réponse et du corps de la réponse
            int response = connection.getResponseCode();
            Log.d(TAG, "Code de réponse de la connexion : " + response);


            //Le subreddit à atteindre existe
            if(response == HttpURLConnection.HTTP_OK)is = connection.getInputStream();

            //Redirection d'un sub à un autre comme dans le cas de r/random
            if(response == HttpURLConnection.HTTP_MOVED_PERM || response == HttpURLConnection.HTTP_MOVED_TEMP || response == HttpURLConnection.HTTP_SEE_OTHER){
                String newUrl = connection.getHeaderField("Location");
                url = new URL(newUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                is = connection.getInputStream();
            }

            //Le sub n'existe pas ou refuse la connexion
            if(Integer.toString(response).startsWith("4")){
                return null;
            }

            /*//Transformation de l'InputStream en String
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            while(true){
                int count = is.read(buffer, 0, bufferSize);
                if(count == -1){
                    break;
                }
                os.write(buffer);
            }
            os.close();

            String body = new String(os.toByteArray(), "UTF-8");
            Log.d(TAG, "Contenu du body : " + body);*/

            //Parsing
            RSSSubParser parser = new RSSSubParser();
            entrys = parser.parse(is);


        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entrys;
    }

    @Override
    protected void onPostExecute(final List<Entry> entrys) {

        if(entrys != null) {
            ListAdapter adapter = new ListAdapter(context, R.layout.card_view_layout, entrys);
            lv.setAdapter(adapter);


            //Ajout de la possibilité de cliquer sur les posts pour lire les commentaires du post
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Log.d(TAG, "OnItemClick : Clicked :" + entrys.get(position));
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("@string/entry_link", entrys.get(position).getLink());
                    intent.putExtra("@string/entry_title", entrys.get(position).getTitle());
                    intent.putExtra("@string/entry_updated", entrys.get(position).getUpdated());
                    intent.putExtra("@string/entry_author", entrys.get(position).getAuthor().toString());
                    intent.putExtra("@string/entry_thumbnail", entrys.get(position).getThumbnail());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }
}
