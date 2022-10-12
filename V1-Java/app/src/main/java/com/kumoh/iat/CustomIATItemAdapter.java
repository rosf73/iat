package com.kumoh.iat;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomIATItemAdapter extends ArrayAdapter<String> {

    Context context;
    String number;
    String[] questions;
    JSONArray[] word_arr;
    JSONArray[] step_arr;

    public CustomIATItemAdapter(@NonNull Context context, int resource, String number, String[] questions, JSONArray[] word_arr, JSONArray[] step_arr) {
        super(context, resource, questions);

        this.context = context;
        this.number = number;
        this.questions = questions;
        this.word_arr = word_arr;
        this.step_arr = step_arr;
    }

    @Override
    public int getCount() {
        return questions.length;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View item = View.inflate(context, R.layout.custom_iat_item, null);
        TextView iat_title = (TextView) item.findViewById(R.id.iat_title);
        Button iat_button = (Button) item.findViewById(R.id.iat_button);

        iat_title.setText("("+(position+1)+")  "+questions[position]);
        final ArrayList<ArrayList<WordData>> words_list = new ArrayList<>();
        final ArrayList<ArrayList<StepData>> steps_list = new ArrayList<>();
        try {
            for (int k = 0; k < questions.length; k++) {
                ArrayList<WordData> word_list = new ArrayList<>();
                ArrayList<StepData> step_list = new ArrayList<>();
                for (int i = 0; i < word_arr[k].length(); i++) {
                    JSONObject word = word_arr[k].getJSONObject(i);
                    JSONArray word_arr = word.getJSONArray("words");
                    List<String> list = new ArrayList<>();
                    for (int j = 0; j < word_arr.length(); j++) list.add(word_arr.getString(j));
                    String[] words = list.toArray(new String[list.size()]);
                    word_list.add(new WordData(word.getString("subject"), words));
                }
                for (int i = 0; i < step_arr[k].length(); i++) {
                    JSONObject step = step_arr[k].getJSONObject(i);
                    JSONArray title_arr = step.getJSONArray("title");
                    JSONArray left_arr = step.getJSONArray("left");
                    JSONArray right_arr = step.getJSONArray("right");
                    List<String> list = new ArrayList<>();
                    for (int j = 0; j < title_arr.length(); j++) list.add(title_arr.getString(j));
                    String[] title = list.toArray(new String[list.size()]);
                    list.clear();
                    for (int j = 0; j < left_arr.length(); j++) list.add(left_arr.getString(j));
                    String[] left = list.toArray(new String[list.size()]);
                    list.clear();
                    for (int j = 0; j < right_arr.length(); j++) list.add(right_arr.getString(j));
                    String[] right = list.toArray(new String[list.size()]);
                    step_list.add(new StepData(title, step.getInt("trial"), left, right));
                }
                words_list.add(word_list);
                steps_list.add(step_list);
            }

            iat_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AlertActivity.class);
                    intent.putExtra("number", number+"_"+(position+1));
                    intent.putExtra("words", words_list.get(position));
                    intent.putExtra("steps", steps_list.get(position));

                    context.startActivity(intent);
                }
            });
            if (!ParticipantGlobalData.getInstance().getAnswer(number+"_"+(position+1)).equals(""))
                iat_button.setEnabled(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }
}
