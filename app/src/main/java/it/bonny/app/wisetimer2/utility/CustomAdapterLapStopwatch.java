package it.bonny.app.wisetimer2.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.List;

import it.bonny.app.wisetimer2.bean.StopwatchBean;
import it.bonny.app.wisetimer2.R;

public class CustomAdapterLapStopwatch extends ArrayAdapter<StopwatchBean> {
    public CustomAdapterLapStopwatch(@NonNull Context context, int resource, List<StopwatchBean> objects) {
        super(context, resource, objects);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try{
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(inflater != null)
                convertView = inflater.inflate(R.layout.row_list_lap_stopwatch, parent, false);
            TextView lap = convertView.findViewById(R.id.lap);
            TextView timePassed = convertView.findViewById(R.id.timePassed);
            TextView time = convertView.findViewById(R.id.time);
            ImageView resultLapIcon = convertView.findViewById(R.id.resultLapIcon);
            StopwatchBean c = getItem(position);
            if(c != null){
                lap.setText(c.getLap());
                timePassed.setText(c.getTimePassed());
                time.setText(c.getTime());
                if(StopwatchBean.best.equals(c.getResultLap())){
                    resultLapIcon.setImageResource(R.drawable.run);
                }else if(StopwatchBean.worse.equals(c.getResultLap())){
                    resultLapIcon.setImageResource(R.drawable.walk);
                }
            }
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return convertView;
    }
}

