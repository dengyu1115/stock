package com.nature.stock.common.util;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.nature.stock.R;


/**
 * 之前都是写在Activity中，感觉代码好多，还是封装到这里好
 */
public class ViewUtil {

    public static Button button(Context context) {
        Button button = new Button(context);
        Drawable drawable = context.getDrawable(R.drawable.common_background);
        float density = context.getResources().getDisplayMetrics().density;
        int width = (int) (60 * density + 0.5f);
        int height = (int) (30 * density + 0.5f);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        button.setLayoutParams(params);
        button.setGravity(Gravity.CENTER);
        button.setPadding(0, 0, 0, 0);
        button.setBackground(drawable);
        return button;
    }

    public static Button button(Context context, String name) {
        Button button = button(context);
        button.setText(name);
        return button;
    }

    public static TextView textView(Context context, String name) {
        TextView text = new TextView(context);
        text.setText(name);
        float density = context.getResources().getDisplayMetrics().density;
        int width = (int) (80 * density + 0.5f);
        int height = (int) (30 * density + 0.5f);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        text.setLayoutParams(params);
        text.setPadding(10, 10, 10, 10);
        text.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        return text;
    }

    public static EditText editText(Context context) {
        EditText editText = new EditText(context);
        Drawable drawable = context.getDrawable(R.drawable.common_background);
        float density = context.getResources().getDisplayMetrics().density;
        int width = (int) (100 * density + 0.5f);
        int height = (int) (30 * density + 0.5f);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        editText.setLayoutParams(params);
        editText.setGravity(Gravity.CENTER);
        editText.setPadding(0, 0, 0, 0);
        editText.setBackground(drawable);
        return editText;
    }

    public static void initActivity(AppCompatActivity context) {
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = context.getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        Window window = context.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
