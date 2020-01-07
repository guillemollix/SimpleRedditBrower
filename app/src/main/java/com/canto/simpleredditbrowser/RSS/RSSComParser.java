package com.canto.simpleredditbrowser.RSS;

import android.util.Log;

import com.canto.simpleredditbrowser.model.Author;
import com.canto.simpleredditbrowser.model.Comment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RSSComParser {

    private final String TAG = "RSSComParser";
    final Pattern pattern = Pattern.compile("<p>(.+?)</p>", Pattern.DOTALL);

    private Matcher matcher;
    private List<Comment> comments = new ArrayList<Comment>();
    private Comment comment;
    private Author author;
    private String text;
    private boolean firstEntrySkipped;

    public List<Comment> getComments() {
        return comments;
    }

    public List<Comment> parse(InputStream is){
        try{

            Log.d(TAG, "Try de la fonction parse");

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();


            parser.setInput(is, "UTF-8");
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                String tagName = parser.getName();

                Log.d(TAG, "boucle while");
                Log.d(TAG, "Tag : " + tagName);
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if(tagName.equalsIgnoreCase("entry")){
                            comment = new Comment();
                        }
                        if(tagName.equalsIgnoreCase("author")){
                            author = new Author();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(tagName.equalsIgnoreCase("name")) {
                            if(author != null && firstEntrySkipped)author.setName(text);
                        }
                        if(tagName.equalsIgnoreCase("uri")) {
                            if(author != null && firstEntrySkipped)author.setUri(text);
                        }
                        if(tagName.equalsIgnoreCase("author")) {
                            if(comment != null && firstEntrySkipped) comment.setAuthor(author);
                        }
                        if(tagName.equalsIgnoreCase("updated")) {
                            if(comment != null && firstEntrySkipped) comment.setUpdated(text);
                        }
                        if(tagName.equalsIgnoreCase("content")) {
                            matcher = pattern.matcher(text);
                            matcher.find();
                            if(comment != null && firstEntrySkipped)comment.setComment(matcher.group(1).replace("&#39;", "'"));
                        }
                        if(tagName.equalsIgnoreCase("entry")) {
                            if(comment != null && firstEntrySkipped)comments.add(comment);
                            if(comment != null)Log.d(TAG, "Nouveau commentaire :" + comment);
                            if(!firstEntrySkipped) firstEntrySkipped = true;
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
        catch (IllegalStateException e) {e.printStackTrace();}

        return comments;
    }
}
