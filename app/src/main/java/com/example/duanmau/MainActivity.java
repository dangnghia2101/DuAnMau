package com.example.duanmau;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.View;

import com.example.duanmau.Activity.FragmentThanhVien;
import com.example.duanmau.Activity.FragmentTrangChinh;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openNavigation();

    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openNavigation(){
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        navigationView = findViewById(R.id.nav_menu);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.nav_TrangChinh_fragment, new FragmentTrangChinh()).commit();
        navigationView.setCheckedItem(R.id.nav_PhieuMuon);

        // Event CLick navigation
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            Fragment temp;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_ThanhVien:
                        temp = new FragmentThanhVien();
                        break;
                    case R.id.nav_PhieuMuon:
                        temp = new FragmentTrangChinh();
                        break;
                    case R.id.nav_LoaiSach:
                        break;
                    case R.id.nav_Sach:
                        break;
                    case R.id.nav_Top10:
                        break;
                    case R.id.nav_DoanhThu:
                        break;
                    case R.id.nav_DoiMatKhau:
                        break;
                    case R.id.nav_DangXuat:
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_TrangChinh_fragment, temp).commit();

                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }


}