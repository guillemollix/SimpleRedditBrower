package com.canto.simpleredditbrowser.RSS;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.canto.simpleredditbrowser.ListAdapter;
import com.canto.simpleredditbrowser.R;
import com.canto.simpleredditbrowser.model.Entry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Downloader extends AsyncTask<String, Integer, List<Entry>> {

    private final String TAG = "Downloader";
    private final String BASE_URL = "https://www.reddit.com/";
    private final String END_URL = ".rss";
    private List<Entry> entrys;
    private Context context;
    private ListView lv;

    public Downloader(Context c, ListView lv){
        this.context = c;
        this.lv = lv;
    }


    @Override
    protected List<Entry> doInBackground(String... strings) {
        String urlString = BASE_URL + strings[0] + END_URL;
        InputStream is;

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
            is = connection.getInputStream();

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
            RSSParser parser = new RSSParser();
            entrys = parser.parse(is);


        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entrys;
    }

    @Override
    protected void onPostExecute(List<Entry> entrys) {
        ListAdapter adapter = new ListAdapter(context, R.layout.card_view_layout, entrys);
        lv.setAdapter(adapter);
    }
}
