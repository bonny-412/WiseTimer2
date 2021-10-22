package it.bonny.app.wisetimer2.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.List;

import it.bonny.app.wisetimer2.bean.SettingBean;
import it.bonny.app.wisetimer2.bean.TimerBean;
import it.bonny.app.wisetimer2.db.ManagerDB;
import it.bonny.app.wisetimer2.wisetoast.WiseToast;
import it.bonny.app.wisetimer2.R;

public class ListTimerAdapter extends ArrayAdapter<TimerBean> {
    private final List<TimerBean> timerBeans;
    private final SettingBean settingBean;
    private int selectedPosition = -1;
    private final Util util;

    public ListTimerAdapter(List<TimerBean> timerBeans, SettingBean settingBean, Activity activity) {
        super(activity, R.layout.row_list_timer, timerBeans);
        this.timerBeans = timerBeans;
        this.settingBean = settingBean;
        this.util = new Util();
        findSelectedTimer();
    }

    @Override
    @NonNull
    public View getView(int position, @Nullable View rowView, @Nullable ViewGroup parent) {
        try {
            ViewHolder holder;
            if(rowView == null){
                rowView = View.inflate(getContext(), R.layout.row_list_timer, null);
                holder = new ViewHolder(rowView);
                rowView.setTag(holder);
            }else {
                holder = (ViewHolder) rowView.getTag();
            }
            holder.title.setText(timerBeans.get(position).getName());

            if(position == selectedPosition){
                holder.checkBox.setChecked(true);
                timerBeans.get(position).setChecked(true);
            }else {
                holder.checkBox.setChecked(false);
                timerBeans.get(position).setChecked(false);
            }
            holder.checkBox.setOnClickListener(onStateChangedListener(holder.checkBox, position));
            holder.infoTimerBtn.setOnClickListener(view -> util.createDialogInfoTimer(timerBeans.get(position), settingBean, timerBeans.get(position).getName(), getContext()));
            holder.deleteTimerBtn.setOnClickListener(view -> getAlertDialogDeleteListTimer(position, timerBeans.get(position).getId(), settingBean));
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return rowView != null ? rowView : View.inflate(getContext(), R.layout.row_list_timer, null);
    }

    private View.OnClickListener onStateChangedListener(final CheckBox checkBox, final int position) {
        return v -> {
            if (checkBox.isChecked()) {
                selectedPosition = position;
            } else {
                selectedPosition = -1;
            }
            notifyDataSetChanged();
        };
    }


    private static class ViewHolder {
        private final TextView title;
        private final CheckBox checkBox;
        private final ImageView infoTimerBtn;
        private final ImageView deleteTimerBtn;

        ViewHolder(View v) {
            checkBox = v.findViewById(R.id.selectedTimer);
            title = v.findViewById(R.id.title);
            infoTimerBtn = v.findViewById(R.id.infoTimerBtn);
            deleteTimerBtn = v.findViewById(R.id.deleteTimerBtn);
        }
    }

    private void findSelectedTimer(){
        for(int i = 0; i < timerBeans.size(); i++){
            TimerBean timerBean = timerBeans.get(i);
            if(timerBean.isChecked()){
                selectedPosition = i;
            }
        }
    }

    private void getAlertDialogDeleteListTimer(final int position, final long id, SettingBean settingBean){
        final ManagerDB managerDB = new ManagerDB(getContext());
        AlertDialog.Builder builder;
        if(settingBean.isDark()){
            builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyleDark);
        }else {
            builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyleLight);
        }
        View viewInfoDialog = View.inflate(getContext(), R.layout.alert_delete_timer, null);
        builder.setCancelable(false);
        builder.setView(viewInfoDialog);
        Button buttonCancel = viewInfoDialog.findViewById(R.id.buttonCancel);
        Button buttonSend = viewInfoDialog.findViewById(R.id.buttonSend);
        TextView textView = viewInfoDialog.findViewById(R.id.textView);
        String title = getContext().getString(R.string.alert_delete_list_timer_title) + " " + timerBeans.get(position).getName();
        textView.setText(title);
        final AlertDialog dialog = builder.create();
        if(dialog != null){
            if(dialog.getWindow() != null){
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getColor(R.color.transparent)));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            }
        }
        buttonCancel.setOnClickListener(v -> {
            if(dialog != null)
                dialog.dismiss();
        });
        buttonSend.setOnClickListener(v -> {
            managerDB.openWriteDB();
            boolean resultDelete = managerDB.deleteById(id);
            managerDB.close();
            if(resultDelete){
                WiseToast.success(getContext(), getContext().getString(R.string.alert_delete_list_timer_message_yes), Toast.LENGTH_SHORT).show();
                timerBeans.remove(position);
                notifyDataSetChanged();
                if(dialog != null)
                    dialog.dismiss();
            }else {
                WiseToast.error(getContext(), getContext().getString(R.string.alert_delete_list_timer_message_error), Toast.LENGTH_SHORT).show();
            }
        });
        if(dialog != null)
            dialog.show();
    }

}
