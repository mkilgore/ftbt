package com.mkilgore.ftbt.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.mkilgore.ftbt.R;
import com.mkilgore.ftbt.singleton.BusProvider;
import com.mkilgore.ftbt.singleton.bluetooth.BluetoothManager;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mkilgore.ftbt.view.controller.ButtonsFragment;
import com.mkilgore.ftbt.view.controller.JoyStickFragment;
import com.mkilgore.ftbt.view.controller.ToggleButtonsFragment;
import com.mkilgore.ftbt.view.controller.TwoAxisFragment;

import java.util.Random;

import app.akexorcist.bluetotohspp.library.BluetoothState;

public class MainActivity extends AppCompatActivity implements Drawer.OnDrawerItemClickListener, FullscreenListener, ActivityListener {
    private static final String KEY_TWO_AXIS = "two_axis";
    private static final String JOY_STICK = "joy_stick";
    private static final String BUTTONS = "buttons";
    private static final String TOGGLE_BUTTONS = "toggle_buttons";
    private static final String KEY_DEVICE_CONNECTION = "device_connection";

    private Toolbar tbMain;
    private Drawer ndMenu;
    private boolean isFullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindView();
        initView();
        setupComponent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Assent.setActivity(this, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.getInstance().getProvider().register(this);
        initialBluetoothService();
    }

    private void initialBluetoothService() {
        if (BluetoothManager.getInstance().isBluetoothAvailable() &&
                !BluetoothManager.getInstance().isServiceAvailable()) {
            BluetoothManager.getInstance().setupService();
            BluetoothManager.getInstance().startService();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.getInstance().getProvider().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothManager.getInstance().stopService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing())
            Assent.setActivity(this, null);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Assent.handleResult(permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                BluetoothManager.getInstance().connect(data);
            }
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                BluetoothManager.getInstance().setupService();
                BluetoothManager.getInstance().startService();
            } else {
                // Do something if user doesn't choose any device (Pressed back)
            }
        }
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        String item = drawerItem.getTag().toString();
        if (item.equalsIgnoreCase(KEY_DEVICE_CONNECTION)) {
            BluetoothManager.getInstance().openBluetoothConnection(this);
        } else if (item.equalsIgnoreCase("settings")) {
            Log.e("Check", "Settings");
        } else if (item.equalsIgnoreCase(JOY_STICK)) {
            openJoyStickFragment();
        } else if (item.equalsIgnoreCase(BUTTONS)) {
            openButtonsFragment();
        } else if (item.equalsIgnoreCase(TOGGLE_BUTTONS)) {
            openToggleButtonsFragment();
        } else if (item.equalsIgnoreCase(KEY_TWO_AXIS)) {
            openTwoAxisFragment();
        }
        return true;
    }

    private void bindView() {
        tbMain = (Toolbar) findViewById(R.id.tb_main);
    }

    private void initView() {
        setupToolbar();
        setupNavigationDrawer();
        checkBluetoothAvailability();
    }

    private void setupToolbar() {
        setSupportActionBar(tbMain);
        setTitle("FTBT");
    }

    private void setupComponent() {
        Assent.setActivity(this, this);
        requestLocationPermission();
    }

    private void requestLocationPermission() {
        requestPermission(new String[]{Assent.ACCESS_COARSE_LOCATION}, new AssentCallback() {
            @Override
            public void onPermissionResult(PermissionResultSet permissionResultSet) {
                if (!permissionResultSet.allPermissionsGranted()) {
                    finish();
                    Toast.makeText(MainActivity.this, "You must grant location permission.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupNavigationDrawer() {
        ProfileDrawerItem section = new ProfileDrawerItem()
                .withSetSelected(false)
                .withSetSelected(false)
                .withEnabled(false);

        PrimaryDrawerItem joyTypeJoyStick = new PrimaryDrawerItem().withName("Joy Stick").withTag(JOY_STICK).withLevel(2);
        PrimaryDrawerItem buttonsDrawer = new PrimaryDrawerItem().withName("Buttons").withTag(BUTTONS).withLevel(2);
        PrimaryDrawerItem toggleButtonsDrawer = new PrimaryDrawerItem().withName("Toggle Buttons").withTag(TOGGLE_BUTTONS).withLevel(2);

        SectionDrawerItem sectionDrawerItem = new SectionDrawerItem().withName("Joystick Type").withTextColorRes(R.color.colorAccent);
        PrimaryDrawerItem deviceDrawerItem = new PrimaryDrawerItem().withName("Device Connection").withTag(KEY_DEVICE_CONNECTION).withIcon(GoogleMaterial.Icon.gmd_devices);
        PrimaryDrawerItem settingsDrawerItem = new PrimaryDrawerItem().withName("Settings").withTag("settings").withIcon(GoogleMaterial.Icon.gmd_settings);

        ndMenu = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(tbMain)
                .withCloseOnClick(true)
                .withSelectedItemByPosition(4)
                .withDelayDrawerClickEvent(300)
                .withDisplayBelowStatusBar(true)
                .withTranslucentStatusBar(false)
                .withOnDrawerItemClickListener(this)
                .addDrawerItems(section, deviceDrawerItem, settingsDrawerItem, sectionDrawerItem, joyTypeJoyStick, toggleButtonsDrawer, buttonsDrawer)
                .build();
    }

    @Override
    public void enterFullscreen() {
        isFullscreen = true;
        int uiOption = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        }
        getWindow().getDecorView().setSystemUiVisibility(uiOption);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        ndMenu.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void exitFullscreen() {
        isFullscreen = false;
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
        ndMenu.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void toggleFullscreen() {
        if (isFullscreen) {
            exitFullscreen();
        } else {
            enterFullscreen();
        }
    }

    @Override
    public void setActivityTitle(int titleResId) {
        setTitle(titleResId);
    }

    @Override
    public void setActivityTitle(String title) {
        setTitle(title);
    }

    public void replaceFragment(Fragment fragment, String fragmentName) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_fragment_container, fragment, fragmentName)
                .addToBackStack(fragmentName)
                .commit();
    }

    public void checkBluetoothAvailability() {
        if (BluetoothManager.getInstance().isBluetoothAvailable()) {
            BluetoothManager.getInstance().enable();
        } else {
            finish();
            Toast.makeText(this, "Bluetooth unavailable on your device", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermission(String[] permissions, AssentCallback callback) {
        int requestCode = new Random().nextInt(100);
        Assent.requestPermissions(callback, requestCode, permissions);
    }

    private void openTwoAxisFragment() {
        boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(KEY_TWO_AXIS, 0);
        if (!fragmentPopped) {
            replaceFragment(TwoAxisFragment.newInstance(), KEY_TWO_AXIS);
        }
    }

    private void openJoyStickFragment() {
        boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(JOY_STICK, 0);
        if (!fragmentPopped) {
            replaceFragment(new JoyStickFragment(), JOY_STICK);
        }
    }

    private void openButtonsFragment() {
        boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(BUTTONS, 0);
        if (!fragmentPopped) {
            replaceFragment(new ButtonsFragment(), BUTTONS);
        }
    }

    private void openToggleButtonsFragment() {
        boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(TOGGLE_BUTTONS, 0);
        if (!fragmentPopped) {
            replaceFragment(new ToggleButtonsFragment(), TOGGLE_BUTTONS);
        }
    }
}
