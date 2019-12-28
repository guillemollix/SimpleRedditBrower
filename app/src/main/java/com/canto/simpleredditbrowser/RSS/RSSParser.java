package com.canto.simpleredditbrowser.RSS;

import android.util.Log;

import com.canto.simpleredditbrowser.model.Author;
import com.canto.simpleredditbrowser.model.Entry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class RSSParser{

    private final String TAG = "RSSParser";

    List<Entry> entrys= new ArrayList<Entry>();
    Entry entry;
    Author author;
    String text;

    public List<Entry> getEntrys() {
        return entrys;
    }

    public List<Entry> parse(InputStream is) {
        try {
//            Log.d(TAG, "Try de la fonction parse");

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();


            parser.setInput(is, "UTF-8");

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                //Log.d(TAG, "boucle while");
                String tagName = parser.getName();
                //Log.d(TAG, "Tag : " + tagName);
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        //Un tag ouvert indique que l'on crée une nouvelle instance d'un objet
                        if (tagName.equalsIgnoreCase("entry")) {
                            entry = new Entry();
                            //Log.d(TAG, "<entry>");
                        }
                        if (tagName.equalsIgnoreCase("author")) {
                            author = new Author();
                            //Log.d(TAG, "<author>");
                        }
                        break;

                    case XmlPullParser.TEXT:
                        //On garde les textes en mémoire à chaque passage au cas où on en a besoin
                        text = parser.getText();
//                        Log.d(TAG, text);
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase("name")) {
                            if(author != null)author.setName(text);
                        }
                        if (tagName.equalsIgnoreCase("uri")) {
                            if(author != null)author.setUri(text);
                        }
                        if(tagName.equalsIgnoreCase("author")){
                            if(entry != null)entry.setAuthor(author);
                        }
                        if (tagName.equalsIgnoreCase("updated")) {
                            if(entry != null)entry.setUpdated(text);
                        }
                        if (tagName.equalsIgnoreCase("title")) {
                            if(entry != null)entry.setTitle(text);
                        }

                        if (tagName.equalsIgnoreCase("content")) {
                            Log.d(TAG, text);
                            String[] splits = text.split("\"");
                            for(String split : splits){
                                if(split.contains("/comments/"))if(entry != null)entry.setLink(split);
                                if(split.contains(".jpg"))if(entry != null)entry.setThumbnail(split);
                            }
                        }

                        if(tagName.equalsIgnoreCase("entry")){
                            if(entry != null)entrys.add(entry);
                            if(entry != null)Log.d(TAG, entry.toString());
                        }

                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }


        } catch (XmlPullParserException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}

        return entrys;
    }
}
