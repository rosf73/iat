package com.kumoh.iat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CustomCaseItemAdapter extends ArrayAdapter<String> {

    Context context;
    String number;
    int selected;
    String[] cases;
    Integer[] is_assay_list;

    public CustomCaseItemAdapter(@NonNull Context context, int resource,
                                 String selected, String number, String[] cases, Integer[] is_assay_list) {
        super(context, resource, cases);

        this.context = context;
        this.number = number;
        this.selected = selected.equals("") ? -1 : Integer.parseInt(selected.split("_")[0]);
        this.cases = cases;
        this.is_assay_list = is_assay_list;
    }

    @Override
    public int getCount() {
        return cases.length;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View item = View.inflate(context, R.layout.custom_case_item, null);
        ImageView check_box = (ImageView) item.findViewById(R.id.check_box);
        Button case_text = (Button) item.findViewById(R.id.case_text);
        final EditText case_edit = (EditText) item.findViewById(R.id.case_edit);

        // 글자 크기 기기 화면 크기별 유동적 조정
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float density = context.getResources().getDisplayMetrics().density;
        if (size.x > size.y) {
            case_text.setTextSize((float) ((int) (size.y / density)/24));
        }
        else {
            case_text.setTextSize((float) ((int) (size.x / density)/24));
        }

        case_text.setText(cases[position]);
        if (selected == position+1) {
            check_box.setImageResource(R.drawable.ic_check_box);
            case_text.setTextColor(Color.rgb(0xDB, 0x1B, 0x60));
            if (ParticipantGlobalData.getInstance().getAnswer(number).split("_").length > 1)
                case_edit.setText(ParticipantGlobalData.getInstance().getAnswer(number).split("_")[1]);
            case_edit.setEnabled(true);
        }
        else {
            check_box.setImageResource(R.drawable.ic_non_check);
            case_text.setTextColor(Color.rgb(0xAA, 0xAA, 0xAA));
        }
        if (is_assay_list[position] != 0) case_edit.setVisibility(View.VISIBLE);
        else case_edit.setVisibility(View.INVISIBLE);

        check_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int j = 0; j < ((LinearLayout) view.getParent().getParent()).getChildCount(); j++) {
                    ((ImageView) ((LinearLayout) ((LinearLayout) view.getParent().getParent()).getChildAt(j)).getChildAt(0)).setImageResource(R.drawable.ic_non_check);
                    ((Button) ((LinearLayout) ((LinearLayout) view.getParent().getParent()).getChildAt(j)).getChildAt(1)).setTextColor(Color.rgb(0xAA, 0xAA, 0xAA));
                    ((EditText) ((LinearLayout) ((LinearLayout) view.getParent().getParent()).getChildAt(j)).getChildAt(2)).setEnabled(false);
                }
                ((Button) ((LinearLayout) view.getParent()).getChildAt(1)).setTextColor(Color.rgb(0xDB, 0x1B, 0x60));
                ((ImageView) view).setImageResource(R.drawable.ic_check_box);
                case_edit.setEnabled(true);

                if (case_edit.getText().toString().equals(""))
                    ParticipantGlobalData.getInstance().putAnswer(number, (position+1) + "");
                else
                    ParticipantGlobalData.getInstance().putAnswer(number, (position+1) + "_" + case_edit.getText().toString());
                ParticipantGlobalData.getInstance().setRootProgress();
            }
        });
        case_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    ParticipantGlobalData.getInstance().putAnswer(number, (position+1) + "_" + textView.getText().toString());
                    ParticipantGlobalData.getInstance().setRootProgress();
                    return false;
                }

                return true;
            }
        });
        case_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int j = 0; j < ((LinearLayout) view.getParent().getParent()).getChildCount(); j++) {
                    ((ImageView) ((LinearLayout) ((LinearLayout) view.getParent().getParent()).getChildAt(j)).getChildAt(0)).setImageResource(R.drawable.ic_non_check);
                    ((Button) ((LinearLayout) ((LinearLayout) view.getParent().getParent()).getChildAt(j)).getChildAt(1)).setTextColor(Color.rgb(0xAA, 0xAA, 0xAA));
                    ((EditText) ((LinearLayout) ((LinearLayout) view.getParent().getParent()).getChildAt(j)).getChildAt(2)).setEnabled(false);
                }
                ((ImageView) ((LinearLayout) view.getParent()).getChildAt(0)).setImageResource(R.drawable.ic_check_box);
                ((Button) view).setTextColor(Color.rgb(0xDB, 0x1B, 0x60));
                case_edit.setEnabled(true);

                if (case_edit.getText().toString().equals(""))
                    ParticipantGlobalData.getInstance().putAnswer(number, (position+1) + "");
                else
                    ParticipantGlobalData.getInstance().putAnswer(number, (position+1) + "_" + case_edit.getText().toString());
                ParticipantGlobalData.getInstance().setRootProgress();
            }
        });
        case_edit.setImeOptions(EditorInfo.IME_ACTION_DONE);

        return item;
    }
}
