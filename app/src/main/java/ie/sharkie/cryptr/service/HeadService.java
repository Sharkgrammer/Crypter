package ie.sharkie.cryptr.service;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import ie.sharkie.cryptr.R;

public class HeadService extends Service {

    private WindowManager windowManager;
    private ImageView head;
    private WindowManager.LayoutParams paramsHead, paramsDialog;
    private boolean runClickListener, headTouch;
    private int paramX, paramY;
    private float eventX, eventY;
    private View dialog;

    View.OnTouchListener viewTouchListener = (v, event) -> {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                if (headTouch) {
                    paramX = paramsHead.x;
                    paramY = paramsHead.y;
                } else {
                    paramX = paramsDialog.x;
                    paramY = paramsDialog.y;
                }

                eventX = event.getRawX();
                eventY = event.getRawY();

                runClickListener = true;
                return true;

            case MotionEvent.ACTION_UP:

                if (runClickListener) {
                    v.performClick();
                }

                return true;

            case MotionEvent.ACTION_MOVE:

                int moveX, moveY, toMove = 5;

                moveX = (int) (event.getRawX() - eventX);
                moveY = (int) (event.getRawY() - eventY);

                if (Math.abs(moveX) > toMove || Math.abs(moveY) > toMove) {
                    runClickListener = false;
                } else {
                    return true;
                }

                if (headTouch) {
                    paramsHead.x = paramX + moveX;
                    paramsHead.y = paramY + moveY;
                    windowManager.updateViewLayout(head, paramsHead);
                } else {
                    paramsDialog.x = paramX + moveX;
                    paramsDialog.y = paramY + moveY;
                    windowManager.updateViewLayout(dialog, paramsDialog);
                }
                return true;

        }
        return false;

    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();

        head = new ImageView(this);
        head.setImageResource(R.drawable.ic_launcher_background);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        headTouch = true;

        head.setOnClickListener(v -> {

            /*
            Intent i = new Intent(c, DialogActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            */

            headTouch = false;

            LayoutInflater li = LayoutInflater.from(getApplicationContext());
            dialog = li.inflate(R.layout.activity_dialog, null);

            dialog.setOnTouchListener(viewTouchListener);

            Button b = dialog.findViewById(R.id.button);
            Button b2 = dialog.findViewById(R.id.button2);

            b.setOnClickListener(v1 -> Toast.makeText(getApplicationContext(), "Look here", Toast.LENGTH_LONG).show());
            b2.setOnClickListener(v1 -> {
                headTouch = true;
                windowManager.removeView(dialog);
                windowManager.addView(head, paramsHead);
            });

            windowManager.addView(dialog, paramsDialog);
            windowManager.removeView(head);
        });

        head.setOnTouchListener(viewTouchListener);

        paramsHead = createParams(0, 100, false);
        paramsDialog = createParams(0, 0, true);

        windowManager.addView(head, paramsHead);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (head != null) windowManager.removeView(head);
    }

    public WindowManager.LayoutParams createParams(int x, int y, boolean center) {

        int layoutFlag;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams p = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                x,
                y,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        if (center) {
            p.gravity = Gravity.CENTER;
        } else {
            p.gravity = Gravity.TOP | Gravity.START;
        }

        return p;
    }

}
