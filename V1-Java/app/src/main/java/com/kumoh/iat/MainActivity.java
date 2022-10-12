package com.kumoh.iat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView hello1, hello2, gift;
    private Button agreement_button;

    String project_name, description, agreement;
    long first_time = 0, second_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hello1 = (TextView) findViewById(R.id.hello1);
        hello2 = (TextView) findViewById(R.id.hello2);
        gift = (TextView) findViewById(R.id.gift);
        agreement_button = (Button) findViewById(R.id.agreement_button);

        agreement_button.setOnClickListener(this);

        new JSONMainTask().execute("http://101.101.208.212:8080/information/all");
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), __TestActivity.class);
//        Intent intent = new Intent(getApplicationContext(), AgreementActivity.class);
//        intent.putExtra("project_name", project_name);
//        intent.putExtra("description", description);
//        intent.putExtra("agreement", agreement);
//
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        second_time = System.currentTimeMillis();
        Toast.makeText(MainActivity.this, "한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
        if(second_time - first_time < 2000){
            super.onBackPressed();
            finishAffinity();
        }
        first_time = System.currentTimeMillis();
    }

    private class JSONMainTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            hello1.setVisibility(View.GONE);
            hello2.setVisibility(View.GONE);
            gift.setVisibility(View.GONE);
            agreement_button.setVisibility(View.GONE);

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

                    JSONObject object = new JSONObject(builder.toString()).getJSONObject("data");
                    ParticipantGlobalData.getInstance().giftable = object.getInt("giftable");
                    if (object.getInt("giftable") == 1)
                        gift.setText("참여가 끝나면\n스타벅스(아메리카노) 기프티콘이 제공됩니다\n(전화번호 당 최초 1회 참여시에만 제공)");
                    else
                        gift.setText("기프티콘 보상이벤트가 종료되었습니다\n보상 없이 참여하실 분들만\n아래 약관 읽기를 눌러주세요");
                    project_name = object.getString("project_name");
                    description = object.getString("description");
                    agreement = object.getString("agreement");

                    return project_name;
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
                    }
                    catch (Exception e) {
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

            if (!res.equals("")) {
                hello1.setVisibility(View.VISIBLE);
                hello2.setVisibility(View.VISIBLE);
                gift.setVisibility(View.VISIBLE);
                agreement_button.setVisibility(View.VISIBLE);
            }
            else
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("안내")
                        .setMessage("서버 연결이 안정적이지 않습니다.\n재연결을 시도할까요?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                new JSONMainTask().execute("http://101.101.208.212:8080/information/all");
                            }
                        })
                        .setNegativeButton("취소", null)
                        .create()
                        .show();
        }
    }
}
