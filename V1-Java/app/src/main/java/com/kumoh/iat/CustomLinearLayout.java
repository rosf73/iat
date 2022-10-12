package com.kumoh.iat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class CustomLinearLayout extends LinearLayout {

    Context context;

    public CustomLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setAdapter(ArrayAdapter adapter) {
        try {
            if (adapter == null) return;

            ViewGroup parent = (ViewGroup) getChildAt(0);
            parent.removeAllViews();

            for (int i = 0; i < adapter.getCount(); i++) {
                parent.addView(adapter.getView(i, null, parent));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
