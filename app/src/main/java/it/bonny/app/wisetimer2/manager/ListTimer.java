package it.bonny.app.wisetimer2.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.List;

import it.bonny.app.wisetimer2.bean.SettingBean;
import it.bonny.app.wisetimer2.bean.TimerBean;
import it.bonny.app.wisetimer2.db.ManagerDB;
import it.bonny.app.wisetimer2.utility.ListTimerAdapter;
import it.bonny.app.wisetimer2.utility.Util;
import it.bonny.app.wisetimer2.R;

public class ListTimer extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    private SettingBean settingBean;
    private final Activity activity = this;
    private final Context context = this;
    private final Util util = new Util();
    private ListView listTimer;
    private final ManagerDB managerDB = new ManagerDB(context);
    private List<TimerBean> timerBeans = new ArrayList<>();
    private TimerBean timerBeanChecked;
    private ImageView infoListTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            settingBean = (SettingBean) getIntent().getSerializableExtra("settingBean");
            if(settingBean == null)
                settingBean = util.getSettingBeanSaved(this);
            setTitle(getString(R.string.listTimerName));
            setTheme("dark".equalsIgnoreCase(settingBean.getModeStyles()) ? R.style.settingsPageDark : R.style.settingsPageLight);
            util.setAppLocale(settingBean.getLanguages(), context);
            setContentView(R.layout.activity_list_timer);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                getSupportActionBar().setDisplayShowCustomEnabled(true);
                getSupportActionBar().setCustomView(R.layout.custom_action_bar_list_timer);
                View view = getSupportActionBar().getCustomView();
                infoListTimer = view.findViewById(R.id.infoListTimer);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            listTimer = findViewById(R.id.listTimer);
            managerDB.openReadDB();
            timerBeans = util.getTimerBeansFromCursor(managerDB.findTimerByChecked(1));
            if(timerBeans != null && timerBeans.size() > 0)
                timerBeanChecked = timerBeans.get(0);
            timerBeans = util.getTimerBeansFromCursor(managerDB.getAllTimerBean());
            managerDB.close();
            if(timerBeans != null && timerBeans.size() > 0){
                ListTimerAdapter adapter = new ListTimerAdapter(timerBeans, settingBean, activity);
                listTimer.setAdapter(adapter);
            }else {
                TextView textView = findViewById(R.id.listEmpty);
                if(textView != null)
                    textView.setVisibility(View.VISIBLE);
            }

            infoListTimer.setOnClickListener(v -> createDialogInfo());
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
            util.errorReport(activity, e.toString(), settingBean, context);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        boolean beanIsEqual = false;
        boolean timerChecked = false;
        boolean foundSelectedTimer = false;
        if(listTimer != null && listTimer.getChildCount() > 0){
            for(int i = 0; i < listTimer.getChildCount(); i ++){
                View view = listTimer.getChildAt(i);
                if(view != null){
                    CheckBox checkBox = view.findViewById(R.id.selectedTimer);
                    if(checkBox != null){
                        TimerBean bean = timerBeans.get(i);
                        if(checkBox.isChecked()){
                            bean.setChecked(true);
                            foundSelectedTimer = true;
                            util.saveTimerBean(bean, activity);
                            beanIsEqual = bean.isEqual(timerBeanChecked);
                            timerChecked = true;
                        }else{
                            bean.setChecked(false);
                        }
                        saveTimerBeanOnDB(bean);
                    }
                }
            }
            if(!timerChecked && timerBeanChecked == null)
                beanIsEqual = true;
        }else{
            beanIsEqual = true;
        }
        if(foundSelectedTimer){
            util.deleteTimerBean(activity);
        }
        if(beanIsEqual)
            activity.recreate();
        return super.onOptionsItemSelected(menuItem);
    }

    private void saveTimerBeanOnDB(TimerBean bean){
        try{
            managerDB.openWriteDB();
            managerDB.update(bean);
            managerDB.close();
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int position = listTimer.getPositionForView(compoundButton);
        if(position != ListView.INVALID_POSITION){
            TimerBean timerBean = timerBeans.get(position);
            timerBean.setChecked(b);
        }
    }

    private void createDialogInfo(){
        AlertDialog.Builder builder;
        if(settingBean.isDark()){
            builder = new AlertDialog.Builder(context, R.style.AlertDialogStyleDark);
        }else {
            builder = new AlertDialog.Builder(context, R.style.AlertDialogStyleLight);
        }
        View viewInfoDialog = View.inflate(context, R.layout.alert_info, null);
        builder.setCancelable(false);
        builder.setView(viewInfoDialog);
        Button buttonCancel = viewInfoDialog.findViewById(R.id.buttonCancel);
        final AlertDialog dialog = builder.create();
        if(dialog != null){
            if(dialog.getWindow() != null){
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.transparent)));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            }
        }
        buttonCancel.setOnClickListener(v -> {
            if(dialog != null)
                dialog.dismiss();
        });
        if(dialog != null)
            dialog.show();
    }
}

