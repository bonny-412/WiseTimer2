package it.bonny.app.wisetimer2.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Locale;

import it.bonny.app.wisetimer2.bean.SettingBean;
import it.bonny.app.wisetimer2.utility.Util;
import it.bonny.app.wisetimer2.wisetoast.WiseToast;
import it.bonny.app.wisetimer2.R;

public class SettingsActivity extends AppCompatActivity {
    private final Activity activity = this;
    static SettingBean settingBean;
    private long backPressedTime;
    private final Util util = new Util();
    private final WiseToast wiseToast = new WiseToast();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            settingBean = util.getSettingBeanSaved(activity);
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            if(settingBean != null)
                mFirebaseAnalytics.setAnalyticsCollectionEnabled(settingBean.isActiveAnalytics());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, savedInstanceState);
            setTitle(R.string.action_settings);
            setTheme(R.style.AppTheme);
            setContentView(R.layout.settings_activity);
            SettingsFragment settingsFragment = new SettingsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("settingBean", settingBean);
            settingsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.settings, settingsFragment).commit();
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                util.colorActionBarTitle(this, this, actionBar);
            }
        } catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
            util.errorReport(activity, e.toString(), settingBean, activity.getApplicationContext());
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        util.saveSettingBean(settingBean, activity);
        return super.onOptionsItemSelected(menuItem);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private SwitchPreference activeCrash, activeAnalytics;
        private CheckBoxPreference sound, vibration, displayOn, sideButtons, voice;
        private ListPreference languages;
        private Preference delete, messageMe, commentApp, otherApp;
        private final Util util = new Util();
        private int backNext = 0;
        private final WiseToast wiseToast = new WiseToast();

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            try {
                if(getContext() != null)
                    util.setAppLocale(settingBean.getLanguages(), getContext());
                setPreferencesFromResource(R.xml.settings_preferences, rootKey);
                initElement();
                if(settingBean.isDisplayOn()){
                    if(getActivity() != null)
                        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
                if(delete != null){
                    delete.setOnPreferenceClickListener(preference -> {
                        if(getActivity() != null)
                            util.getAlertDialogDeleteSettings(settingBean, getActivity(), getContext());
                        return false;
                    });
                }
                commentApp.setOnPreferenceClickListener(preference -> {
                    try {
                        if(getActivity() != null)
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=" + getActivity().getPackageName())));
                    } catch (ActivityNotFoundException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        if(getActivity() != null)
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                    }
                    return false;
                });

                otherApp.setOnPreferenceClickListener(preference -> {
                    final String url = "https://play.google.com/store/apps/dev?id=6114894737573241391";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    return false;
                });

                messageMe.setOnPreferenceClickListener(preference -> {
                    createDialogSendEmail();
                    return false;
                });
                voice.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean val = (boolean) newValue;
                    voice.setChecked(val);
                    settingBean.setVoice(val);
                    if(getActivity() != null)
                        util.saveSettingBean(settingBean, getActivity());
                    return false;
                });
                sound.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean val = (boolean) newValue;
                    sound.setChecked(val);
                    settingBean.setSound(val);
                    if(getActivity() != null)
                        util.saveSettingBean(settingBean, getActivity());
                    return false;
                });
                vibration.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean val = (boolean) newValue;
                    settingBean.setVibration(val);
                    vibration.setChecked(val);
                    if(getActivity() != null)
                        util.saveSettingBean(settingBean, getActivity());
                    return false;
                });
                activeCrash.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean val = (boolean) newValue;
                    settingBean.setActiveCrash(val);
                    activeCrash.setChecked(val);
                    if(getActivity() != null)
                        util.saveSettingBean(settingBean, getActivity());
                    return false;
                });
                activeAnalytics.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean val = (boolean) newValue;
                    settingBean.setActiveAnalytics(val);
                    activeAnalytics.setChecked(val);
                    if(getActivity() != null)
                        util.saveSettingBean(settingBean, getActivity());
                    return false;
                });
                displayOn.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean val = (boolean) newValue;
                    settingBean.setDisplayOn(val);
                    displayOn.setChecked(val);
                    if(getActivity() != null)
                        util.saveSettingBean(settingBean, getActivity());
                    return false;
                });
                sideButtons.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean val = (boolean) newValue;
                    if(val){
                        if(!settingBean.isShowAlertSadeButtons())
                            createDialogInfoSadeButtons();
                    }
                    settingBean.setSideButtons(val);
                    sideButtons.setChecked(val);
                    if(getActivity() != null)
                        util.saveSettingBean(settingBean, getActivity());
                    return false;
                });

                languages.setOnPreferenceClickListener(preference -> false);
                languages.setOnPreferenceChangeListener((preference, newValue) -> {
                    String val = (String) newValue;
                    settingBean.setLanguages(val);
                    languages.setValue(val);
                    if(getActivity() != null){
                        util.saveSettingBean(settingBean, getActivity());
                        getActivity().recreate();
                    }
                    return false;
                });
            }catch (Exception e){
                util.errorReport(getActivity(), e.toString(), settingBean, getContext());
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        }

        private void initElement(){
            voice = getPreferenceManager().findPreference(getString(R.string.id_voice));
            sound = getPreferenceManager().findPreference(getString(R.string.id_sound));
            vibration = getPreferenceManager().findPreference(getString(R.string.id_vibration));
            languages = getPreferenceManager().findPreference(getString(R.string.id_languages));
            delete = getPreferenceManager().findPreference(getString(R.string.id_delete));
            messageMe = getPreferenceManager().findPreference(getString(R.string.id_message_me));
            commentApp = getPreferenceManager().findPreference(getString(R.string.id_comment_app));
            otherApp = getPreferenceManager().findPreference(getString(R.string.id_other_app));
            displayOn = getPreferenceManager().findPreference(getString(R.string.id_display_on));
            sideButtons = getPreferenceManager().findPreference(getString(R.string.id_side_buttons));
            activeCrash = getPreferenceManager().findPreference(getString(R.string.id_active_crash));
            activeAnalytics = getPreferenceManager().findPreference(getString(R.string.id_active_analytics));
            voice.setChecked(settingBean.isVoice());
            sound.setChecked(settingBean.isSound());
            vibration.setChecked(settingBean.isVibration());
            languages.setValue(settingBean.getLanguages());
            displayOn.setChecked(settingBean.isDisplayOn());
            sideButtons.setChecked(settingBean.isSideButtons());
            activeCrash.setChecked(settingBean.isActiveCrash());
            activeAnalytics.setChecked(settingBean.isActiveAnalytics());
        }

        private void createDialogInfoSadeButtons(){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyleDark);
            backNext = 0;
            View viewInfoDialog = View.inflate(getContext(), R.layout.alert_info_sade_buttons, null);
            builder.setCancelable(false);
            builder.setView(viewInfoDialog);
            Button buttonCancel = viewInfoDialog.findViewById(R.id.buttonCancel);
            CheckBox showAlertSadeButtons = viewInfoDialog.findViewById(R.id.showAlertSadeButtons);
            final Button buttonNext = viewInfoDialog.findViewById(R.id.buttonNext);
            final TextView id_info_alert_sade_buttons = viewInfoDialog.findViewById(R.id.id_info_alert_sade_buttons);
            final RelativeLayout labelCheckbox = viewInfoDialog.findViewById(R.id.labelCheckbox);
            final AlertDialog dialog = builder.create();
            if(dialog != null){
                if(dialog.getWindow() != null){
                    if(getContext() != null)
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getColor(R.color.transparent)));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                }
            }
            showAlertSadeButtons.setChecked(settingBean.isShowAlertSadeButtons());
            buttonNext.setOnClickListener(v -> {
                if(backNext == 0){
                    id_info_alert_sade_buttons.setText(getString(R.string.info_alert_sade_buttons_stopwatch));
                    labelCheckbox.setVisibility(View.VISIBLE);
                    buttonNext.setText(R.string.back);
                    backNext = 1;
                }else if(backNext == 1){
                    id_info_alert_sade_buttons.setText(getString(R.string.info_alert_sade_buttons_timer));
                    labelCheckbox.setVisibility(View.GONE);
                    buttonNext.setText(R.string.button_next_info_page_edit);
                    backNext = 0;
                }
            });
            showAlertSadeButtons.setOnCheckedChangeListener((buttonView, isChecked) -> settingBean.setShowAlertSadeButtons(isChecked));
            buttonCancel.setOnClickListener(v -> {
                if(dialog != null)
                    dialog.dismiss();
            });
            if(dialog != null)
                dialog.show();
        }

        private void createDialogSendEmail(){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyleDark);
            View viewInfoDialog = View.inflate(getContext(), R.layout.alert_send_email, null);
            builder.setCancelable(false);
            builder.setView(viewInfoDialog);
            Button buttonCancel = viewInfoDialog.findViewById(R.id.buttonCancel);
            Button buttonSendEmail = viewInfoDialog.findViewById(R.id.buttonSendEmail);
            final EditText emailText = viewInfoDialog.findViewById(R.id.text);
            final AlertDialog dialog = builder.create();
            if(dialog != null){
                if(dialog.getWindow() != null){
                    if(getContext() != null)
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getColor(R.color.transparent)));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                }
            }
            buttonCancel.setOnClickListener(v -> {
                if(dialog != null)
                    dialog.dismiss();
            });
            buttonSendEmail.setOnClickListener(v -> {
                if(emailText.getText() != null && !"".equals(emailText.getText().toString().trim())){
                    if(getActivity() != null && getContext() != null)
                        util.sendEmail(emailText.getText().toString().trim(), "WiseTimer Info", getActivity(), getContext());
                }else{
                    if(getContext() != null)
                        wiseToast.warning(getContext(), getString(R.string.email_text_required), Toast.LENGTH_SHORT).show();
                }
            });
            if(dialog != null)
                dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            util.exitAppByOnBackPressed(this);
        } else {
            if(getApplicationContext() != null)
                wiseToast.warning(getApplicationContext(), getString(R.string.pressToExit), Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}