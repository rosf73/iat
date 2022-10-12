package com.kumoh.iat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SurveyTempActivity extends AppCompatActivity implements View.OnClickListener {

    private static class Question {
        String question;
    }
    private static class BaseQuestion extends Question {
        String[] cases;
        Integer[] is_assay_list;
    }
    private static class TableQuestion extends Question {
        String[] sub_contents;
        String[] cases;
    }
    private static class SubQuestion extends Question {
        String[] sub_contents;
        String[][] sub_cases;
    }
    private static class IATQuestion extends Question { }
    private static class AssayQuestion extends Question { }

    LinearLayout root_layout, notice_layout, survey_layout, single_view;
    ScrollView survey_scroll;
    TextView title_bar, number_view, question_view, first_cell;
    ProgressBar modal_progress, survey_progress;
    CustomTableView table_view, table_title_view;
    CustomTableRow first_row;
    CustomLinearLayout base_case_view, sub_view;
    EditText assay_edit;
    Button start_button, prev, next, iat_view;
    View row_line;

    // 문항 데이터
    int[] types;
    Question[] q_list;
    int curr_question, static_fidelity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.temp_activity_survey);

        curr_question = getIntent().getIntExtra("current", 0);

        root_layout = (LinearLayout) findViewById(R.id.root_layout);
        notice_layout = (LinearLayout) findViewById(R.id.notice_layout);
        survey_layout = (LinearLayout) findViewById(R.id.survey_layout);
        survey_scroll = (ScrollView) findViewById(R.id.survey_scroll);
        single_view = (LinearLayout) findViewById(R.id.single_view);

        title_bar = (TextView) findViewById(R.id.title_bar);
        number_view = (TextView) findViewById(R.id.number_view);
        question_view = (TextView) findViewById(R.id.question_view);
        first_cell = (TextView) findViewById(R.id.first_cell);

        modal_progress = (ProgressBar) findViewById(R.id.modal_progress);
        survey_progress = (ProgressBar) findViewById(R.id.survey_progress);

        table_view = (CustomTableView) findViewById(R.id.table_view);
        table_title_view = (CustomTableView) findViewById(R.id.table_title_view);
        first_row = (CustomTableRow) findViewById(R.id.first_row);
        base_case_view = (CustomLinearLayout) findViewById(R.id.base_case_view);
        sub_view = (CustomLinearLayout) findViewById(R.id.sub_view);
        iat_view = (Button) findViewById(R.id.iat_view);
        row_line = findViewById(R.id.row_line);

        assay_edit = (EditText) findViewById(R.id.assay_edit);
        start_button = (Button) findViewById(R.id.start_button);
        prev = (Button) findViewById(R.id.prev);
        next = (Button) findViewById(R.id.next);

        // 글자 크기 기기 화면 크기별 유동적 조정
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float density = getResources().getDisplayMetrics().density;
        if (size.x > size.y) {
            number_view.setTextSize((float) ((int) (size.y / density)/12));
            question_view.setTextSize((float) ((int) (size.y / density)/20));
        }
        else {
            number_view.setTextSize((float) ((int) (size.x / density)/12));
            question_view.setTextSize((float) ((int) (size.x / density)/20));
        }

        start_button.setOnClickListener(this);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);
        iat_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AlertActivity.class);
                intent.putExtra("number", (curr_question+1)+"");

                startActivity(intent);
            }
        });

        ParticipantGlobalData.getInstance().setRootParentView(root_layout); // progress bar 컨트롤 용도

        if (curr_question > 0) { // 작성 중이던 설문이 있을 경우 화면 컨트롤
            if (curr_question >= ParticipantGlobalData.getInstance().getQuestionsLength()) // IAT가 마지막 문항이었을경우
                curr_question = ParticipantGlobalData.getInstance().getQuestionsLength() - 1;

            title_bar.setVisibility(View.GONE);
            notice_layout.setVisibility(View.GONE);
            survey_layout.setVisibility(View.VISIBLE);
            prev.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            survey_progress.setMax(ParticipantGlobalData.getInstance().answers.length());
            survey_progress.setProgress(ParticipantGlobalData.getInstance().getAnsweredLength());

            try {
                JSONArray temp_arr = ParticipantGlobalData.getInstance().questions;

                types = new int[temp_arr.length()];
                q_list = new Question[temp_arr.length()];

                // data forming
                for (int i = 0; i < temp_arr.length(); i++) {
                    types[i] = temp_arr.getJSONObject(i).getInt("type");
                    if (types[i] == 1) makeBaseQuestion(i, temp_arr);
                    if (types[i] == 2) makeTableQuestion(i, temp_arr);
                    if (types[i] == 3) makeSubQuestion(i, temp_arr);
                    if (types[i] == 4) makeIATQuestion(i, temp_arr);
                    if (types[i] == 5) makeAssayQuestion(i, temp_arr);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            printCurrentQuestion();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == start_button.getId()) {
            title_bar.setVisibility(View.GONE);
            notice_layout.setVisibility(View.GONE);
            modal_progress.setVisibility(View.VISIBLE);
            prev.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            prev.setEnabled(false);
            next.setEnabled(false);
            new JSONSurveyTask().execute("http://101.101.208.212:8080/question/all");
        }
        else if (view.getId() == prev.getId()) {
            if (--curr_question <= 0) prev.setEnabled(false);
            next.setEnabled(true);
            printCurrentQuestion();
        }
        else if (view.getId() == next.getId()) {
            if (!ParticipantGlobalData.getInstance().isAllAnswered(curr_question + 1)) { // 현재 페이지에서 모든 대답을 하지 않은 경우
                Toast toast = Toast.makeText(this, "모든 문항을 수행해주십시오", Toast.LENGTH_SHORT);
                toast.show();
            }
            else if (curr_question + 1 >= ParticipantGlobalData.getInstance().getQuestionsLength() // 마지막 까지 모든 대답을 함
                    && ParticipantGlobalData.getInstance().answers.length() == ParticipantGlobalData.getInstance().getAnsweredLength())
                    new AlertDialog.Builder(SurveyTempActivity.this)
                            .setTitle("안내")
                            .setMessage("마지막 문항입니다.\n이대로 제출 하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent intent = new Intent(getApplicationContext(), FinishActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("취소", null)
                            .create()
                            .show();
            else { // 현재 페이지에서 모든 대답을 한 경우
                curr_question++;
                prev.setEnabled(true);
                printCurrentQuestion();
            }
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("안내")
                .setMessage("지금 종료하면 결과가 저장되지 않습니다. 그래도 테스트를 종료하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(SurveyTempActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("취소", null)
                .create()
                .show();
    }

    private void printCurrentQuestion() {
        number_view.setText((curr_question+1)+"."); // 문항 세팅
        question_view.setText(q_list[curr_question].question);
//        survey_scroll.fullScroll(ScrollView.FOCUS_UP); // 애니메이션 스크롤 업
        survey_scroll.scrollTo(0, 0); // 직관적 스크롤 업
        number_view.setVisibility(View.VISIBLE);
        // 보기 or 서브 문항 세팅
        blindItems();
        if (types[curr_question] == 1) makeBaseItem(curr_question);
        if (types[curr_question] == 2) makeTableItem(curr_question);
        if (types[curr_question] == 3) makeSubItem(curr_question);
        if (types[curr_question] == 4) makeIATItem(curr_question);
        if (types[curr_question] == 5) makeAssayItem(curr_question);
    }

    public class JSONSurveyTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("GET");
                    con.setRequestProperty("Cache-Control", "no-cache"); // cache 설정
                    con.setRequestProperty("Accept", "text/html"); // 서버로부터 response 데이터를 html로 받음
                    con.setDoInput(true); // InputStream 으로 서버로부터 응답을 받음
                    con.connect();

                    // 서버로부터 설문 문항 수신
                    InputStream stream = con.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder builder = new StringBuilder();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    JSONObject object = new JSONObject(builder.toString()); // Adapter로 수신한 설문 문항 전달
                    ParticipantGlobalData.getInstance().setQuestions(object.getJSONArray("data"));

                    JSONArray temp_arr = ParticipantGlobalData.getInstance().questions;

                    types = new int[temp_arr.length()];
                    q_list = new Question[temp_arr.length()];

                    // data forming
                    for (int i = 0; i < temp_arr.length(); i++) {
                        types[i] = temp_arr.getJSONObject(i).getInt("type");
                        if (types[i] == 1) makeBaseQuestion(i, temp_arr);
                        if (types[i] == 2) makeTableQuestion(i, temp_arr);
                        if (types[i] == 3) makeSubQuestion(i, temp_arr);
                        if (types[i] == 4) makeIATQuestion(i, temp_arr);
                        if (types[i] == 5) makeAssayQuestion(i, temp_arr);
                    }

                    ParticipantGlobalData.getInstance().setFidelity(static_fidelity);

                    return object.toString();
                }
                catch (Exception e) {
                    return null;
                }
                finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            if (res == null) {
                Toast toast = Toast.makeText(getApplicationContext(), "불러오기에 실패하였습니다\n데이터 연결이 안정적인지 확인해주세요", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }

            next.setEnabled(true);
            survey_progress.setMax(ParticipantGlobalData.getInstance().answers.length());
            survey_progress.setProgress(ParticipantGlobalData.getInstance().getAnsweredLength());
            modal_progress.setVisibility(View.GONE);
            survey_layout.setVisibility(View.VISIBLE);

            printCurrentQuestion();
        }
    }

    private void makeBaseQuestion(final int pos, JSONArray arr) throws Exception {
        if (ParticipantGlobalData.getInstance().getAnswer((pos+1)+"").equals(""))
            ParticipantGlobalData.getInstance().putAnswer((pos+1)+"", "");

        q_list[pos] = new BaseQuestion();
        q_list[pos].question = arr.getJSONObject(pos).getString("question");
        JSONArray cases_arr = arr.getJSONObject(pos).getJSONArray("cases");
        JSONArray assay_arr = arr.getJSONObject(pos).getJSONArray("is_assay");
        List<String> cases_list = new ArrayList<String>();
        List<Integer> assay_list = new ArrayList<Integer>();
        for (int j = 0; j < cases_arr.length(); j++) {
            cases_list.add(cases_arr.getString(j));
        }
        for (int j = 0; j < assay_arr.length(); j++) {
            assay_list.add(assay_arr.getInt(j));
        }
        ((BaseQuestion)q_list[pos]).cases = cases_list.toArray(new String[cases_list.size()]);
        ((BaseQuestion)q_list[pos]).is_assay_list = assay_list.toArray(new Integer[assay_list.size()]);
    }
    private void makeTableQuestion(final int pos, JSONArray arr) throws Exception {
        q_list[pos] = new TableQuestion();
        q_list[pos].question = arr.getJSONObject(pos).getString("question");
        JSONArray sub_arr = arr.getJSONObject(pos).getJSONArray("sub_contents");
        JSONArray cases_arr = arr.getJSONObject(pos).getJSONArray("cases");
        List<String> sub_list = new ArrayList<String>();
        List<String> cases_list = new ArrayList<String>();
        for (int j = 0; j < sub_arr.length(); j++) {
            if (ParticipantGlobalData.getInstance().getAnswer((pos+1)+"_"+(j+1)).equals(""))
                ParticipantGlobalData.getInstance().putAnswer((pos+1)+"_"+(j+1), "");

            sub_list.add(sub_arr.getString(j));
        }
        for (int j = 0; j < cases_arr.length(); j++) {
            cases_list.add(cases_arr.getString(j));
        }
        ((TableQuestion)q_list[pos]).sub_contents = sub_list.toArray(new String[sub_list.size()]);
        ((TableQuestion)q_list[pos]).cases = cases_list.toArray(new String[cases_list.size()]);
    }
    private void makeSubQuestion(final int pos, JSONArray arr) throws Exception {
        q_list[pos] = new SubQuestion();
        q_list[pos].question = arr.getJSONObject(pos).getString("question");
        JSONArray sub_content_arr = arr.getJSONObject(pos).getJSONArray("sub_contents");
        String[] temp = new String[sub_content_arr.length()];
        String[][] cases_temp = new String[sub_content_arr.length()][];
        for (int i = 0; i < sub_content_arr.length(); i++) {
            if (ParticipantGlobalData.getInstance().getAnswer((pos+1)+"_"+(i+1)).equals(""))
                ParticipantGlobalData.getInstance().putAnswer((pos+1)+"_"+(i+1), "");

            temp[i] = sub_content_arr.getJSONObject(i).getString("content");
            JSONArray sub_case_arr = sub_content_arr.getJSONObject(i).getJSONArray("cases");
            List<String> sub_cases_list = new ArrayList<String>();
            for (int j = 0; j < sub_case_arr.length(); j++) {
                sub_cases_list.add(sub_case_arr.getString(j));
            }
            cases_temp[i] = sub_cases_list.toArray(new String[sub_cases_list.size()]);
        }
        ((SubQuestion)q_list[pos]).sub_contents = temp;
        ((SubQuestion)q_list[pos]).sub_cases = cases_temp;
    }
    private void makeIATQuestion(final int pos, JSONArray arr) throws Exception {
        q_list[pos] = new IATQuestion();
        q_list[pos].question = arr.getJSONObject(pos).getString("question");
        JSONArray sub_content_arr = arr.getJSONObject(pos).getJSONArray("sub_contents");
        String[] temp = new String[sub_content_arr.length()];
        JSONArray[] word_temp = new JSONArray[sub_content_arr.length()];
        JSONArray[] step_temp = new JSONArray[sub_content_arr.length()];
        for (int i = 0; i < sub_content_arr.length(); i++) {
            if (ParticipantGlobalData.getInstance().getAnswer((pos+1)+"_"+(i+1)).equals(""))
                ParticipantGlobalData.getInstance().putAnswer((pos+1)+"_"+(i+1), "");
            if (ParticipantGlobalData.getInstance().getAnswer((pos+1)+"_"+(i+1)+"_D").equals(""))
                ParticipantGlobalData.getInstance().putAnswer((pos+1)+"_"+(i+1)+"_D", "");

            temp[i] = sub_content_arr.getJSONObject(i).getString("content");
            word_temp[i] = sub_content_arr.getJSONObject(i).getJSONArray("words");
            step_temp[i] = sub_content_arr.getJSONObject(i).getJSONArray("steps");

            ArrayList<WordData> word_list = new ArrayList<>();
            ArrayList<StepData> step_list = new ArrayList<>();
            for (int j = 0; j < word_temp[i].length(); j++) {
                JSONObject word = word_temp[i].getJSONObject(j);
                JSONArray word_arr = word.getJSONArray("words");
                List<String> list = new ArrayList<>();
                for (int k = 0; k < word_arr.length(); k++) list.add(word_arr.getString(k));
                String[] words = list.toArray(new String[list.size()]);
                word_list.add(new WordData(word.getString("subject"), words));
            }
            for (int j = 0; j < step_temp[i].length(); j++) {
                JSONObject step = step_temp[i].getJSONObject(j);
                JSONArray title_arr = step.getJSONArray("title");
                JSONArray left_arr = step.getJSONArray("left");
                JSONArray right_arr = step.getJSONArray("right");
                List<String> list = new ArrayList<>();
                for (int k = 0; k < title_arr.length(); k++) list.add(title_arr.getString(k));
                String[] title = list.toArray(new String[list.size()]);
                list.clear();
                for (int k = 0; k < left_arr.length(); k++) list.add(left_arr.getString(k));
                String[] left = list.toArray(new String[list.size()]);
                list.clear();
                for (int k = 0; k < right_arr.length(); k++) list.add(right_arr.getString(k));
                String[] right = list.toArray(new String[list.size()]);
                step_list.add(new StepData(title, step.getInt("trial"), left, right));
            }
            ParticipantGlobalData.getInstance().word_list.add(word_list);
            ParticipantGlobalData.getInstance().step_list.add(step_list);

            static_fidelity += step_temp[i].length();
        }
    }
    private void makeAssayQuestion(final int pos, JSONArray arr) throws Exception {
        if (ParticipantGlobalData.getInstance().getAnswer((pos+1)+"").equals(""))
            ParticipantGlobalData.getInstance().putAnswer((pos+1)+"", "");

        q_list[pos] = new AssayQuestion();
        q_list[pos].question = arr.getJSONObject(pos).getString("question");
    }
    private void blindItems() {
        survey_scroll.setVisibility(View.GONE);
        base_case_view.setVisibility(View.GONE);
        table_view.setVisibility(View.GONE);
        table_title_view.setVisibility(View.GONE);
        sub_view.setVisibility(View.GONE);
        single_view.setVisibility(View.GONE);
        iat_view.setVisibility(View.GONE);
        assay_edit.setVisibility(View.GONE);
    }
    private void makeBaseItem(final int pos) {
        survey_scroll.setVisibility(View.VISIBLE);
        base_case_view.setVisibility(View.VISIBLE);
        BaseQuestion bq = (BaseQuestion) q_list[pos];

        CustomCaseItemAdapter adapter = new CustomCaseItemAdapter(this, R.layout.custom_case_item,
                ParticipantGlobalData.getInstance().getAnswer((pos+1)+""), (pos+1)+"", bq.cases, bq.is_assay_list);
        base_case_view.setAdapter(adapter);
    }
    private void makeTableItem(final int pos) {
        survey_scroll.setVisibility(View.VISIBLE);
        table_view.setVisibility(View.VISIBLE);
        table_title_view.setVisibility(View.VISIBLE);
        TableQuestion tq = (TableQuestion) q_list[pos];

        first_row.removeAllViews();
        table_view.removeAllViews();
        table_title_view.removeAllViews();

        first_row.addView(first_cell);
        CustomTableCaseAdapter case_adapter = new CustomTableCaseAdapter(this, this, R.layout.custom_table_case_item,
                "", (pos+1)+"", tq.cases);
        first_row.setAdapter(case_adapter);
        for (int i = 1; i < first_row.getChildCount(); i++) {
            first_row.getChildAt(i).setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        }
        table_title_view.addView(first_row);
        table_title_view.addView(row_line);

        CustomSubTableAdapter adapter = new CustomSubTableAdapter(this, this, R.layout.custom_sub_item,
                (pos+1)+"", tq.sub_contents, tq.cases);
        table_view.setAdapter(adapter);
    }
    private void makeSubItem(final int pos) {
        survey_scroll.setVisibility(View.VISIBLE);
        sub_view.setVisibility(View.VISIBLE);
        SubQuestion sq = (SubQuestion) q_list[pos];

        CustomSubItemAdapter adapter = new CustomSubItemAdapter(this, R.layout.custom_sub_item,
                (pos+1)+"", sq.sub_contents, sq.sub_cases);
        sub_view.setAdapter(adapter);
    }
    private void makeIATItem(final int pos) {
        single_view.setVisibility(View.VISIBLE);
        iat_view.setVisibility(View.VISIBLE);

        if (ParticipantGlobalData.getInstance().isDoneTest()) {
            iat_view.setText("완료됨");
            iat_view.setBackgroundColor(Color.rgb(0xAA, 0xAA, 0xAA));
            iat_view.setEnabled(false);
        }
    }
    private void makeAssayItem(final int pos) {
        assay_edit.setVisibility(View.VISIBLE);

        assay_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    ParticipantGlobalData.getInstance().putAnswer((pos+1)+"", textView.getText().toString());
                    ParticipantGlobalData.getInstance().setRootProgress();
                    return false;
                }

                return true;
            }
        });
        String ans = ParticipantGlobalData.getInstance().getAnswer((pos+1)+"");
        if (!ans.equals(""))
            assay_edit.setText(ans);
    }
}