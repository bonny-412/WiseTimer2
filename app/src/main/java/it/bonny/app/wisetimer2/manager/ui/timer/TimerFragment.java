package it.bonny.app.wisetimer2.manager.ui.timer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.HashMap;
import java.util.List;

import it.bonny.app.wisetimer2.bean.SettingBean;
import it.bonny.app.wisetimer2.bean.StateType;
import it.bonny.app.wisetimer2.bean.TimerBean;
import it.bonny.app.wisetimer2.db.ManagerDB;
import it.bonny.app.wisetimer2.manager.ListTimer;
import it.bonny.app.wisetimer2.manager.TimerActivity;
import it.bonny.app.wisetimer2.utility.ContinuousLongClickListener;
import it.bonny.app.wisetimer2.utility.Util;
import it.bonny.app.wisetimer2.wisetoast.WiseToast;
import it.bonny.app.wisetimer2.R;

public class TimerFragment extends Fragment {
    private EditText editTextSeries, editTextRestMin, editTextRestSec, editTextWorkMin, editTextWorkSec,
            editTextRounds, editTextRestRoundsSec, editTextRestRoundsMin;
    private Button btnAddSeries, btnAddRest, btnAddWork, btnRemoveSeries, btnRemoveRest, btnRemoveWork, infoPreWorkButton,
            btnAddRounds, btnAddRestRounds, btnRemoveRounds, btnRemoveRestRounds;
    private ImageView infoTimerBtn;
    private final Util util = new Util();
    private ManagerDB managerDB = null;
    private TimerBean timerBeanSaved = null, timerBeanCurrent = new TimerBean();
    private SettingBean settingBean;
    private FloatingActionButton fabAddList, fabViewList, fabViewSave;
    private Animation fab_open, fab_close, fab_rotate_antiClock, fab_rotate_clock;
    private TextView textViewTimerUsedNameCustom, textViewPreWorkInfo, restRounds;
    private LinearLayout layButtonRestRounds;
    private boolean isOpen = false;
    private View root;
    private CheckBox infoPreWorkCheckbox;
    private String timerName = "";
    private List<TimerBean> timerBeanList = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            root = inflater.inflate(R.layout.fragment_timer, container, false);
            if(getActivity() != null)
                settingBean = util.getSettingBeanSaved(getActivity());
            if(settingBean != null){
                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(root.getContext());
                if(settingBean != null) {
                    mFirebaseAnalytics.setAnalyticsCollectionEnabled(settingBean.isActiveAnalytics());
                }
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, savedInstanceState);
            }
            managerDB = new ManagerDB(getActivity());
            if(getContext() != null)
                util.setAppLocale(settingBean.getLanguages(), getContext());
            managerDB.openReadDB();
            timerBeanList = util.getTimerBeansFromCursor(managerDB.findTimerByChecked(1));
            if(timerBeanList != null && timerBeanList.size() > 0)
                timerBeanSaved = timerBeanList.get(0);
            managerDB.close();
            if(timerBeanSaved == null){
                timerBeanSaved = util.getTimerBeanSaved(getActivity());
                timerBeanSaved.setName(getString(R.string.timerUsedNameCustomized));
            }
            timerBeanCurrent.copy(timerBeanSaved);
            FloatingActionButton fabStart = root.findViewById(R.id.fabStart);
            initElement();
            showWelcomeAlert();
            //************INIZIO Gestisco il cursore nei vari campi di testo****************
            editTextSeries.setOnClickListener(v -> {
                editTextSeries.setCursorVisible(true);
                if(isOpen)
                    closeFloatList();
            });
            editTextSeries.setOnEditorActionListener((v, actionId, event) -> {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    if(util.isZeroEmpty(editTextSeries.getText().toString()))
                        editTextSeries.setText(R.string.uno);
                    editTextSeries.setCursorVisible(false);
                    timerBeanCurrent.setNumSeries(editTextSeries.getText().toString());
                    if(isOpen)
                        closeFloatList();
                    setTimerUsedName();
                }
                return false;
            });
            editTextRounds.setOnClickListener(v -> {
                editTextRounds.setCursorVisible(true);
                roundsRestVisible();
                if(isOpen)
                    closeFloatList();
            });
            editTextRounds.setOnEditorActionListener((v, actionId, event) -> {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    if(util.isZeroEmpty(editTextRounds.getText().toString()))
                        editTextRounds.setText(R.string.uno);
                    editTextRounds.setCursorVisible(false);
                    timerBeanCurrent.setNumRounds(editTextRounds.getText().toString());
                    roundsRestVisible();
                    if(isOpen)
                        closeFloatList();
                    setTimerUsedName();
                }
                return false;
            });
            editTextRestMin.setOnClickListener(v -> {
                editTextRestMin.setCursorVisible(true);
                if(isOpen)
                    closeFloatList();
            });
            editTextRestMin.setOnEditorActionListener((v, actionId, event) -> {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    if(util.isZeroEmpty(editTextRestMin.getText().toString()))
                        editTextRestMin.setText(R.string.zero);
                    editTextRestMin.setCursorVisible(false);
                    timerBeanCurrent.setRestMin(editTextRestMin.getText().toString());
                    if(isOpen)
                        closeFloatList();
                    setTimerUsedName();
                }
                return false;
            });
            editTextRestSec.setOnClickListener(v -> {
                editTextRestSec.setCursorVisible(true);
                if(isOpen)
                    closeFloatList();
            });
            editTextRestSec.setOnEditorActionListener((v, actionId, event) -> {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    if(util.isZeroEmpty(editTextRestSec.getText().toString()) && util.isZeroEmpty(editTextRestMin.getText().toString()))
                        editTextRestSec.setText(R.string.zero);
                    editTextRestSec.setCursorVisible(false);
                    timerBeanCurrent.setRestSec(editTextRestSec.getText().toString());
                    if(isOpen)
                        closeFloatList();
                    setTimerUsedName();
                }
                return false;
            });
            editTextRestRoundsMin.setOnClickListener(v -> {
                editTextRestRoundsMin.setCursorVisible(true);
                if(isOpen)
                    closeFloatList();
            });
            editTextRestRoundsMin.setOnEditorActionListener((v, actionId, event) -> {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    if(util.isZeroEmpty(editTextRestRoundsMin.getText().toString()))
                        editTextRestRoundsMin.setText(R.string.zero);
                    editTextRestRoundsMin.setCursorVisible(false);
                    timerBeanCurrent.setRestRoundsMin(editTextRestRoundsMin.getText().toString());
                    if(isOpen)
                        closeFloatList();
                    setTimerUsedName();
                }
                return false;
            });
            editTextRestRoundsSec.setOnClickListener(v -> {
                editTextRestRoundsSec.setCursorVisible(true);
                if(isOpen)
                    closeFloatList();
            });
            editTextRestRoundsSec.setOnEditorActionListener((v, actionId, event) -> {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    if(util.isZeroEmpty(editTextRestSec.getText().toString()) && util.isZeroEmpty(editTextRestRoundsMin.getText().toString()))
                        editTextRestRoundsSec.setText(R.string.zero);
                    editTextRestRoundsSec.setCursorVisible(false);
                    timerBeanCurrent.setRestRoundsSec(editTextRestRoundsSec.getText().toString());
                    if(isOpen)
                        closeFloatList();
                    setTimerUsedName();
                }
                return false;
            });
            editTextWorkMin.setOnClickListener(v -> {
                editTextWorkMin.setCursorVisible(true);
                if(isOpen)
                    closeFloatList();
            });
            editTextWorkMin.setOnEditorActionListener((v, actionId, event) -> {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    if(util.isZeroEmpty(editTextWorkMin.getText().toString()))
                        editTextWorkMin.setText(R.string.zero);
                    editTextWorkMin.setCursorVisible(false);
                    timerBeanCurrent.setWorkMin(editTextWorkMin.getText().toString());
                    if(isOpen)
                        closeFloatList();
                    setTimerUsedName();
                }
                return false;
            });
            editTextWorkSec.setOnClickListener(v -> {
                editTextWorkSec.setCursorVisible(true);
                if(isOpen)
                    closeFloatList();
            });
            editTextWorkSec.setOnEditorActionListener((v, actionId, event) -> {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    if(util.isZeroEmpty(editTextWorkSec.getText().toString()) && util.isZeroEmpty(editTextWorkMin.getText().toString()))
                        editTextWorkSec.setText(R.string.zero);
                    editTextWorkSec.setCursorVisible(false);
                    timerBeanCurrent.setWorkSec(editTextWorkSec.getText().toString());
                    if(isOpen)
                        closeFloatList();
                    setTimerUsedName();
                }
                return false;
            });
            //************FINE Gestisco il cursore nei vari campi di testo****************
            //Aggiungo i listener ai bottoni
            btnAddSeries.setOnClickListener(v -> {
                String value = editTextSeries.getText().toString();
                editTextSeries.setText(util.addSeriesRounds(value));
                timerBeanCurrent.setNumSeries(util.addSeriesRounds(value));
                if(isOpen)
                    closeFloatList();
                setTimerUsedName();
            });
            btnAddRounds.setOnClickListener(v -> {
                String value = editTextRounds.getText().toString();
                editTextRounds.setText(util.addSeriesRounds(value));
                timerBeanCurrent.setNumRounds(util.addSeriesRounds(value));
                roundsRestVisible();
                if(isOpen)
                    closeFloatList();
                setTimerUsedName();
            });
            btnAddRest.setOnClickListener(v -> {
                String valueSec = editTextRestSec.getText().toString();
                String valueMin = editTextRestMin.getText().toString();
                HashMap<String, String> hashMap = util.addSeconds(valueSec, valueMin);
                editTextRestSec.setText(hashMap.get("sec"));
                editTextRestMin.setText(hashMap.get("min"));
                timerBeanCurrent.setRestSec(hashMap.get("sec"));
                timerBeanCurrent.setRestMin(hashMap.get("min"));
                if(isOpen)
                    closeFloatList();
                setTimerUsedName();
            });
            btnAddRestRounds.setOnClickListener(v -> {
                String valueSec = editTextRestRoundsSec.getText().toString();
                String valueMin = editTextRestRoundsMin.getText().toString();
                HashMap<String, String> hashMap = util.addSeconds(valueSec, valueMin);
                editTextRestRoundsSec.setText(hashMap.get("sec"));
                editTextRestRoundsMin.setText(hashMap.get("min"));
                timerBeanCurrent.setRestRoundsSec(hashMap.get("sec"));
                timerBeanCurrent.setRestRoundsMin(hashMap.get("min"));
                if(isOpen)
                    closeFloatList();
                setTimerUsedName();
            });
            btnAddWork.setOnClickListener(v -> {
                String valueSec = editTextWorkSec.getText().toString();
                String valueMin = editTextWorkMin.getText().toString();
                HashMap<String, String> hashMap = util.addSeconds(valueSec, valueMin);
                editTextWorkSec.setText(hashMap.get("sec"));
                editTextWorkMin.setText(hashMap.get("min"));
                timerBeanCurrent.setWorkSec(hashMap.get("sec"));
                timerBeanCurrent.setWorkMin(hashMap.get("min"));
                if(isOpen)
                    closeFloatList();
                setTimerUsedName();
            });
            btnRemoveSeries.setOnClickListener(v -> {
                String value = editTextSeries.getText().toString();
                editTextSeries.setText(util.removeSeriesRounds(value));
                timerBeanCurrent.setNumSeries(util.removeSeriesRounds(value));
                if(isOpen)
                    closeFloatList();
                setTimerUsedName();
            });
            btnRemoveRounds.setOnClickListener(v -> {
                String value = editTextRounds.getText().toString();
                editTextRounds.setText(util.removeSeriesRounds(value));
                timerBeanCurrent.setNumRounds(util.removeSeriesRounds(value));
                roundsRestVisible();
                if(isOpen)
                    closeFloatList();
                setTimerUsedName();
            });
            btnRemoveRest.setOnClickListener(v -> {
                String valueSec = editTextRestSec.getText().toString();
                String valueMin = editTextRestMin.getText().toString();
                HashMap<String, String> hashMap = util.removeSeconds(valueSec, valueMin);
                editTextRestMin.setText(hashMap.get("min"));
                editTextRestSec.setText(hashMap.get("sec"));
                timerBeanCurrent.setRestMin(hashMap.get("min"));
                timerBeanCurrent.setRestSec(hashMap.get("sec"));
                if(isOpen)
                    closeFloatList();
                setTimerUsedName();
            });
            btnRemoveRestRounds.setOnClickListener(v -> {
                String valueSec = editTextRestRoundsSec.getText().toString();
                String valueMin = editTextRestRoundsMin.getText().toString();
                HashMap<String, String> hashMap = util.removeSeconds(valueSec, valueMin);
                editTextRestRoundsMin.setText(hashMap.get("min"));
                editTextRestRoundsSec.setText(hashMap.get("sec"));
                timerBeanCurrent.setRestRoundsMin(hashMap.get("min"));
                timerBeanCurrent.setRestRoundsSec(hashMap.get("sec"));
                if(isOpen)
                    closeFloatList();
                setTimerUsedName();
            });
            btnRemoveWork.setOnClickListener(v -> {
                String valueSec = editTextWorkSec.getText().toString();
                String valueMin = editTextWorkMin.getText().toString();
                HashMap<String, String> hashMap = util.removeSeconds(valueSec, valueMin);
                editTextWorkMin.setText(hashMap.get("min"));
                editTextWorkSec.setText(hashMap.get("sec"));
                timerBeanCurrent.setWorkMin(hashMap.get("min"));
                timerBeanCurrent.setWorkSec(hashMap.get("sec"));
                if(isOpen)
                    closeFloatList();
                setTimerUsedName();
            });
            //Gestisco il click prolungato
            addClickLong();
            //Gestisco il change dei edittext
            editTextSeries.setOnFocusChangeListener((v, hasFocus) -> {
                String value = editTextSeries.getText().toString();
                if(util.isZeroEmpty(value))
                    value = "1";
                editTextSeries.setText(value);
                timerBeanCurrent.setNumSeries(value);
                if(isOpen)
                    closeFloatList();
                setTimerUsedName();
            });
            editTextRounds.setOnFocusChangeListener((v, hasFocus) -> {
                String value = editTextRounds.getText().toString();
                if(util.isZeroEmpty(value))
                    value = "1";
                editTextRounds.setText(value);
                timerBeanCurrent.setNumRounds(value);
                roundsRestVisible();
                if(isOpen)
                    closeFloatList();
                setTimerUsedName();
            });
            editTextWorkSec.setOnFocusChangeListener((v, hasFocus) -> {
                if(!hasFocus){
                    String sec = editTextWorkSec.getText().toString();
                    String min = editTextWorkMin.getText().toString();
                    TimerBean bean = util.countSecond(sec, min);
                    editTextWorkSec.setText(bean.getNumSec());
                    editTextWorkMin.setText(bean.getNumMin());
                    timerBeanCurrent.setWorkSec(bean.getNumSec());
                    timerBeanCurrent.setWorkMin(bean.getNumMin());
                    if(isOpen)
                        closeFloatList();
                    setTimerUsedName();
                }
            });
            editTextWorkSec.setOnEditorActionListener((v, actionId, event) -> {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    editTextWorkSec.setCursorVisible(false);
                    String sec = editTextWorkSec.getText().toString();
                    String min = editTextWorkMin.getText().toString();
                    TimerBean bean = util.countSecond(sec, min);
                    editTextWorkSec.setText(bean.getNumSec());
                    editTextWorkMin.setText(bean.getNumMin());
                    timerBeanCurrent.setWorkSec(bean.getNumSec());
                    timerBeanCurrent.setWorkMin(bean.getNumMin());
                    if(isOpen)
                        closeFloatList();
                    setTimerUsedName();
                }
                return false;
            });
            editTextRestSec.setOnFocusChangeListener((v, hasFocus) -> {
                if(!hasFocus){
                    String sec = editTextRestSec.getText().toString();
                    String min = editTextRestMin.getText().toString();
                    TimerBean bean = util.countSecond(sec, min);
                    editTextRestSec.setText(bean.getNumSec());
                    editTextRestMin.setText(bean.getNumMin());
                    timerBeanCurrent.setRestSec(bean.getNumSec());
                    timerBeanCurrent.setRestMin(bean.getNumMin());
                    if(isOpen)
                        closeFloatList();
                    setTimerUsedName();
                }
            });

            editTextRestSec.setOnEditorActionListener((v, actionId, event) -> {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    editTextRestSec.setCursorVisible(false);
                    String sec = editTextRestSec.getText().toString();
                    String min = editTextRestMin.getText().toString();
                    TimerBean bean = util.countSecond(sec, min);
                    editTextRestSec.setText(bean.getNumSec());
                    editTextRestMin.setText(bean.getNumMin());
                    timerBeanCurrent.setRestSec(bean.getNumSec());
                    timerBeanCurrent.setRestMin(bean.getNumMin());
                    if(isOpen)
                        closeFloatList();
                    setTimerUsedName();
                }
                return false;
            });
            editTextRestRoundsSec.setOnFocusChangeListener((v, hasFocus) -> {
                if(!hasFocus){
                    String sec = editTextRestRoundsSec.getText().toString();
                    String min = editTextRestRoundsMin.getText().toString();
                    TimerBean bean = util.countSecond(sec, min);
                    editTextRestRoundsSec.setText(bean.getNumSec());
                    editTextRestRoundsMin.setText(bean.getNumMin());
                    timerBeanCurrent.setRestRoundsSec(bean.getNumSec());
                    timerBeanCurrent.setRestRoundsMin(bean.getNumMin());
                    if(isOpen)
                        closeFloatList();
                    setTimerUsedName();
                }
            });

            editTextRestRoundsSec.setOnEditorActionListener((v, actionId, event) -> {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    editTextRestRoundsSec.setCursorVisible(false);
                    String sec = editTextRestRoundsSec.getText().toString();
                    String min = editTextRestRoundsMin.getText().toString();
                    TimerBean bean = util.countSecond(sec, min);
                    editTextRestRoundsSec.setText(bean.getNumSec());
                    editTextRestRoundsMin.setText(bean.getNumMin());
                    timerBeanCurrent.setRestRoundsSec(bean.getNumSec());
                    timerBeanCurrent.setRestRoundsMin(bean.getNumMin());
                    if(isOpen)
                        closeFloatList();
                    setTimerUsedName();
                }
                return false;
            });
            fabStart.setOnClickListener(view -> {
                boolean result = timerBeanIsOk();
                if(result){
                    Intent intent = new Intent(getActivity(), TimerActivity.class);
                    if(settingBean.isInfoPreWork()){
                        timerBeanCurrent.setTimerState(StateType.PRE_WORK);
                    }else {
                        if(!util.isZeroEmpty(timerBeanCurrent.getWorkMin()) && !util.isZeroEmpty(timerBeanCurrent.getWorkSec())){
                            timerBeanCurrent.setTimerState(StateType.WORK);
                        }else if(!util.isZeroEmpty(timerBeanCurrent.getRestMin()) && !util.isZeroEmpty(timerBeanCurrent.getRestSec())){
                            timerBeanCurrent.setTimerState(StateType.REST);
                        }
                    }
                    intent.putExtra("timerBean", timerBeanCurrent);
                    intent.putExtra("settingBean", settingBean);
                    someActivityResultLauncher.launch(intent);
                    if(isOpen)
                        closeFloatList();
                }else {
                    if(isOpen)
                        closeFloatList();
                    if(getContext() != null)
                        WiseToast.warning(getContext(), getString(R.string.seconds_work_rest_0), Toast.LENGTH_SHORT).show();
                }
            });
            fabStart.setOnLongClickListener(v -> {
                if(getContext() != null)
                    WiseToast.info(getContext(), getString(R.string.play_timer_info), Toast.LENGTH_SHORT).show();
                return true;
            });

            fabAddList.setOnClickListener(v -> {
                if(isOpen){
                    closeFloatList();
                }else {
                    openFloatList();
                }
            });

            fabViewList.setOnClickListener(view -> {
                Intent intent = new Intent(getActivity(), ListTimer.class);
                intent.putExtra("settingBean", settingBean);
                intent.putExtra("timerBean", timerBeanCurrent);
                startActivity(intent);
                if(isOpen)
                    closeFloatList();
            });

            fabViewList.setOnLongClickListener(v -> {
                if(getContext() != null)
                    WiseToast.info(getContext(), getString(R.string.listTimer), Toast.LENGTH_SHORT).show();
                return true;
            });
            fabViewSave.setOnLongClickListener(v -> {
                if(getContext() != null)
                    WiseToast.info(getContext(), getString(R.string.saveTimer), Toast.LENGTH_SHORT).show();
                return true;
            });
            textViewTimerUsedNameCustom.setOnClickListener(v -> {
                if(getContext() != null)
                    WiseToast.info(getContext(), getString(R.string.timerUsedName), Toast.LENGTH_SHORT).show();
            });

            fabViewSave.setOnClickListener(view -> {
                if((util.isZeroEmpty(editTextRestSec.getText().toString()) && util.isZeroEmpty(editTextRestMin.getText().toString())) &&
                        (util.isZeroEmpty(editTextWorkSec.getText().toString()) && util.isZeroEmpty(editTextWorkMin.getText().toString()))){
                    if(getContext() != null)
                        WiseToast.warning(getContext(), getString(R.string.seconds_work_rest_0), Toast.LENGTH_SHORT).show();
                }else {
                    createDialogSaveData();
                    if(isOpen)
                        closeFloatList();
                }
            });

            infoPreWorkCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                settingBean.setInfoPreWork(isChecked);
                infoPreWorkOnOff();
                if(getActivity() != null)
                    util.saveSettingBean(settingBean, getActivity());
            });

            infoPreWorkButton.setOnClickListener(v -> createDialogInfoLap());

            infoTimerBtn.setOnClickListener(v -> util.createDialogInfoTimer(timerBeanCurrent, settingBean, getString(R.string.info_timer), getContext()));
            return root;
        }catch (Exception e){
            util.errorReport(getActivity(), e.toString(), settingBean, getContext());
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return null;
    }

    private boolean timerBeanIsOk(){
        String seriesValue = editTextSeries.getText().toString();
        String workSec = editTextWorkSec.getText().toString();
        String workMin = editTextWorkMin.getText().toString();
        String restSec = editTextRestSec.getText().toString();
        String restMin = editTextRestMin.getText().toString();
        String numRounds = editTextRounds.getText().toString();
        String restRoundsSec = editTextRestRoundsSec.getText().toString();
        String restRoundsMin = editTextRestRoundsMin.getText().toString();
        boolean result = true;
        if((util.isZeroEmpty(workSec) && util.isZeroEmpty(workMin)) &&
                (util.isZeroEmpty(restSec) && util.isZeroEmpty(restMin)) &&
                (util.isZeroEmpty(restRoundsSec) && util.isZeroEmpty(restRoundsMin))){
            result = false;
        }else {
            timerBeanCurrent = new TimerBean(seriesValue, workSec, workMin, restSec, restMin, numRounds, restRoundsSec, restRoundsMin);
        }
        return result;
    }

    private void initElement(){
        infoTimerBtn = root.findViewById(R.id.infoTimerBtn);
        btnAddSeries = root.findViewById(R.id.btnAddSerie);
        btnAddRest = root.findViewById(R.id.btnAddRest);
        btnAddWork = root.findViewById(R.id.btnAddWork);
        btnAddRounds = root.findViewById(R.id.btnAddRounds);
        btnAddRestRounds = root.findViewById(R.id.btnAddRestRounds);

        btnRemoveSeries = root.findViewById(R.id.btnRemoveSerie);
        btnRemoveRest = root.findViewById(R.id.btnRemoveRest);
        btnRemoveWork = root.findViewById(R.id.btnRemoveWork);
        btnRemoveRounds = root.findViewById(R.id.btnRemoveRounds);
        btnRemoveRestRounds = root.findViewById(R.id.btnRemoveRestRounds);

        editTextSeries = root.findViewById(R.id.editTextSerie);
        editTextRestMin = root.findViewById(R.id.editTextRestMin);
        editTextRestSec = root.findViewById(R.id.editTextRestSec);
        editTextWorkMin = root.findViewById(R.id.editTextWorkMin);
        editTextWorkSec = root.findViewById(R.id.editTextWorkSec);
        editTextRounds = root.findViewById(R.id.editTextRounds);
        editTextRestRoundsMin = root.findViewById(R.id.editTextRestRoundsMin);
        editTextRestRoundsSec = root.findViewById(R.id.editTextRestRoundsSec);

        restRounds = root.findViewById(R.id.restRounds);
        layButtonRestRounds = root.findViewById(R.id.layButtonRestRounds);

        fabAddList = root.findViewById(R.id.fabAddList);
        fabViewList = root.findViewById(R.id.fabViewList);
        fabViewSave = root.findViewById(R.id.fabViewSave);
        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        fab_rotate_clock = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_rotate_clock);
        fab_rotate_antiClock = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_rotate_anticlock);
        textViewTimerUsedNameCustom = root.findViewById(R.id.textViewTimerUsedNameCustom);
        textViewPreWorkInfo = root.findViewById(R.id.textViewPreWorkInfo);
        infoPreWorkButton = root.findViewById(R.id.infoPreWorkButton);
        infoPreWorkCheckbox = root.findViewById(R.id.infoPreWorkCheckbox);
        infoPreWorkCheckbox.setChecked(settingBean.isInfoPreWork());

        editTextSeries.setText(timerBeanCurrent.getNumSeries());
        editTextRestMin.setText(timerBeanCurrent.getRestMin());
        editTextRestSec.setText(timerBeanCurrent.getRestSec());
        editTextWorkMin.setText(timerBeanCurrent.getWorkMin());
        editTextWorkSec.setText(timerBeanCurrent.getWorkSec());
        editTextRounds.setText(timerBeanCurrent.getNumRounds());
        editTextRestRoundsMin.setText(timerBeanCurrent.getRestRoundsMin());
        editTextRestRoundsSec.setText(timerBeanCurrent.getRestRoundsSec());
        timerName = " " + timerBeanCurrent.getName();
        textViewTimerUsedNameCustom.setText(timerName);
        infoPreWorkOnOff();
        roundsRestVisible();
    }

    private void openFloatList(){
        fabViewList.startAnimation(fab_open);
        fabViewSave.startAnimation(fab_open);
        fabAddList.startAnimation(fab_rotate_clock);
        fabViewList.setClickable(true);
        fabViewSave.setClickable(true);
        isOpen = true;
    }

    private void closeFloatList(){
        fabViewList.startAnimation(fab_close);
        fabViewSave.startAnimation(fab_close);
        fabAddList.startAnimation(fab_rotate_antiClock);
        fabViewList.setClickable(false);
        fabViewSave.setClickable(false);
        isOpen = false;
    }

    private void createDialogSaveData(){
        final ManagerDB managerDB = new ManagerDB(getActivity());
        AlertDialog.Builder builder;
        if(settingBean.isDark()){
            builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyleDark);
        }else {
            builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyleLight);
        }
        View viewSaveDialog = View.inflate(getContext(), R.layout.alert_save_timer, null);
        builder.setCancelable(false);
        builder.setView(viewSaveDialog);
        Button buttonSave = viewSaveDialog.findViewById(R.id.buttonSaveData);
        Button buttonCancel = viewSaveDialog.findViewById(R.id.buttonSaveDataCancel);
        final TextView titleAlert = viewSaveDialog.findViewById(R.id.titleAlert);
        final TextView infoTextSaveTimer = viewSaveDialog.findViewById(R.id.infoTextSaveTimer);
        final TextView countText = viewSaveDialog.findViewById(R.id.countText);
        final EditText editText = viewSaveDialog.findViewById(R.id.editTextDialogUserInput);
        final RelativeLayout boxInfoSaveTimer = viewSaveDialog.findViewById(R.id.boxInfoSaveTimer);
        titleAlert.setText(getString(R.string.alert_save_data_title));
        countText.setText(util.setCountText(editText));
        final AlertDialog dialog = builder.create();
        if(dialog != null){
            if(dialog.getWindow() != null){
                if(getContext() != null)
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getColor(R.color.transparent)));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            }
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                countText.setText(util.setCountText(editText));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countText.setText(util.setCountText(editText));
            }

            @Override
            public void afterTextChanged(Editable s) {
                countText.setText(util.setCountText(editText));
            }
        });
        buttonSave.setOnClickListener(v -> {
            String name = editText.getText().toString();
            if(!"".equalsIgnoreCase(name.trim())){
                managerDB.openReadDB();
                List<TimerBean> timerBeansApp = util.getTimerBeansFromCursor(managerDB.findTimerByName(name));
                managerDB.close();
                if(timerBeansApp == null || timerBeansApp.size() == 0){
                    boolean resultTimerBeanIsOk = timerBeanIsOk();
                    if(resultTimerBeanIsOk){
                        timerBeanCurrent.setName(name);
                        managerDB.openWriteDB();
                        long insertOK = managerDB.insert(timerBeanCurrent);
                        managerDB.close();
                        if(getContext() != null){
                            if(insertOK != -1){
                                WiseToast.success(getContext(), getString(R.string.alert_save_timer_field_ok), Toast.LENGTH_SHORT).show();
                                timerBeanCurrent.setName(getString(R.string.timerUsedNameCustomized));
                            }else {
                                WiseToast.error(getContext(), getString(R.string.alert_save_timer_field_ko), Toast.LENGTH_SHORT).show();
                                timerBeanCurrent.setName(getString(R.string.timerUsedNameCustomized));
                            }
                        }
                    }else {
                        if(getContext() != null)
                            WiseToast.error(getContext(), getString(R.string.seconds_work_rest_0), Toast.LENGTH_SHORT).show();
                    }
                    if(dialog != null)
                        dialog.dismiss();
                }else {
                    infoTextSaveTimer.setText(getString(R.string.already_existing_name));
                    boxInfoSaveTimer.setVisibility(View.VISIBLE);
                }
            }else {
                infoTextSaveTimer.setText(getString(R.string.alert_save_timer_field_required));
                boxInfoSaveTimer.setVisibility(View.VISIBLE);
            }
        });
        buttonCancel.setOnClickListener(v -> {
            if(dialog != null)
                dialog.dismiss();
        });
        if(dialog != null)
            dialog.show();
    }

    private void addClickLong(){
        //add series
        new ContinuousLongClickListener(btnAddSeries, v -> {
            int currentValue = Integer.parseInt(String.valueOf(editTextSeries.getText()));
            int newValue = currentValue + 10;
            editTextSeries.setText(String.valueOf(newValue));
            timerBeanCurrent.setNumSeries(String.valueOf(newValue));
            setTimerUsedName();
            return false;
        });
        //add rounds
        new ContinuousLongClickListener(btnAddRounds, v -> {
            int currentValue = Integer.parseInt(String.valueOf(editTextRounds.getText()));
            int newValue = currentValue + 10;
            editTextRounds.setText(String.valueOf(newValue));
            timerBeanCurrent.setNumRounds(String.valueOf(newValue));
            setTimerUsedName();
            roundsRestVisible();
            return false;
        });
        //add rest
        new ContinuousLongClickListener(btnAddRest, v -> {
            int currentValueSec = Integer.parseInt(String.valueOf(editTextRestSec.getText()));
            int newValue = currentValueSec + 10;
            String sec = editTextRestSec.getText().toString();
            String min = editTextRestMin.getText().toString();
            TimerBean bean = util.countSecondLongClickAdd(sec, min, "" + newValue);
            editTextRestSec.setText(bean.getNumSec());
            editTextRestMin.setText(bean.getNumMin());
            timerBeanCurrent.setRestSec(bean.getNumSec());
            timerBeanCurrent.setRestMin(bean.getRestMin());
            setTimerUsedName();
            return false;
        });
        //add rest rounds
        new ContinuousLongClickListener(btnAddRestRounds, v -> {
            int currentValueSec = Integer.parseInt(String.valueOf(editTextRestRoundsSec.getText()));
            int newValue = currentValueSec + 10;
            String sec = editTextRestRoundsSec.getText().toString();
            String min = editTextRestRoundsMin.getText().toString();
            TimerBean bean = util.countSecondLongClickAdd(sec, min, "" + newValue);
            editTextRestRoundsSec.setText(bean.getNumSec());
            editTextRestRoundsMin.setText(bean.getNumMin());
            timerBeanCurrent.setRestRoundsSec(bean.getNumSec());
            timerBeanCurrent.setRestRoundsMin(bean.getRestMin());
            setTimerUsedName();
            roundsRestVisible();
            return false;
        });
        //add work
        new ContinuousLongClickListener(btnAddWork, v -> {
            int currentValueSec = Integer.parseInt(String.valueOf(editTextWorkSec.getText()));
            int newValue = currentValueSec + 10;
            String sec = editTextWorkSec.getText().toString();
            String min = editTextWorkMin.getText().toString();
            TimerBean bean = util.countSecondLongClickAdd(sec, min, "" + newValue);
            editTextWorkSec.setText(bean.getNumSec());
            editTextWorkMin.setText(bean.getNumMin());
            timerBeanCurrent.setWorkSec(bean.getNumSec());
            timerBeanCurrent.setWorkMin(bean.getRestMin());
            setTimerUsedName();
            return false;
        });
        //remove rest
        new ContinuousLongClickListener(btnRemoveRest, v -> {
            int currentValueSec = Integer.parseInt(String.valueOf(editTextRestSec.getText()));
            int currentValueMin = Integer.parseInt(String.valueOf(editTextRestMin.getText()));
            int newValue = (currentValueMin * 60) + currentValueSec - 10;
            String sec = editTextRestSec.getText().toString();
            String min = editTextRestMin.getText().toString();
            if(newValue > 0){
                TimerBean bean = util.countSecondLongClickRemove(sec, min, "" + newValue);
                sec = bean.getNumSec();
                min = bean.getNumMin();
            }else {
                sec = "00";
                min = "00";
            }
            editTextRestSec.setText(sec);
            editTextRestMin.setText(min);
            timerBeanCurrent.setRestSec(sec);
            timerBeanCurrent.setRestMin(min);
            setTimerUsedName();
            return false;
        });
        //remove rest rounds
        new ContinuousLongClickListener(btnRemoveRestRounds, v -> {
            int currentValueSec = Integer.parseInt(String.valueOf(editTextRestRoundsSec.getText()));
            int currentValueMin = Integer.parseInt(String.valueOf(editTextRestRoundsMin.getText()));
            int newValue = (currentValueMin * 60) + currentValueSec - 10;
            String sec = editTextRestRoundsSec.getText().toString();
            String min = editTextRestRoundsMin.getText().toString();
            if(newValue > 0){
                TimerBean bean = util.countSecondLongClickRemove(sec, min, "" + newValue);
                sec = bean.getNumSec();
                min = bean.getNumMin();
            }else {
                sec = "00";
                min = "00";
            }
            editTextRestRoundsSec.setText(sec);
            editTextRestRoundsMin.setText(min);
            timerBeanCurrent.setRestRoundsSec(sec);
            timerBeanCurrent.setRestRoundsMin(min);
            setTimerUsedName();
            return false;
        });
        //remove work
        new ContinuousLongClickListener(btnRemoveWork, v -> {
            int currentValueSec = Integer.parseInt(String.valueOf(editTextWorkSec.getText()));
            int currentValueMin = Integer.parseInt(String.valueOf(editTextWorkMin.getText()));
            int newValue = (currentValueMin * 60) + currentValueSec - 10;
            String sec = editTextWorkSec.getText().toString();
            String min = editTextWorkMin.getText().toString();
            if(newValue > 0){
                TimerBean bean = util.countSecondLongClickRemove(sec, min, "" + newValue);
                sec = bean.getNumSec();
                min = bean.getNumMin();
            }else {
                sec = "00";
                min = "00";
            }
            editTextWorkSec.setText(sec);
            editTextWorkMin.setText(min);
            timerBeanCurrent.setRestSec(sec);
            timerBeanCurrent.setRestMin(min);
            setTimerUsedName();
            return false;
        });
        //remove series
        new ContinuousLongClickListener(btnRemoveSeries, v -> {
            int currentValue = Integer.parseInt(String.valueOf(editTextSeries.getText()));
            int newValue = currentValue - 10;
            String newValueStr;
            if(newValue > 0){
                newValueStr = String.valueOf(newValue);
            }else {
                newValueStr = "1";
            }
            editTextSeries.setText(newValueStr);
            timerBeanCurrent.setNumSeries(newValueStr);
            setTimerUsedName();
            return false;
        });
        //remove rounds
        new ContinuousLongClickListener(btnRemoveRounds, v -> {
            int currentValue = Integer.parseInt(String.valueOf(editTextRounds.getText()));
            int newValue = currentValue - 10;
            String newValueStr;
            if(newValue > 0){
                newValueStr = String.valueOf(newValue);
            }else {
                newValueStr = "1";
            }
            editTextRounds.setText(newValueStr);
            timerBeanCurrent.setNumRounds(newValueStr);
            setTimerUsedName();
            roundsRestVisible();
            return false;
        });
    }

    private void createDialogInfoLap(){
        AlertDialog.Builder builder;
        if(settingBean.isDark()){
            builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyleDark);
        }else {
            builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyleLight);
        }
        View viewInfoDialog = View.inflate(getContext(), R.layout.alert_info_lap, null);
        builder.setCancelable(false);
        builder.setView(viewInfoDialog);
        Button buttonCancel = viewInfoDialog.findViewById(R.id.buttonCancel);
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
        if(dialog != null)
            dialog.show();
    }

    private void setTimerUsedName(){
        if(timerBeanList != null && timerBeanList.size() > 0 && timerBeanCurrent.isEqual(timerBeanSaved)){
            timerName = " " + timerBeanCurrent.getName();
        }else {
            timerName = " " + getString(R.string.timerUsedNameCustomized);
            TimerBean timerBeanApp = null;
            managerDB.openReadDB();
            timerBeanList = util.getTimerBeansFromCursor(managerDB.findTimerByChecked(1));
            if(timerBeanList != null && timerBeanList.size() > 0)
                timerBeanApp = timerBeanList.get(0);
            if(timerBeanApp != null){
                timerBeanApp.setChecked(false);
                managerDB.update(timerBeanApp);
            }
            managerDB.close();
            if(getActivity() != null)
                util.saveTimerBean(timerBeanCurrent, getActivity());
        }
        textViewTimerUsedNameCustom.setText(timerName);
    }

    private void infoPreWorkOnOff(){
        String preWorkInfoStr = getString(R.string.preWork);
        if(settingBean.isInfoPreWork())
            preWorkInfoStr += " " + getString(R.string.preWorkInfoOn);
        else
            preWorkInfoStr += " " + getString(R.string.preWorkInfoOff);
        textViewPreWorkInfo.setText(preWorkInfoStr);
    }

    private void roundsRestVisible(){
        int numRounds = util.convertStringInteger(editTextRounds.getText().toString());
        if(numRounds > 1){
            restRounds.setVisibility(View.VISIBLE);
            layButtonRestRounds.setVisibility(View.VISIBLE);
        }else {
            restRounds.setVisibility(View.GONE);
            layButtonRestRounds.setVisibility(View.GONE);
            editTextRestRoundsSec.setText(getString(R.string.zero));
            editTextRestRoundsMin.setText(getString(R.string.zero));
            setTimerUsedName();
        }
    }

    /*
        When we open another activity, we can send data to it by using an intent and putExtra.
        But what if we also want to get something back? This is what the startActivityForResult method is for.
        By opening our child activity with this method and overriding onActivityResult we can send data back to
        our parent activity after we set it with setResult in our child activity before we close it
    */
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Util.RESULT_OK) {
                    if(getActivity() != null){
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getActivity());
                        notificationManagerCompat.cancel(0);
                    }
                    if(getContext() != null)
                        WiseToast.success(getContext(), getString(R.string.end_timer), Toast.LENGTH_SHORT).show();
                }
            });

    //Shows the welcome alert
    private void showWelcomeAlert(){
        boolean firstStart = false;
        SharedPreferences prefs;
        if(getContext() != null){
            prefs = getContext().getSharedPreferences(Util.PREFS_NAME_FILE, Context.MODE_PRIVATE);
            firstStart = prefs.getBoolean("firstStart", true);
        }
        if(firstStart){
            AlertDialog.Builder builder;
            if(settingBean.isDark()){
                builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyleDark);
            }else {
                builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyleLight);
            }
            View viewSaveDialog = View.inflate(getContext(), R.layout.alert_welcome, null);
            builder.setCancelable(false);
            builder.setView(viewSaveDialog);
            Button buttonClose = viewSaveDialog.findViewById(R.id.buttonClose);
            final AlertDialog dialog = builder.create();
            if(dialog != null){
                if(dialog.getWindow() != null){
                    if(getContext() != null)
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getColor(R.color.transparent)));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                }
            }
            buttonClose.setOnClickListener(v -> {
                if(dialog != null){
                    dialog.dismiss();
                    if(getActivity() != null){
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(Util.PREFS_NAME_FILE, Context.MODE_PRIVATE).edit();
                        editor.putBoolean("firstStart", false);
                        editor.apply();
                    }
                }
            });
            if(dialog != null)
                dialog.show();
        }
    }

}
