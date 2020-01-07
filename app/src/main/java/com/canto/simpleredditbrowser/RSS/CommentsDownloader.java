package com.canto.simpleredditbrowser.RSS;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.canto.simpleredditbrowser.CommentListAdapter;
import com.canto.simpleredditbrowser.R;
import com.canto.simpleredditbrowser.model.Comment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

public class CommentsDownloader extends AsyncTask<String, Integer, List<Comment>> {

    private final String TAG = "CommentsDownloader";

    private String urlString;
    private List<Comment> comments;
    private Context context;
    private ListView lv;
    private ProgressBar pb;

    public CommentsDownloader(String url, Context c, ListView lv, ProgressBar pb){
        this.urlString = url + ".rss";
        this.context = c;
        this.lv = lv;
        this.pb = pb;
    }

    @Override
    protected List<Comment> doInBackground(String... strings) {
        InputStream is = null;

        try {
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
            if (response == HttpURLConnection.HTTP_OK) is = connection.getInputStream();

            //Redirection d'un sub à un autre comme dans le cas de r/random
            if (response == HttpURLConnection.HTTP_MOVED_PERM || response == HttpURLConnection.HTTP_MOVED_TEMP || response == HttpURLConnection.HTTP_SEE_OTHER) {
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
            if (Integer.toString(response).startsWith("4")) {
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
            RSSComParser parser = new RSSComParser();
            comments = parser.parse(is);


        }catch (MalformedURLException e){
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return comments;
    }

    @Override
    protected void onPostExecute(List<Comment> comments) {
        CommentListAdapter adapter = new CommentListAdapter(context, R.layout.comment_layout, comments);
        lv.setAdapter(adapter);

        pb.setVisibility(View.GONE);
    }
}
