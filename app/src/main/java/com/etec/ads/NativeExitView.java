package com.etec.ads;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class NativeExitView implements View.OnTouchListener {
    private RelativeLayout mView;
    private View.OnClickListener mClickListener;

    protected static NativeExitView mInstance;

    public static NativeExitView instance() {
        if (mInstance == null) {
            mInstance = new NativeExitView();
        }
        return mInstance;
    }

    public void show(final View adView, View.OnClickListener l) {
        if (mView == null) {
            LayoutInflater inflater = LayoutInflater.from(AdsManager.instance().getActivity());
            mView = (RelativeLayout)inflater.inflate(R.layout.native_exit_ad_view, null, false);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            AdsManager.instance().getActivity().addContentView(mView,lp);
        }
        mView.bringToFront();

        FrameLayout frameLayout = mView.findViewById(R.id.ad_placeholder);
        frameLayout.removeAllViews();
        frameLayout.addView(adView);

        mView.findViewById(R.id.btn_native_exit_confirm).setOnClickListener(l);
        mView.findViewById(R.id.btn_native_exit_cancel).setOnClickListener(l);

        mView.setOnTouchListener(this);
    }

    public void close() {
        if (mView != null) {
            ViewGroup vg = (ViewGroup) mView.getParent();
            vg.removeView(mView);
            mView = null;
        }
    }

    public static void export_close() {
        System.out.println("NativeExitView export_close");
        AdsManager.instance().getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NativeExitView.instance().close();
            }
        });
    }

    @Override
    public boolean onTouch(View var1, MotionEvent var2) {
        return true;
    }
}
