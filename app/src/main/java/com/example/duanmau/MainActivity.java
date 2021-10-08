package com.example.duanmau;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duanmau.Fragment.FragPhieuMuon;
import com.example.duanmau.Fragment.FragmentDoanhThu;
import com.example.duanmau.Fragment.FragmentDoiMatKhau;
import com.example.duanmau.Fragment.FragmentLoaiSach;
import com.example.duanmau.Fragment.FragmentSach;
import com.example.duanmau.Fragment.FragmentThanhVien;
import com.example.duanmau.Fragment.FragmentTrangChinh;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public NavigationView navigationView;

    TextView tvNameNavigation;
    ImageView imgAvatarNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openNavigation();

        hideNavigation();

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

        getSupportFragmentManager().beginTransaction().replace(R.id.nav_TrangChinh_fragment, new FragPhieuMuon()).commit();
        navigationView.setCheckedItem(R.id.nav_PhieuMuon);

        //Ánh xạ đến textView header,
        View headerView = navigationView.getHeaderView(0);
        tvNameNavigation = headerView.findViewById(R.id.tv_nameNavigation);
        imgAvatarNavigation = headerView.findViewById(R.id.imv_avaterNagigation);

        //lấy intent để đưa tên lên Header
        Intent intent = getIntent();
        tvNameNavigation.setText(intent.getStringExtra("HoTen"));
        Picasso.get().load(intent.getStringExtra("Avatar")).into(imgAvatarNavigation);

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
                        temp = new FragPhieuMuon();
                        break;
                    case R.id.nav_LoaiSach:
                        temp =  new FragmentLoaiSach();
                        break;
                    case R.id.nav_Sach:
                        temp = new FragmentSach();
                        break;
                    case R.id.nav_Top10:
                        break;
                    case R.id.nav_DoanhThu:
                        temp = new FragmentDoanhThu();
                        break;
                    case R.id.nav_DoiMatKhau:
                        temp = new FragmentDoiMatKhau();
                        break;
                    case R.id.nav_DangXuat:
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        SharedPreferences pref =getSharedPreferences("USER_FILE", MODE_PRIVATE);
                        SharedPreferences.Editor edit = pref.edit();
                        edit.clear();
                        edit.commit();
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_TrangChinh_fragment, temp).commit();

                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    // Ẩn các tác vụ thành viên và thủ thư không có quyền
    private void hideNavigation(){
        Intent intent = getIntent();
        int quyen = intent.getIntExtra("Quyen", 3);

        if(quyen==3) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_DoanhThu).setVisible(false);
            menu.findItem(R.id.nav_ThanhVien).setVisible(false);
        }
    }

}