package com.example.doveletter.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doveletter.R;
import com.example.doveletter.fragments.ContactFragment;
import com.example.doveletter.fragments.DraftFragment;
import com.example.doveletter.fragments.ReceiveFragment;
import com.example.doveletter.fragments.RubbishFragment;
import com.example.doveletter.fragments.SentFragment;
import com.example.doveletter.utils.StatusBarUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView iv_home;
    private TextView tv_title;
    private FloatingActionButton floatingActionButton;
//    private TextView tv_host_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        StatusBarUtil.setStatusBarMode(this, true, R.color.colorMain);
        initView();
        applyPermission();
    }

    private void applyPermission() {
        //权限集合
//        List<String> permissionList = new ArrayList<>();
////        if (ContextCompat.checkSelfPermission(this, Manifest.permission.
////                READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
////            permissionList.add(Manifest.permission.READ_PHONE_STATE);
////        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.
//                WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.
//                READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//        }
//        if (!permissionList.isEmpty()) {
//            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
//            ActivityCompat.requestPermissions(this, permissions, 1);
//        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            }, BAIDU_READ_PHONE_STATE);
        }
    }

    private static final int BAIDU_READ_PHONE_STATE = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case BAIDU_READ_PHONE_STATE:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才可以使用本程序!", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    private void initView() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_views);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        iv_home = findViewById(R.id.iv_home);
        iv_home.setOnClickListener(this);
        tv_title = findViewById(R.id.tv_title);
//        floatingActionButton=findViewById(R.id.btn_addcontact);

        if (getIntent() != null) {
            int id = getIntent().getIntExtra("id", -1);
            switch (id) {
                case 5:
                    chooseContact();
                    break;
            }
        }
//        tv_host_address=findViewById(R.id.tv_hostaddress);
//        tv_host_address.setText(MyApplication.userInfo.getUseraddress());
    }

    private void chooseContact() {
        tv_title.setText("通讯录");
        Fragment fragment = new ContactFragment();
        replaceFragment(fragment);
        iv_home.setVisibility(View.GONE);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_inbox:
                tv_title.setText("收件箱");
                fragment = new ReceiveFragment();
                replaceFragment(fragment);
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_sent:
                tv_title.setText("已发送");
                fragment = new SentFragment();
                replaceFragment(fragment);
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_drafts:
                tv_title.setText("草稿箱");
                fragment = new DraftFragment();
                replaceFragment(fragment);
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_rubbish:
                tv_title.setText("垃圾箱");
                fragment = new RubbishFragment();
                replaceFragment(fragment);
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_address:
                tv_title.setText("通讯录");
                fragment = new ContactFragment();
                replaceFragment(fragment);
                drawerLayout.closeDrawers();
                break;
        }
        return false;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_layout, fragment);
        transaction.commit();
    }
}
