package com.canto.simpleredditbrowser.RSS;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.canto.simpleredditbrowser.ListAdapter;
import com.canto.simpleredditbrowser.R;
import com.canto.simpleredditbrowser.model.Entry;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Scanner;

public class Downloader extends AsyncTask<Void, Void, Object> {

    private final String TAG = "Downloader";

    Context c;
    String urlAdress;
    ListView lv;

    ProgressDialog pd;
    public List<Entry> entrys;

    public Downloader(Context c, String urlAdress, ListView lv) {
        this.c = c;
        this.urlAdress = urlAdress;
        this.lv = lv;
    }

    //Permet d'afficher un écran de chargement le temps de charger les données
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(c);
        pd.setTitle("Téléchargement des données");
        pd.setMessage("Téléchargement........ Veuillez patienter");
        pd.show();
    }

    //Téléchargement des données
    @Override
    protected Object doInBackground(Void... voids) {
        return this.downloadData();
    }

    //Retire l'écran de chargement et traite le résultat
    @Override
    protected void onPostExecute(Object data) {
        super.onPostExecute(data);
        pd.dismiss();

        if(data.toString().startsWith("Error")){
            Toast.makeText(c, data.toString(), Toast.LENGTH_SHORT).show();
        }
        else{
            //Parsing
//            Log.d(TAG, "onPostExecute : parsing");
//            if(data == null)Log.d(TAG, "onPostExecute : InputStream à parser est null");
//            if(data != null)Log.d(TAG, "onPostExecute : InputStream à parser n'est pas null");
            RSSParser parser = new RSSParser();
            Log.d(TAG, "onPostExecute : Parser créé");
            entrys = parser.parse((InputStream) data);
//            if(entrys == null)Log.d(TAG, "onPostExecute : la liste d'entrys en sortie du Parsing est nulle");
//            if(entrys != null)Log.d(TAG, "onPostExecute : la liste d'entrys en sortie du Parsing n'est pas nulle");
            Log.d(TAG, Integer.toString(entrys.size()));

            ListAdapter listAdapter = new ListAdapter(c, R.layout.card_view_layout, entrys);
            lv.setAdapter(listAdapter);
            listAdapter.addAll(entrys);

        }
    }

    private Object downloadData(){
        Object con = Connector.connect(urlAdress);
        if(con.toString().startsWith("Error")){
            return con.toString();
        }

        try{
            HttpURLConnection connection = (HttpURLConnection) con;
            InputStream is = new BufferedInputStream(connection.getInputStream());
            Log.d(TAG, "Taille de l'input stream : " + is.available());
            return(is);
        }catch(IOException e){
            e.printStackTrace();
            return ErrorTracker.IO_ERROR;
        }
    }
}
