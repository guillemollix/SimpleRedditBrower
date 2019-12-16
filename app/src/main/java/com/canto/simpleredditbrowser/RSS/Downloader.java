package com.canto.simpleredditbrowser.RSS;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class Downloader extends AsyncTask<Void, Void, Object> {

    Context c;
    String urlAdress;
    ListView listView;

    ProgressDialog pd;

    public Downloader(Context c, String urlAdress, ListView listView) {
        this.c = c;
        this.urlAdress = urlAdress;
        this.listView = listView;
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
            Toast.makeText(c, data.toString(), Toast.LENGTH_SHORT);
        }
        else{
            //Parsing
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

            return(is);
        }catch(IOException e){
            e.printStackTrace();
            return ErrorTracker.IO_ERROR;
        }
    }
}
