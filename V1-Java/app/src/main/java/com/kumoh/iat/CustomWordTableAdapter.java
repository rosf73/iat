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

public class CustomWordTableAdapter extends ArrayAdapter<String> {

    Context context;
    String[] categories;
    String[] words_list;

    public CustomWordTableAdapter(@NonNull Context context, int resource, String[] categories, String[] words_list) {
        super(context, resource, categories);

        this.context = context;
        this.categories = categories;
        this.words_list = words_list;
    }

    @Override
    public int getCount() {
        return categories.length;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View item = View.inflate(context, R.layout.custom_word_table_item, null);
        TextView category = (TextView) item.findViewById(R.id.category);
        TextView words = (TextView) item.findViewById(R.id.words);

        // 글자 크기 기기 화면 크기별 유동적 조정
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float density = context.getResources().getDisplayMetrics().density;
        if (size.x > size.y) {
            category.setTextSize((float) ((int) (size.y / density)/22));
            words.setTextSize((float) ((int) (size.y / density)/24));
        }
        else {
            category.setTextSize((float) ((int) (size.x / density)/22));
            words.setTextSize((float) ((int) (size.x / density)/24));
        }

        category.setText(categories[position]);
        words.setText(words_list[position]);

        return item;
    }
}
