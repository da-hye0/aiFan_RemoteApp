package com.example.myapplication;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MessageUtils {

    private static Toast mToast;

    public static void showShortToastMsg(Context context, String msg) {

        showToastMsg(context, msg, Toast.LENGTH_SHORT, Gravity.CENTER);
    }

    public static void showShortToastMsg(Context context, int stringResId) {
        showShortToastMsg(context, context.getString(stringResId));
    }

    public static void showLongToast(Context context, int stringResId) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(context, context.getString(stringResId), Toast.LENGTH_LONG);
        View v = mToast.getView().findViewById(android.R.id.message);
        if (v != null && v instanceof TextView) {
            ((TextView) v).setGravity(Gravity.CENTER);
        }

        mToast.setText(context.getString(stringResId));
        mToast.show();
    }

    public static void showLongToastStringFormat(Context context, String text) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        mToast.show();
    }

    public static void showLongToastMsg(Context context, String msg) {
        showToastMsg(context, msg, Toast.LENGTH_LONG, Gravity.CENTER);
    }

    public static void showLongToastMsg(Context context, int stringResId) {
        showLongToastMsg(context, context.getString(stringResId));
    }

    public static Toast showToastMsg(Context context, int stringResId, int duration, int gravity) {
        Toast toast = Toast.makeText(context, stringResId, duration);
        View root = toast.getView();
        View v = root.findViewById(android.R.id.message);
        if (v != null && v instanceof TextView) {
            ((TextView) v).setGravity(gravity);
        }
        toast.show();
        return toast;
    }

    public static void showToastMsg(Context context, String msg, int duration, int gravity) {
        if (mToast != null) {
            mToast.cancel();
        }
        if (context == null) return;
        mToast = Toast.makeText(context, msg, duration);
        mToast.show();
        releaseToast(duration == Toast.LENGTH_SHORT ? 1000 : 2000);
    }

    private static void releaseToast(long duration) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                    mToast = null;
                }

            }
        }, duration);
    }

    public static void showToastMsg(Context context, int stringResId, int duration, int gravity, int xOffset, int yOffset) {
        Toast toast = Toast.makeText(context, stringResId, duration);
        View v = toast.getView().findViewById(android.R.id.message);
        if (v != null && v instanceof TextView) {
            TextView textView = (TextView) v;
            textView.setGravity(Gravity.CENTER);

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(textView.getTextSize());
            int textSize = (int) Math.ceil(paint.measureText(context.getString(stringResId)));
            textView.getLayoutParams().width = textSize;
        }

        toast.setGravity(gravity, xOffset, yOffset);
        toast.show();
    }

    public static void showToastMsg(Context context, String message, int duration, int gravity, int xOffset, int yOffset) {
        Toast toast = Toast.makeText(context, message, duration);
        View v = toast.getView().findViewById(android.R.id.message);
        if (v != null && v instanceof TextView) {
            TextView textView = (TextView) v;
            textView.setGravity(Gravity.CENTER);

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(textView.getTextSize());
            int textSize = (int) Math.ceil(paint.measureText(message));
            textView.getLayoutParams().width = textSize;
        }

        toast.setGravity(gravity, xOffset, yOffset);
        toast.show();
    }

}