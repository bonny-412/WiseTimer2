package it.bonny.app.wisetimer2.manager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import it.bonny.app.wisetimer2.bean.SettingBean;
import it.bonny.app.wisetimer2.utility.Util;
import it.bonny.app.wisetimer2.R;

public class InformationActivity extends AppCompatActivity {
    private final Activity activity = this;
    static SettingBean settingBean;
    private TextView linkTermsService, linkPrivacyPolice;
    private final Util util = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            if(settingBean != null) {
                mFirebaseAnalytics.setAnalyticsCollectionEnabled(settingBean.isActiveAnalytics());
            }
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, savedInstanceState);
            settingBean = util.getSettingBeanSaved(activity);
            setTitle(R.string.action_information);
            setTheme(R.style.AppTheme);
            setContentView(R.layout.information_activity);
            initElement();
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            linkTermsService.setOnClickListener(v -> {
                final String url = "https://wisetimer.netlify.app/terms-conditions.html";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            });
            linkPrivacyPolice.setOnClickListener(v -> {
                final String url = "https://wisetimer.netlify.app/privacy-policy.html";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            });

        } catch (Exception e){
            util.errorReport(activity, e.toString(), settingBean, activity.getApplicationContext());
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        util.saveSettingBean(settingBean, activity);
        return super.onOptionsItemSelected(menuItem);
    }

    private void initElement(){
        linkTermsService = findViewById(R.id.linkTermsService);
        linkPrivacyPolice = findViewById(R.id.linkPrivacyPolice);
    }
}
