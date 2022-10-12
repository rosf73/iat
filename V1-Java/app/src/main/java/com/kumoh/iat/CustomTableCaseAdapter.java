package com.kumoh.iat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CustomTableCaseAdapter extends ArrayAdapter<String> {

    Activity activity;
    Context context;
    int selected;
    String number;
    String[] cases;

    public CustomTableCaseAdapter(@NonNull Context context, Activity activity, int resource, String selected, String number, String[] cases) {
        super(context, resource, cases);

        this.activity = activity;
        this.context = context;
        this.selected = selected.equals("") ? -1 : Integer.parseInt(selected.substring(0, 1));
        this.number = number;
        this.cases = cases;
    }

    @Override
    public int getCount() {
        return cases.length;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View item = View.inflate(context, R.layout.custom_table_case_item, null);
        TextView number_case = (TextView) item.findViewById(R.id.number);

        number_case.setText(cases[position]);

        // 글자 크기 기기 화면 크기별 유동적 조정
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float density = context.getResources().getDisplayMetrics().density;
        if (size.x > size.y) {
            number_case.setTextSize((float) ((int) (size.y / density)/32));
        }
        else {
            number_case.setTextSize((float) ((int) (size.x / density)/32));
        }

        if (cases[position].length() == 1) {
            if (selected == position+1) {
                number_case.setBackgroundColor(Color.rgb(0x6F, 0xAD, 0xDB));
            }
            else {
                number_case.setBackgroundColor(android.R.attr.colorBackground);
            }
            number_case.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int j = 1; j < ((TableRow) view.getParent()).getChildCount(); j++) {
                        ((TextView) ((TableRow) view.getParent()).getChildAt(j)).setBackgroundColor(android.R.attr.colorBackground);
                    }
                    ((TextView) view).setBackgroundColor(Color.rgb(0x6F, 0xAD, 0xDB));

                    ParticipantGlobalData.getInstance().putAnswer(number, (position+1)+"");
                    ParticipantGlobalData.getInstance().setRootProgress();
                }
            });
        }

        return item;
    }
}
