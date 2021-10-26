package it.bonny.app.wisetimer2.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;

import it.bonny.app.wisetimer2.bean.SettingBean;
import it.bonny.app.wisetimer2.manager.ui.stopwatch.StopwatchFragment;
import it.bonny.app.wisetimer2.utility.CubeOutDepthTransformation;
import it.bonny.app.wisetimer2.utility.Util;
import it.bonny.app.wisetimer2.wisetoast.WiseToast;
import it.bonny.app.wisetimer2.R;

public class MainActivity extends AppCompatActivity {
    private final Activity activity = this;
    private final Context context = this;
    private final Util util = new Util();
    private SettingBean settingBean;
    private long backPressedTime;
    private int idFragmentNow = -1;
    private ViewPager2 viewPager;
    private MenuItem prevMenuItem;
    public static boolean isRunningStopwatch = false;
    private final int STOPWATCH = 1;
    private final int TIMER = 0;
    private BottomNavigationView navView;
    private final WiseToast wiseToast = new WiseToast();

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            settingBean = util.getSettingBeanSaved(activity);
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            if(settingBean != null) {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(settingBean.isActiveCrash());
                mFirebaseAnalytics.setAnalyticsCollectionEnabled(settingBean.isActiveAnalytics());
                FirebaseMessaging.getInstance().setAutoInitEnabled(settingBean.isActiveAnalytics());
            }
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, savedInstanceState);
            setTheme(R.style.AppTheme_NoActionBar);
            util.setAppLocale(settingBean.getLanguages(), context);
            setContentView(R.layout.activity_main);
            final Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if(getSupportActionBar() != null)
                getSupportActionBar().setDisplayShowTitleEnabled(false);

            if(settingBean.isDisplayOn())
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            navView = findViewById(R.id.nav_view);

            viewPager = findViewById(R.id.frame_layout);
            viewPager.setPageTransformer(new CubeOutDepthTransformation());
            FragmentManager fragmentManager = getSupportFragmentManager();
            ViewPagerAdapter adapter = new ViewPagerAdapter(fragmentManager, getLifecycle());
            viewPager.setAdapter(adapter);
            viewPager.registerOnPageChangeCallback(pageChangeCallback);
            viewPager.setUserInputEnabled(false);

            navView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if(isRunningStopwatch){
                    idFragmentNow = R.id.navigation_stopwatch;
                    viewPager.setCurrentItem(STOPWATCH);
                    wiseToast.warning(context, getString(R.string.stopwatch_running), Toast.LENGTH_SHORT).show();
                }else {
                    if(itemId == R.id.navigation_timer) {
                        if(idFragmentNow != R.id.navigation_timer){
                            idFragmentNow = R.id.navigation_timer;
                            viewPager.setCurrentItem(TIMER);
                        }
                    }else if(itemId == R.id.navigation_stopwatch) {
                        if(idFragmentNow != R.id.navigation_stopwatch){
                            idFragmentNow = R.id.navigation_stopwatch;
                            viewPager.setCurrentItem(STOPWATCH);
                        }
                    }
                }
                return false;
            });
        } catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
            util.errorReport(activity, e.toString(), settingBean, activity.getApplicationContext());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("settingBean", settingBean);
            intent.putExtra("idFragmentNow", idFragmentNow);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_information){
            Intent intent = new Intent(MainActivity.this, InformationActivity.class);
            intent.putExtra("settingBean", settingBean);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * ViewPager page change listener
     */
    ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            if(isRunningStopwatch){
                idFragmentNow = R.id.navigation_stopwatch;
                viewPager.setCurrentItem(STOPWATCH);
                wiseToast.warning(context, getString(R.string.stopwatch_running), Toast.LENGTH_SHORT).show();
            }else {
                super.onPageSelected(position);
                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    navView.getMenu().getItem(STOPWATCH).setChecked(false);
                navView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navView.getMenu().getItem(position);
                idFragmentNow = position;
            }
        }
    };


    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            util.exitAppByOnBackPressed(this);
        } else {
            if(context != null)
                wiseToast.warning(context, getString(R.string.pressToExit), Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if(settingBean.isSideButtons()){
                Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.frame_layout + ":" + viewPager.getCurrentItem());
                if (viewPager.getCurrentItem() == STOPWATCH && page != null) {
                    ((StopwatchFragment)page).myOnKeyDown(keyCode);
                    return true;
                }
            }
            return super.onKeyDown(keyCode, event);
        } catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
            util.errorReport(activity, e.toString(), settingBean, activity.getApplicationContext());
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.frame_layout + ":" + viewPager.getCurrentItem());
        if (viewPager.getCurrentItem() == STOPWATCH && page != null) {
            ((StopwatchFragment)page).getNotificationManagerCompat().cancel(0);
        }
    }

}