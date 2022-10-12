package com.kumoh.iat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;

public class CustomTableView extends TableLayout {

    Context context;

    public CustomTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setAdapter(ArrayAdapter adapter) {
        try {
            if (adapter == null) return;

            for (int i = 0; i < adapter.getCount(); i++) {
                addView(adapter.getView(i, null, this));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
