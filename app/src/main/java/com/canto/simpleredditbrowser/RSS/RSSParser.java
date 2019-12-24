package com.canto.simpleredditbrowser.RSS;

import com.canto.simpleredditbrowser.model.Author;
import com.canto.simpleredditbrowser.model.Entry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class RSSParser{

    List<Entry> entrys= new ArrayList<Entry>();
    Entry entry;
    Author author;
    String text;

    public List<Entry> getEntrys() {
        return entrys;
    }

    public List<Entry> parse(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        //Un tag ouvert indique que l'on crée une nouvelle instance d'un objet
                        if (tagName.equalsIgnoreCase("entry")) {
                            entry = new Entry();
                        }
                        if (tagName.equalsIgnoreCase("author")) {
                            author = new Author();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        //On garde les textes en mémoire à chaque passage au cas où on en a besoin
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase("name")) {
                            author.setName(text);
                        }
                        if (tagName.equalsIgnoreCase("uri")) {
                            author.setUri(text);
                        }
                        if(tagName.equalsIgnoreCase("author")){
                            entry.setAuthor(author);
                        }
                        if (tagName.equalsIgnoreCase("updated")) {
                            entry.setUpdated(text);
                        }
                        if (tagName.equalsIgnoreCase("title")) {
                            entry.setTitle(text);
                        }
                        if(tagName.equalsIgnoreCase("entry")){
                            entrys.add(entry);
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
