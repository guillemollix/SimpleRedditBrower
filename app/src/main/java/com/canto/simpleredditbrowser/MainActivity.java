package com.canto.simpleredditbrowser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";
    private static final String FILE_NAME = "my_subs.txt";
    private DrawerLayout drawer;
    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Rends la toolbar en haut de l'écran réactive
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Simple Reddit Browser");


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Rends le drawer accessible par un bouton dans la toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Affiche le fragment d'accueil par défaut
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedFragment("")).commit();
            navigationView.setCheckedItem(R.id.menu_home);
        }

        //Rend utilisable la barre de recherche du Drawer
        Button searchButton = (Button) navigationView.getHeaderView(0).findViewById(R.id.search_button);
        final EditText searchBar = navigationView.getHeaderView(0).findViewById(R.id.search_bar);
        final SubMenu subMenu = navigationView.getMenu().addSubMenu("My Subreddits");

        //Initialisation de la base de données pour l'historique
        dataBaseHelper = new DataBaseHelper(this);
        List<String> subs = retrieveHistory();
        for(String sub : subs){
            subMenu.add(sub);
        }

        searchButton.setOnClickListener(new View.OnClickListener() { //Avec le bouton
            @Override
            public void onClick(View v) {
                String subSearched = "r/" + searchBar.getText();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedFragment(subSearched)).commit();
                toolbar.setTitle(subSearched);
                subMenu.add(subSearched);
                dataBaseHelper.addData(subSearched);

            }
        });
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String subSearched = "r/" + searchBar.getText();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedFragment(subSearched)).commit();
                    toolbar.setTitle(subSearched);
                    subMenu.add(subSearched);
                    dataBaseHelper.addData(subSearched);
                    return true;
                }
                return false;
            }
        });





    }

    //Gère l'ouverture des fragments en cliquant sur les boutons de la barre de navigation
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        switch(menuItem.getItemId()){
            case R.id.menu_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedFragment("")).commit();
                toolbar.setTitle("Simple Reddit Browser");
                break;

            case R.id.menu_all:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedFragment("r/all")).commit();
                toolbar.setTitle("r/all");
                break;

            case R.id.menu_random:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedFragment("r/random")).commit();
                toolbar.setTitle("r/random");
                break;

            default:
                String subName = menuItem.getTitle().toString();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedFragment(subName)).commit();
                toolbar.setTitle(subName);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    //Permet de fermer le drawer avec back lorsqu'il est ouvert
    @Override
    public void onBackPressed(){

        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    //Affichage du menu de la toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    //Mise en fonctionnement des boutons
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Toolbar toolbar = findViewById(R.id.toolbar);

        switch (item.getItemId()){
            case R.id.toolbar_refresh:
                String title = String.valueOf(toolbar.getTitle());
                Log.d(TAG, title +"a");
                if(title == "Simple Reddit Browser")getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedFragment("")).commit();
                else{
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedFragment(title)).commit();
                }
                break;

            case R.id.toolbar_menu1:
                dataBaseHelper.deleteAll();
                Toast toast = Toast.makeText(this, "Restart the app to validate", Toast.LENGTH_LONG);
                toast.show();

        }

        return super.onOptionsItemSelected(item);
    }

    public List<String> retrieveHistory(){
        Cursor data = dataBaseHelper.getData();
        List<String> subs = new ArrayList<>();
        while (data.moveToNext()){
            subs.add(data.getString(1));
        }
        return subs;
    }
}
