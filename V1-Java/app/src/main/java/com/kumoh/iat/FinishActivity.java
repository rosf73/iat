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
import android.widget.ProgressBar;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

public class FinishActivity extends AppCompatActivity {

    private ProgressBar progress;
    private TextView text;
    private Button exit, retry;

    long first_time = 0, second_time = 0;
    int day = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        progress = (ProgressBar)findViewById(R.id.progress);
        text = (TextView)findViewById(R.id.text);
        exit = (Button)findViewById(R.id.exit);
        retry = (Button)findViewById(R.id.retry);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DataSaver().execute("http://101.101.208.212:8080/answer/upload");
            }
        });

        new DataSaver().execute("http://101.101.208.212:8080/answer/upload");
    }
    @Override
    public void onBackPressed() {
        second_time = System.currentTimeMillis();
        Toast.makeText(FinishActivity.this, "한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
        if(second_time - first_time < 2000){
            super.onBackPressed();
            finishAffinity();
        }
        first_time = System.currentTimeMillis();
    }

    // 테스트 결과 데이터 전송
    class DataSaver extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            progress.setVisibility(View.VISIBLE);
            retry.setVisibility(View.GONE);
            try {
                String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                JSONObject object = new JSONObject();
                object.put("phone_num", ParticipantGlobalData.getInstance().phone_num);
                object.put("day", today);
                object.put("answer_data", ParticipantGlobalData.getInstance().answers);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
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
                        e.printStackTrace();
                        return "";
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            if (res.equals("")) { // 오류 시 처리
                progress.setVisibility(View.GONE);
                retry.setVisibility(View.VISIBLE);
                text.setText("데이터 연결이 안정적인지 다시 확인해주시기 바랍니다.\n재전송 시도는 아래 버튼을 눌러주세요.");
                return;
            }

            if (res.substring(0, 7).equals("SUCCESS")) {
                day = Integer.parseInt(res.substring(7));
                if (!ParticipantGlobalData.getInstance().isFidelity()) {
                    progress.setVisibility(View.GONE);
                    exit.setVisibility(View.VISIBLE);
                    text.setText("테스트 과정에 성실하게 임하지 않으셨습니다\n\n재참여 시 진지하게 임하지 않으면\n보상이 제한될 수 있음을 다시 한 번 알려드립니다");

                    ParticipantGlobalData.getInstance().clear();
                } else if (ParticipantGlobalData.getInstance().giftable == 1) {
                    if (day > 1) {
                        progress.setVisibility(View.GONE);
                        exit.setVisibility(View.VISIBLE);
                        text.setText("다시 한 번 참여해주셔서 감사합니다!\n기타 문의는 약관을 참고해주세요.");

                        ParticipantGlobalData.getInstance().clear();
                    } else {
                        text.setText("기프티콘 처리 중...");
                        new RewordTask().execute("https://bizapi.giftishow.com/bizApi/send");
                    }
                } else {
                    if (day > 1) {
                        progress.setVisibility(View.GONE);
                        exit.setVisibility(View.VISIBLE);
                        text.setText("다시 한 번 참여해주셔서 감사합니다!\n기타 문의는 약관을 참고해주세요.");

                        ParticipantGlobalData.getInstance().clear();
                    } else
                        new AlertDialog.Builder(FinishActivity.this)
                                .setTitle("안내")
                                .setMessage("향후 추가 면담에 참여할 생각이 있으십니까? 면담 연구의 경우, 참여하시는 분께 사례비로 5만원을 지급해 드립니다.")
                                .setPositiveButton("승낙", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        text.setText("참여해주셔서 감사드립니다!\n\n면담 연구에 대해서는 차후에 연락이 갈 것입니다.\n기타 문의는 약관을 참고해주세요.");
                                        new MeetingTask().execute("http://101.101.208.212:8080/participant/upmeet");
                                    }
                                })
                                .setNegativeButton("거절", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        progress.setVisibility(View.GONE);
                                        exit.setVisibility(View.VISIBLE);
                                        text.setText("참여해주셔서 감사드립니다!\n기타 문의는 약관을 참고해주세요.");

                                        ParticipantGlobalData.getInstance().clear();
                                    }
                                })
                                .create()
                                .show();
                }
            } else {
                progress.setVisibility(View.GONE);
                retry.setVisibility(View.VISIBLE);
                text.setText("데이터 연결이 안정적인지 다시 확인해주시기 바랍니다.\n재전송 시도는 아래 버튼을 눌러주세요.");
            }
        }
    }

    // 기프티쇼 통신용 내장 객체
    class RewordTask extends AsyncTask<String, String, String> {
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
                object += "&phone_no=" + ParticipantGlobalData.getInstance().phone_num; // 표기할 수신 번호('-'제외)
                object += "&tr_id=" + UUID.randomUUID().toString().substring(0, 25); // 결제내역의 유니크한 식별자(uuid로 생성)
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

            if (res.equals("")) { // 오류 시 처리
                progress.setVisibility(View.GONE);
                retry.setVisibility(View.VISIBLE);
                text.setText("데이터 연결이 안정적인지 다시 확인해주시기 바랍니다.\n재전송 시도는 아래 버튼을 눌러주세요.");
                return;
            }

            if ((res.split(","))[0].equals("E0010")) { // Biz money balance is insufficient
                progress.setVisibility(View.GONE);
                exit.setVisibility(View.VISIBLE);
                text.setText("기프티콘을 받을 수 없는 상태입니다.\n약관에 기재된 번호로 문의해주시기 바랍니다.");
                ParticipantGlobalData.getInstance().clear();
            } else if ((res.split(","))[0].equals("ERR0817")) { // 전화번호 전송 데이터 오류
                progress.setVisibility(View.GONE);
                exit.setVisibility(View.VISIBLE);
                text.setText("전화번호 입력이 잘못되었습니다.\n약관에 기재된 번호로 문의해주시기 바랍니다.");
                ParticipantGlobalData.getInstance().clear();
            } else if ((res.split(","))[0].equals("ERR0215")) { // TRID is duplicated. Resending.
                new RewordTask().execute("https://bizapi.giftishow.com/bizApi/send");
            } else if ((res.split(","))[0].equals("ERR0201")) { // Required value is missing
                progress.setVisibility(View.GONE);
                exit.setVisibility(View.VISIBLE);
                text.setText("기프티콘 발송 요청용 데이터에 문제가 있습니다.\n약관에 기재된 번호로 문의해주시기 바랍니다.");
                ParticipantGlobalData.getInstance().clear();
            } else if (!(res.split(","))[0].equals("0000")) {
                progress.setVisibility(View.GONE);
                exit.setVisibility(View.VISIBLE);
                text.setText("기프티콘 발송 상태가 원활하지 않습니다.\n약관에 기재된 번호로\n아래 오류코드와 함께 문의해주시기 바랍니다.\n\n"+res);
                ParticipantGlobalData.getInstance().clear();
            } else
                new AlertDialog.Builder(FinishActivity.this)
                    .setTitle("안내")
                        .setMessage("향후 추가 면담에 참여할 생각이 있으십니까? 면담 연구의 경우, 참여하시는 분께 사례비로 5만원을 지급해 드립니다.")
                        .setPositiveButton("승낙", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                text.setText("참여해주셔서 감사드립니다!\n기프티콘은 입력해주신 전화번호로 발송됩니다.\n\n면담 연구에 대해서는 차후에 연락이 갈 것입니다.\n기타 문의는 약관을 참고해주세요.");
                                new MeetingTask().execute("http://101.101.208.212:8080/participant/upmeet");
                            }
                        })
                        .setNegativeButton("거절", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                progress.setVisibility(View.GONE);
                                exit.setVisibility(View.VISIBLE);
                                text.setText("참여해주셔서 감사드립니다!\n기프티콘은 입력해주신 전화번호로 발송됩니다.\n기타 문의는 약관을 참고해주세요.");

                                ParticipantGlobalData.getInstance().clear();
                            }
                        })
                        .create()
                        .show();
        }
    }

    // 면담 연구 의향 저장용 내장 객체
    class MeetingTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject object = new JSONObject();
                object.put("phone_num", ParticipantGlobalData.getInstance().phone_num);
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

            progress.setVisibility(View.GONE);
            exit.setVisibility(View.VISIBLE);

            if (res.equals("")) {
                text.setText("현재 서버 문제로 인하여\n면담 참가 등록이 정상적으로 완료되지 않았습니다.\n약관에 기재된 번호로 문의해주시면 감사드리겠습니다.");
            }

            ParticipantGlobalData.getInstance().clear();
        }
    }
}