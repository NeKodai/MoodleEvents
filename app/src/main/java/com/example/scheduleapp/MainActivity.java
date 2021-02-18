package com.example.scheduleapp;

import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;

import java.io.File;

/**
 * メインスレッド
 */
public class MainActivity extends AppCompatActivity {

    private WebView webView;

    /**
     * ビューの初期設定
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MainFragment fragment = new MainFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container,fragment);
        transaction.commit();
        this.webView = findViewById(R.id.webView1);

        //Drawerの設定
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
             @Override
             public boolean onNavigationItemSelected(MenuItem item) {
                 System.out.println(item);
                 drawer.closeDrawers();
                 FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                 getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                 switch(item.getItemId()){
                     case R.id.event_list_item:
                         if(!(getSupportFragmentManager().findFragmentById(R.id.container) instanceof MainFragment)){
                             transaction.replace(R.id.container,new MainFragment());
                             transaction.commit();
                         }
                         break;
                     case R.id.add_event_item:
                         transaction.replace(R.id.container,new CreateEventFragment());
                         transaction.addToBackStack(null);
                         transaction.commit();
                         break;
                     case R.id.setting_item:
                         transaction.replace(R.id.container,new SettingFragment());
                         transaction.addToBackStack(null);
                         transaction.commit();
                         break;
                 }
                 return false;
             }
         });
        actionBarDrawerToggle.syncState();
    }

    /**
     * このアクティビティから画面が離れた時の処理
     */
    @Override
    protected void onPause(){
        //this.timer.cancel();
        super.onPause();
        System.out.println("中断しました");
        return;
    }

    /**
     * メニュー画面を作成
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //this.controller.setSettingActivity();
            return true;
        }
        else if(id == R.id.action_reset){
            File aFile = new File(getFilesDir(),"schedule.json");
            if(aFile.exists()){
                aFile.delete();
                Toast.makeText(this, "リセットしました", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public WebView getWebView(){
        return this.webView;
    }
}
