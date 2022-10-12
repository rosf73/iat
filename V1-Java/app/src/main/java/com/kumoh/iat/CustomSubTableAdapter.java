package com.kumoh.iat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CustomSubTableAdapter extends ArrayAdapter<String> {

    Activity activity;
    Context context;
    String number;
    String[] questions;
    String[] cases;

    public CustomSubTableAdapter(@NonNull Context context, Activity activity, int resource, String number, String[] questions, String[] cases) {
        super(context, resource, questions);

        this.activity = activity;
        this.context = context;
        this.number = number;
        this.questions = questions;
        this.cases = cases;
    }

    @Override
    public int getCount() {
        return questions.length;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View item = View.inflate(context, R.layout.custom_table_item, null);
        CustomTableRow table_row = (CustomTableRow) item.findViewById(R.id.table_row);
        TextView table_question = (TextView) item.findViewById(R.id.table_question);

        table_row.setBackgroundColor(position%2 == 0 ? Color.rgb(0xF2, 0xF2, 0xF2) : Color.rgb(0xFF, 0xFF, 0xFF));
        table_question.setText(questions[position]);
        String[] temp = new String[cases.length];
        for (int i = 0; i < cases.length; i++) {
            temp[i] = (i+1)+"";
        }
        CustomTableCaseAdapter adapter = new CustomTableCaseAdapter(context, activity, R.layout.custom_table_case_item,
                ParticipantGlobalData.getInstance().getAnswer(number+"_"+(position+1)), number+"_"+(position+1), temp);
        table_row.setAdapter(adapter);

        for (int i = 1; i < table_row.getChildCount(); i++) {
            ((TextView) table_row.getChildAt(i)).setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        }

        return item;
    }
}
