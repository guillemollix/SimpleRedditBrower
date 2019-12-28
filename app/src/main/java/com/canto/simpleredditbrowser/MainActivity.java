package com.canto.simpleredditbrowser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Rends la toolbar en haut de l'écran réactive
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        }


        return super.onOptionsItemSelected(item);
    }
}
