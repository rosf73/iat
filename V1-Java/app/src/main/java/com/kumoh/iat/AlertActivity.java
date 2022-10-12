package com.kumoh.iat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;

public class AlertActivity extends AppCompatActivity {

    ScrollView alert_scroll;
    CustomTableView table;
    TextView test_text;
    Button button;
    LinearLayout alert_progress;

    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_alert);

        // 하고 있는 공부 적용
        number = getIntent().getStringExtra("number");

        alert_scroll = (ScrollView) findViewById(R.id.alert_scroll);
        table = (CustomTableView) findViewById(R.id.word_table);
        test_text = (TextView) findViewById(R.id.test_text);
        button = (Button) findViewById(R.id.button);
        alert_progress = (LinearLayout) findViewById(R.id.alert_progress);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AlertActivity.this)
                        .setTitle("안내")
                        .setMessage("마구잡이로 눌러 참여할 시 보상이 제공되지 않습니다. 신중하게 누르십시오.")
                        .setPositiveButton("신중히 참여하겠습니다", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent(getApplicationContext(), TestingActivity.class);
                                intent.putExtra("number", number);

                                startActivity(intent);
                            }
                        })
                        .create()
                        .show();
            }
        });

        new JSONAlertTask().execute();
    }

    public class JSONAlertTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... anything) {
            int curr_step = ParticipantGlobalData.getInstance().test_step;
            ArrayList<WordData> curr_list = ParticipantGlobalData.getInstance().word_list.get(curr_step);
            try {
                String[] categories = new String[curr_list.size()];
                String[] words_list = new String[curr_list.size()];
                for (int i = 0; i < curr_list.size(); i++) {
                    String str = curr_list.get(i).getWords()[0];
                    for (int j = 1; j < curr_list.get(i).getWords().length; j++)
                        str += ", " + curr_list.get(i).getWords()[j];

                    categories[i] = curr_list.get(i).getSubject();
                    words_list[i] = str;
                }

                CustomWordTableAdapter adapter = new CustomWordTableAdapter(AlertActivity.this, R.layout.custom_word_table_item,
                        categories, words_list);
                table.setAdapter(adapter);

                return "SUCCESS";
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            if (res != null) {
                alert_scroll.setVisibility(View.VISIBLE);
                alert_progress.setVisibility(View.GONE);
            }
        }
    }
}
