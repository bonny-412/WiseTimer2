package it.bonny.app.wisetimer2.wisetoast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import it.bonny.app.wisetimer2.R;

@SuppressLint("InflateParams")
public class WiseToast {
    private static final Typeface LOADED_TOAST_TYPEFACE = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
    private static final Typeface currentTypeface = LOADED_TOAST_TYPEFACE;
    private Toast lastToast = null;

    public WiseToast() {}

    @CheckResult
    public Toast warning(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return warning(context, message, duration, true);
    }

    @CheckResult
    public Toast warning(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, WiseToastUtil.getDrawable(context, R.drawable.ic_error_outline_white_24dp_toast),
                WiseToastUtil.getColor(context, R.color.newColorYellow), WiseToastUtil.getColor(context, R.color.newColorBg),
                duration, withIcon, true);
    }

    @CheckResult
    public Toast info(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return info(context, message, duration, true);
    }

    @CheckResult
    public Toast info(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, WiseToastUtil.getDrawable(context, R.drawable.ic_info_outline_white_24dp_toast),
                WiseToastUtil.getColor(context, R.color.newColorBlue), WiseToastUtil.getColor(context, R.color.newColorBg),
                duration, withIcon, true);
    }

    @CheckResult
    public Toast success(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return success(context, message, duration, true);
    }

    @CheckResult
    public Toast success(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, WiseToastUtil.getDrawable(context, R.drawable.ic_check_white_24dp_toast),
                WiseToastUtil.getColor(context, R.color.newColorGreen), WiseToastUtil.getColor(context, R.color.newColorBg),
                duration, withIcon, true);
    }

    @CheckResult
    public Toast error(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return error(context, message, duration, true);
    }

    @CheckResult
    public Toast error(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, WiseToastUtil.getDrawable(context, R.drawable.ic_clear_white_24dp_toast),
                WiseToastUtil.getColor(context, R.color.newColorRed), WiseToastUtil.getColor(context, R.color.newColorBg),
                duration, withIcon, true);
    }

    @SuppressLint("ShowToast")
    @CheckResult
    public Toast custom(@NonNull Context context, @NonNull CharSequence message, Drawable icon,
                               @ColorInt int tintColor, @ColorInt int textColor, int duration,
                               boolean withIcon, boolean shouldTint) {
        final Toast currentToast = Toast.makeText(context, "", duration);
        final View toastLayout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.toast_layout, null);
        final ImageView toastIcon = toastLayout.findViewById(R.id.toast_icon);
        final TextView toastTextView = toastLayout.findViewById(R.id.toast_text);
        Drawable drawableFrame;

        if (shouldTint)
            drawableFrame = WiseToastUtil.tint9PatchDrawableFrame(context, tintColor);
        else
            drawableFrame = WiseToastUtil.getDrawable(context, R.drawable.toast_frame);
        WiseToastUtil.setBackground(toastLayout, drawableFrame);

        if (withIcon) {
            if (icon == null)
                throw new IllegalArgumentException("Avoid passing 'icon' as null if 'withIcon' is set to true");
            WiseToastUtil.setBackground(toastIcon, WiseToastUtil.tintIcon(icon, textColor));
        } else {
            toastIcon.setVisibility(View.GONE);
        }

        toastTextView.setText(message);
        toastTextView.setTextColor(textColor);
        toastTextView.setTypeface(currentTypeface);
        int textSize = 16;
        toastTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        currentToast.setView(toastLayout);

        if(lastToast != null)
            lastToast.cancel();
        lastToast = currentToast;

        return currentToast;
    }

}
