package com.pushkin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.pushkin.PushkinDatabaseHelper;
import com.pushkin.R;
import com.pushkin.fragment.ConversationsFragment;
import com.pushkin.fragment.KintactsFragment;
import com.pushkin.fragment.ProfileFragment;
import com.pushkin.fragment.SettingsFragment;
import com.pushkin.other.CircleTransform;

import java.io.File;

public class MainConversationView extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    // urls to load navigation header background image
    // and profile image
//    https://c1.staticflickr.com/2/1347/539904548_1c576220e7.jpg
//    http://api.androidhive.info/images/nav-menu-header-bg.jpg
    private static final String urlNavHeaderBg = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
    private static final String urlProfileImg = "http://cdnak1.psbin.com/img/mw=650/cr=n/d=j7phc/x3a82y2kqa3dtubu.jpg";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_CONVERSATIONS = "conversations";
    private static final String TAG_KINTACTS = "kintacts";
    private static final String TAG_PROFILE = "profile";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_CONVERSATIONS;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    //get instance of our lovely databasehelper
    public static PushkinDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println ("Here!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        dbHelper = new PushkinDatabaseHelper(this);

        //inflate menu
        navigationView.inflateMenu(R.menu.activity_main_drawer_);
        Menu menu = navigationView.getMenu();
        if (menu == null) {
            System.out.println ("menu is null");
        }
        System.out.println("Size of menu is " + menu.size());

        // Navigation view header
//        View header = navigationView.getHeaderView(0);
//        navHeader = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        navigationView.inflateHeaderView(R.layout.nav_header_main);

        View hView = navigationView.getHeaderView(0);
        txtName = (TextView) hView.findViewById(R.id.name);
        txtWebsite = (TextView) hView.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) hView.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) hView.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        //fab.setImageResource(R.drawable.ic_add_black_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //startActivity(new Intent(MainConversationView.this, Chat.class));
            }
        });

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_CONVERSATIONS;
            loadHomeFragment();
        }
    }

    /***
     * Load navigation menu header information
     * load actual menu items into the navigationView
     * like background image, profile image
     * name, website, notifications action view (dot)
     */

    private void loadNavHeader() {
        // showing dot next to notifications label

        navigationView.getMenu().getItem(0).setActionView(R.layout.menu_dot);
//        TextView txtName1 = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name);

        // name, website
//        txtName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name);
        txtName.setText("John Smith");
        txtWebsite.setText("jsmith@yahoo.com");

//         loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);
//
        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);


    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
//        Runnable mPendingRunnable = new Runnable() {
//            @Override
//            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
//                LinearLayout frameLayout = (LinearLayout) findViewById(R.id.frame);
//                TextView tb = new TextView(getApplicationContext());
//                tb.setText("hey");
//                frameLayout.addView(tb);
//                fragmentTransaction.add(R.id.drawer_layout, fragment).commit();
                FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame);
                System.out.println (frameLayout);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);

//                fragmentTransaction.commitAllowingStateLoss();
                fragmentTransaction.commit();
//                setTitle(CURRENT_TAG);
//            }
//        };

//        // If mPendingRunnable is not null, then add to the message queue
//        if (mPendingRunnable != null) {
//            mHandler.post(mPendingRunnable);
//        }

//        // show or hide the fab button
        toggleFab();
//
//        //Closing drawer on item click
        drawer.closeDrawers();
//
//        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // conversations
                ConversationsFragment conversationsFragment = new ConversationsFragment();
                return conversationsFragment;
            case 1:
                // kintacts
                KintactsFragment kintactsFragment = new KintactsFragment();
                return kintactsFragment;
            case 2:
                // profile
                ProfileFragment profileFragment = new ProfileFragment();
                return profileFragment;
            case 3:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;

            default:
                return new ConversationsFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_conversations:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_CONVERSATIONS;
                        break;
                    case R.id.nav_kintacts:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_KINTACTS;
                        break;
                    case R.id.nav_profile:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_PROFILE;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_CONVERSATIONS;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logging out", Toast.LENGTH_LONG).show();
            File dir = getFilesDir();
            File file = new File(dir, "userToken.dat");
            boolean deleted = file.delete();
            System.out.println("DID WE DELETE IT? ");
            System.out.println(deleted);
            startActivity(new Intent(MainConversationView.this, Login.class));
            return deleted;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'

        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }
}
