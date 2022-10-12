package com.kumoh.iat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

public class __TestActivity extends AppCompatActivity {

    ScrollView scroll_view;
    TextView text;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        scroll_view = (ScrollView) findViewById(R.id.scroll_view);
        text = (TextView) findViewById(R.id.text);
        progress = (ProgressBar) findViewById(R.id.progress);

//        ParticipantGlobalData.getInstance().setFidelity(10);
//        boolean[] b = {false,false,true,false,true,false,false,true,false,true};
//        for (int i = 0; i < 10; i++) {
//            if (!b[i]) {
//                ParticipantGlobalData.getInstance().fidelity--;
//            }
//        }
//        text.setText("fidelity : " + ParticipantGlobalData.getInstance().fidelity + "\nstatic : " + ParticipantGlobalData.getInstance().static_fidelity
//                    + "\nisFidelity : " + ParticipantGlobalData.getInstance().isFidelity());
//        progress.setVisibility(View.GONE);
//        scroll_view.setVisibility(View.VISIBLE);

        new TempRewordTask().execute("https://bizapi.giftishow.com/bizApi/send");
//        new TempMeetingTask().execute("http://101.101.208.212:8080/participant/upmeet");
    }

    // 기프티쇼 통신용 내장 객체
    class TempRewordTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                String object = "api_code=0204";
                object += "&custom_auth_code=REAL920d59124cb043ffa32693f5d7b31387";
                object += "&custom_auth_token=uxT8SmXFQnT5ZiuXz7XpdQ==";
                object += "&dev_yn=N";
                object += "&goods_code=G00000008077"; // [스타벅스] 아이스 카페아메리카노 Tall 쿠폰 코드
                object += "&mms_msg=본+연구는+학습자들의+학업수행경험을+확인하고+적절한+조력방안을+모색하기+위한+연구입니다"; // MMS 함께 보낼 메시지
                object += "&mms_title=학업수행경험에+관한+연구"; // MMS 제목
                object += "&callback_no=0544787875"; // 표기할 발신 번호('-'제외)
                object += "&phone_no="; // 표기할 수신 번호('-'제외)
                object += "&tr_id=" + UUID.randomUUID().toString().substring(0, 25); // 결제내역의 유니크한 식별자(uuid로 생성 고려)
                object += "&user_id=pecja1@kumoh.ac.kr"; // 회원 ID

                HttpsURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    con = (HttpsURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.setRequestProperty("Cache-Control", "no-cache"); // cache 설정
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // JSON 형식으로 전송
                    con.setRequestProperty("Accept", "application/json"); // 서버로부터 response 데이터를 json으로 받음
                    con.setDoOutput(true); // OutStream 으로 post 데이터를 넘겨줌
                    con.setDoInput(true); // InputStream 으로 서버로부터 응답을 받음
                    con.connect();

                    // 버퍼를 생성하고 넣음
                    OutputStream outStream = con.getOutputStream();
                    outStream.write(object.getBytes("UTF-8"));
                    outStream.flush();
                    outStream.close();

                    // 서버로부터 설문 문항 수신
                    InputStream stream;
                    StringBuilder builder = new StringBuilder();
                    int status = con.getResponseCode();

                    if (status != HttpURLConnection.HTTP_OK)  {
                        stream = con.getErrorStream();
                        reader = new BufferedReader(new InputStreamReader(stream));
                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }

                        return builder.toString();
                    }
                    else  {
                        stream = con.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(stream));
                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }

                        JSONObject rcv_object = new JSONObject(builder.toString());

                        return rcv_object.getString("code") + ", " + rcv_object.getString("message");
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
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
            } catch (Exception e) {
                Log.e("Test", "fail2");
                return "";
            }
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            if (res.equals(""))
                text.setText("에러");
            else
                text.setText(res);
            progress.setVisibility(View.GONE);
            scroll_view.setVisibility(View.VISIBLE);
        }
    }

    // 면담 연구 의향 저장용 내장 객체
    class TempMeetingTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject object = new JSONObject();
                object.put("phone_num", "01051859323");
                object.put("meeting", true);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.setRequestProperty("Cache-Control", "no-cache");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "text/html");
                    con.setDoOutput(true);
                    con.setDoInput(true);
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
                    e.printStackTrace();
                    return "";
                }
                finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            if (res.equals(""))
                text.setText("에러");
            else
                text.setText(res);
            progress.setVisibility(View.GONE);
            scroll_view.setVisibility(View.VISIBLE);
        }
    }

    // IAT Question 결과 저장
    public class JSONSaveDataTask extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... strs) {
            ArrayList<TestData> dataList = new ArrayList<>();
            int[] r1 = {2556,711,710,727,712,790,708,760,725,747,720,781,712,703,693,700,685,711,744,692},
                    w1 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            dataList.add(new TestData(0, r1, w1));
            int[] r2 = {867,757,724,788,736,726,744,734,672,724,688,737,710,784,769,801,747,690,763,801},
                    w2 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            dataList.add(new TestData(1, r2, w2));
            int[] r3 = {753,662,793,679,705,713,657,644,600,636,747,681,717,761,668,577,714,647,617,680,694,689,831,738,651,746,719,671,636,663,745,678,933,594,593,692,728,719,732,684},
                    w3 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            dataList.add(new TestData(2, r3, w3));
            int[] r4 = {683,619,627,673,832,573,599,616,710,676,620,628,632,574,669,686,663,600,664,635},
                    w4 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            dataList.add(new TestData(3, r4, w4));
            int[] r5 = {693,595,648,571,626,618,712,577,579,569,624,589,637,608,556,690,620,644,623,697,300,697,693,567,665,765,592,569,632,560,593,644,579,618,600,602,622,565,649,644},
                    w5 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            dataList.add(new TestData(4, r5, w5));

            try {
                double z_avr_block_3 = 0, z_avr_block_5 = 0;
                String str = "";
                boolean[] isWrong = new boolean[dataList.size()]; // 블록마다 삭제 처리할지 등록

                int count = 0, sum = 0;
                // 출력되는 반응시간만 count
                // 평균 avr, 분산 dev
                for (int index = 0; index < dataList.size(); index++) {
                    int[] timeList = dataList.get(index).getReactionTime();
                    int[] wrongList = dataList.get(index).getWrongList();

                    int wrong_sum = 0;
                    for (int i = 0; i < wrongList.length; i++) // 오답 개수 sum
                        if (wrongList[i] > 0) wrong_sum++;
                    if (((double)wrong_sum / (double)(wrongList.length)) < 0.1) { // 해당 블록 오답률이 10%미만이라면
                        for (int i = 0; i < timeList.length; i++)
                            if (wrongList[i] == 0) { // 오답이 아닌경우만 체크
                                sum += timeList[i];
                                count++;
                            }
                        isWrong[index] = false;
                    } else {
                        isWrong[index] = true;
                    }
                }
                double avr = (double)sum / (double)count; sum = 0;

                for (int index = 0; index < dataList.size(); index++) {
                    int[] timeList = dataList.get(index).getReactionTime();
                    int[] wrongList = dataList.get(index).getWrongList();

                    if (!isWrong[index])
                        for (int i = 0; i < timeList.length; i++)
                            if (wrongList[i] == 0) {
                                sum += (timeList[i] - avr) * (timeList[i] - avr);
                            }
                }
                double dev = (double)sum / (double)(count - 1); // 표본분산

                for (int index = 0; index < dataList.size(); index++) {
                    int block = dataList.get(index).getBlock();
                    int[] timeList = dataList.get(index).getReactionTime();
                    int[] wrongList = dataList.get(index).getWrongList();

                    // 오답률이 10% 이상이면 모든 반응과 Z점수 삭제 및 충실성 false
                    if (isWrong[index]) {
                        for (int i = 0; i < timeList.length; i++)
                            str += block + "_" + (i+1) + ",.,.\n";
                        ParticipantGlobalData.getInstance().fidelity--;
                    }
                    else {
                        // 반응 시간 전처리
                        for (int i = 0; i < timeList.length; i++) { // 극단 치 재코딩
                            if (timeList[i] < 300)
                                timeList[i] = 300;
                            if (timeList[i] > 3000)
                                timeList[i] = 3000;
                        }

                        // 데이터 쓰기 및 z 점수 평균 도출
                        double z_total = 0;
                        int count_per_block = 0;
                        for (int i = 0; i < timeList.length; i++) {
                            double z = (timeList[i] - avr) / Math.sqrt(dev); // z 점수
                            if (wrongList[i] > 0) {
                                str += block + "_" + (i+1) + ",.,.";
                            }
                            else {
                                str += block + "_" + (i+1) + "," + timeList[i] + "," + z;
                                z_total += z;
                                count_per_block++;
                            }
                            str += "\n";
                        }
                        if (block == 3)
                            z_avr_block_3 = z_total / count_per_block;
                        else if (block == 5)
                            z_avr_block_5 = z_total / count_per_block;
                    }
                }

                Log.e("평균 : ", avr+"");
                Log.e("표준분산 : ", dev+"");
                Log.e("카운트 : ", (count-1)+"");
                Log.e("표준편차 : ", "");
                Log.e("z점수 : ", "");
                Log.e("d점수 : ", "");

                return str;
            } catch (Exception e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
        }
    }
}