package com.kumoh.iat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.widget.ArrayAdapter;
import android.widget.TableRow;
import android.widget.TextView;

public class CustomTableRow extends TableRow {

    Context context;

    public CustomTableRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setAdapter(ArrayAdapter adapter) {
        try {
            if (adapter == null) return;

            // 글자 크기 기기 화면 크기별 유동적 조정
            Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            float density = context.getResources().getDisplayMetrics().density;
            if (size.x > size.y) {
                ((TextView) getChildAt(0)).setTextSize((float) ((int) (size.y / density)/24));
            }
            else {
                ((TextView) getChildAt(0)).setTextSize((float) ((int) (size.x / density)/24));
            }

            for (int i = 0; i < adapter.getCount(); i++) {
                addView(adapter.getView(i, null, this));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
