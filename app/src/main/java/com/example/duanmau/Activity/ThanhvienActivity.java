package com.example.duanmau.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.duanmau.MainActivity;
import com.example.duanmau.R;
import com.google.android.material.navigation.NavigationView;

public class ThanhvienActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanhvien);

        OpenNavigasionView();
    }

    public void OpenNavigasionView(){
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        navigationView = findViewById(R.id.nav_menu);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);


        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Event CLick navigation
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_ThanhVien:
                        Toast.makeText(ThanhvienActivity.this, "Click thành viên", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ThanhvienActivity.this, ThanhvienActivity.class));
                        break;
                    case R.id.nav_PhieuMuon:
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

                return false;
            }
        });
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
}