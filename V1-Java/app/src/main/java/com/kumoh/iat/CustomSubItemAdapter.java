package com.kumoh.iat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CustomSubItemAdapter extends ArrayAdapter<String> {

    Context context;
    String number;
    String[] questions;
    String[][] cases;

    public CustomSubItemAdapter(@NonNull Context context, int resource, String number, String[] questions, String[][] cases) {
        super(context, resource, questions);

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
        View item = View.inflate(context, R.layout.custom_sub_item, null);
        TextView sub_question = (TextView) item.findViewById(R.id.sub_question_view);
        CustomLinearLayout ll = (CustomLinearLayout) item.findViewById(R.id.sub_case_view);

        // 글자 크기 기기 화면 크기별 유동적 조정
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float density = context.getResources().getDisplayMetrics().density;
        if (size.x > size.y) {
            sub_question.setTextSize((float) ((int) (size.y / density)/21));
        }
        else {
            sub_question.setTextSize((float) ((int) (size.x / density)/21));
        }

        sub_question.setText("("+(position+1)+")  "+questions[position]);
        Integer[] dumy_list = new Integer[questions.length];
        for (int i = 0; i < questions.length; i++) dumy_list[i] = 0;
        CustomCaseItemAdapter adapter = new CustomCaseItemAdapter(context, R.layout.custom_case_item,
                ParticipantGlobalData.getInstance().getAnswer(number+"_"+(position+1)), number+"_"+(position+1), cases[position], dumy_list);
        ll.setAdapter(adapter);

        return item;
    }
}
