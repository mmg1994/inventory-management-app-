package com.example.sawasawa.activity;




import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;

import com.example.sawasawa.R;

//public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

public class SettingsActivity extends AppCompatActivity {


    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, settings, share, about, logout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //################################################
        drawerLayout = findViewById(R.id.drawer_layout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        about = findViewById(R.id.about);
        logout = findViewById(R.id.logout);
        settings = findViewById(R.id.settings);
        share = findViewById(R.id.share);


        menu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                openDrawer(drawerLayout);
            }
        });

        home.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                redirectActivity(SettingsActivity.this, MainActivity.class);

            }
        });

        settings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                recreate();
            }
        });
        share.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                redirectActivity(SettingsActivity.this, ShareActivity.class);
            }
        });
        about.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                redirectActivity(SettingsActivity.this, AboutActivity.class);
            }
        });
        logout.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(SettingsActivity.this,"logout", Toast.LENGTH_SHORT).show();
            }
        });



    }


    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity){
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    protected void onPause(){
        super.onPause();
        closeDrawer(drawerLayout);
    }

}