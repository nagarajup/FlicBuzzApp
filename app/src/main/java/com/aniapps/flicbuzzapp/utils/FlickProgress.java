package com.aniapps.flicbuzzapp.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.aniapps.flicbuzzapp.R;

public class FlickProgress extends LinearLayout implements Runnable {

    private Context context;
    private Thread animationThread;
    private boolean stopped = true;
    private int i = 0;
    int width = 320;
    private LinearLayout lay, main_lay;
    private ImageView img_in, img_up;

    public FlickProgress(Context context) {
        super(context);
        this.context = context;
        prepareLayout();
    }

    public FlickProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        prepareLayout();
    }

    /**
     * This is called when you want the dialog to be dismissed
     */
    public void dismiss() {
        stopped = true;
        setVisibility(View.GONE);
    }

    /**
     * Loads the layout and sets the initial set of images
     */
    private void prepareLayout() {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.myprogressbar, null);
        addView(view);
        lay = (LinearLayout) findViewById(R.id.lay);
        main_lay = (LinearLayout) findViewById(R.id.main_lay);
        img_in = (ImageView) findViewById(R.id.img_in);
        img_up = (ImageView) findViewById(R.id.img_up);
        ViewTreeObserver viewTree = img_in.getViewTreeObserver();
        viewTree.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                main_lay.getLayoutParams().width = img_in.getWidth();
                img_up.getLayoutParams().width = img_in.getWidth();
                width = img_in.getWidth();
                return true;
            }
        });
    }

    /**
     * Starts the animation thread
     */
    public void startAnimation() {
        setVisibility(View.VISIBLE);
        animationThread = new Thread(this, "Progress");
        animationThread.start();
    }

    @Override
    public void run() {
        while (stopped) {
            try {
                // Sleep for 0.3 secs and after that change the images
                Thread.sleep(180);
                handler.sendEmptyMessage(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (i > width)
                i = 0;
            lay.setLayoutParams(new LayoutParams(i, LayoutParams.WRAP_CONTENT));
            i = i + (width / 5);
            super.handleMessage(msg);
        }

    };

}
