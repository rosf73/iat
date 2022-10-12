package com.kumoh.iat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private LinearLayout first_layout, second_layout, major_layout;
    private EditText phone_num, age;
    private Spinner grade_spinner, school_year_spinner, major_spinner;
    private Button man_button, woman_button, next_button;

    boolean is_checked = false;
    String gender = "", major = "-1";
    int grade = 0;
    long first_time = 0, second_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ParticipantGlobalData.getInstance().clear();

        first_layout = (LinearLayout) findViewById(R.id.first_layout);
        second_layout = (LinearLayout) findViewById(R.id.second_layout);
        major_layout = (LinearLayout) findViewById(R.id.major_layout);
        phone_num = (EditText) findViewById(R.id.phone_num);
        age = (EditText) findViewById(R.id.age);
        grade_spinner = (Spinner) findViewById(R.id.grade_spin);
        school_year_spinner = (Spinner) findViewById(R.id.school_year_spin);
        major_spinner = (Spinner) findViewById(R.id.major_spin);
        man_button = (Button) findViewById(R.id.man_button);
        woman_button = (Button) findViewById(R.id.woman_button);
        next_button = (Button) findViewById(R.id.next_button);

        grade_spinner.setOnItemSelectedListener(this);
        school_year_spinner.setOnItemSelectedListener(this);
        major_spinner.setOnItemSelectedListener(this);

        man_button.setOnClickListener(this);
        woman_button.setOnClickListener(this);
        next_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (!is_checked) { // 전화번호 입력 화면
            String reqExp = "^(010|011)[0-9]{7,8}$";
            if (phone_num.getText().toString() == null || phone_num.getText().toString().equals("")) {
                new AlertDialog.Builder(this)
                        .setTitle("안내")
                        .setMessage("전화번호를 입력해주세요")
                        .setPositiveButton("확인", null)
                        .create()
                        .show();
            }
            else if (!phone_num.getText().toString().matches(reqExp))
                new AlertDialog.Builder(this)
                        .setTitle("안내")
                        .setMessage("전화번호를 정확히 입력해주세요")
                        .setPositiveButton("확인", null)
                        .create()
                        .show();
            else {
                ParticipantGlobalData.getInstance().setPhoneNum(phone_num.getText().toString());
                new JSONLoginTask().execute("http://101.101.208.212:8080/participant/", "signin");
            }
        }
        else { // 정보 입력 화면
            if (view.getId() == man_button.getId()) { //.parseColor("#CC5000")
                man_button.setBackgroundColor(Color.rgb(0xCC, 0x50, 0x00));
                woman_button.setBackgroundColor(Color.rgb(0xDD, 0xDD, 0xDD));
                gender = "남";
            }
            else if (view.getId() == woman_button.getId()) {
                man_button.setBackgroundColor(Color.rgb(0xDD, 0xDD, 0xDD));
                woman_button.setBackgroundColor(Color.rgb(0xCC, 0x50, 0x00));
                gender = "여";
            }
            else {
                if (age.getText().toString() == null || age.getText().toString().equals("")
                    || gender.equals("")
                    || grade == 0) {
                    new AlertDialog.Builder(this)
                            .setTitle("안내")
                            .setMessage("모든 항목을 입력해주세요")
                            .setPositiveButton("확인", null)
                            .create()
                            .show();
                }
                else if (grade == 3 && major.equals("-1")) {
                    new AlertDialog.Builder(this)
                            .setTitle("안내")
                            .setMessage("대학생은 전공을 입력해주세요")
                            .setPositiveButton("확인", null)
                            .create()
                            .show();
                }
                else {
                    new JSONLoginTask().execute("http://101.101.208.212:8080/participant/", "signup");
                }
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == grade_spinner.getId()) {
            ArrayAdapter<CharSequence> adapter = null;
            if (i == 0) {
                school_year_spinner.setEnabled(true);
                major = "-1";
                major_layout.setVisibility(View.GONE);
                adapter = ArrayAdapter.createFromResource(this,
                        R.array.middle_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                grade = 10;
            }
            else if (i == 1) {
                school_year_spinner.setEnabled(true);
                major = "-1";
                major_layout.setVisibility(View.GONE);
                adapter = ArrayAdapter.createFromResource(this,
                        R.array.high_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                grade = 20;
            }
            else if (i == 2) {
                school_year_spinner.setEnabled(true);
                major = "1";
                major_layout.setVisibility(View.VISIBLE);
                adapter = ArrayAdapter.createFromResource(this,
                        R.array.univ_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                grade = 30;
            }
            else {
                school_year_spinner.setEnabled(false);
                major = "-1";
                major_layout.setVisibility(View.GONE);
                grade = 40;
            }
            school_year_spinner.setAdapter(adapter);
        }
        else if (adapterView.getId() == school_year_spinner.getId()) {
            if (i == 0) grade = (grade/10)*10 + 1;
            else if (i == 1) {
                if (grade >= 20 && grade < 30) {
                    major = "1";
                    major_layout.setVisibility(View.VISIBLE);
                }
                grade = (grade/10)*10 + 2;
            }
            else if (i == 2) {
                if (grade >= 20 && grade < 30) {
                    major = "1";
                    major_layout.setVisibility(View.VISIBLE);
                }
                grade = (grade/10)*10 + 3;
            }
            else if (i == 3) grade = (grade/10)*10 + 4;
            else if (i == 4) grade = (grade/10)*10 + 5;
            else grade = (grade/10)*10 + 6;
        }
        else if (adapterView.getId() == major_spinner.getId()) {
            if (i == 0) major = "1";
            else if (i == 1) major = "2";
            else major = "3";
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    @Override
    public void onBackPressed() {
        if (!is_checked) {
            second_time = System.currentTimeMillis();
            Toast.makeText(LoginActivity.this, "한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
            if(second_time - first_time < 2000){
                super.onBackPressed();
                finishAffinity();
            }
            first_time = System.currentTimeMillis();
        }
        else {
            is_checked = false;
            first_layout.setVisibility(View.VISIBLE);
            second_layout.setVisibility(View.GONE);
        }
    }

    public class JSONLoginTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject object = new JSONObject();
                object.put("phone_num", phone_num.getText().toString());
                if (urls[1].equals("signup")) {
                    object.put("age", Integer.parseInt(age.getText().toString()));
                    object.put("gender", gender.equals("남") ? 1 : 2);
                    object.put("grade", grade);
                    object.put("major", major);
                }

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]+urls[1]);
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.setRequestProperty("Cache-Control", "no-cache"); // cache 설정
                    con.setRequestProperty("Content-Type", "application/json"); // JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html"); // 서버로부터 response 데이터를 html로 받음
                    con.setDoOutput(true); // OutStream 으로 post 데이터를 넘겨줌
                    con.setDoInput(true); // InputStream 으로 서버로부터 응답을 받음
                    con.connect();

                    // 버퍼를 생성하고 넣음
                    OutputStream outStream = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(object.toString());
                    writer.flush();
                    writer.close();

                    // 서버로부터 데이터를 받음
                    InputStream stream = con.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();
                }
                catch (Exception e) {
                    return "";
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
                        return "";
                    }
                }
            }
            catch (Exception e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            if (res.equals("NO_EXIST")) {
                is_checked = true;
                first_layout.setVisibility(View.GONE);
                second_layout.setVisibility(View.VISIBLE);
            }
            else if (res.equals("EXIST")) {
                Intent intent = new Intent(getApplicationContext(), CheckingActivity.class);

                startActivity(intent);
            }
            else if (res.equals("SUCCESS_SIGNUP")) {
                Intent intent = new Intent(getApplicationContext(), CheckingActivity.class);

                startActivity(intent);
            }
            else {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("안내")
                        .setMessage("서버로부터 데이터를 수신하지못했습니다. 데이터 연결이 안정적인지 확인해주세요.")
                        .setPositiveButton("확인", null)
                        .create()
                        .show();
            }
        }
    }
}
