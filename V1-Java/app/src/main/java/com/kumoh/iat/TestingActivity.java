package com.kumoh.iat;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class TestingActivity extends AppCompatActivity implements View.OnClickListener {

    // 자극 단어 조합
    private String[][] word_list;
    // 단계별+주제별 자극 단어
    private String[][][] stimulus_list;
    // 단계별 자극 수
    private int[] trials;
    // 단계별+주제별 자극 단어 수
    private int[][] limited_trials = {
            {0, 10, 10},
            {5, 5, 10},
            {10, 10, 20},
            {5, 10, 5},
            {10, 20, 10}
    };
    // 단계별 주제
    private String[][] titles;

    // 이전 화면에선 직렬화 전송된 데이터 리스트 객체
    private String number;
    private ArrayList<StepData> step_list;
    // 다음 화면으로 직렬화 전송할 데이터 리스트 객체
    private ArrayList<TestData> dataList = new ArrayList<>();

    private int[] timeList; // 반응 시간 저장 배열
    private int[] wrongList; // 틀린 횟수 저장 배열
    private long first_time = 0, second_time = 0; // 반응 시간 체크 변수

    // 화면 트리거 변수
    private String w = "";
    private int block = 0, stimulus = -1, cnt_s, cnt_l, cnt_r, randStart, randEnd, randMStart, randMEnd;

    ScrollView scroll_view;
    ProgressBar save_progress;
    LinearLayout notice, button_layout;
    TextView leftTitle, rightTitle, info, word, firstWords, secondWords, finishText;
    ImageView fail;
    Button startButton, leftButton, rightButton, finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // 안드로이드 상단 바 제거
        setContentView(R.layout.activity_testing);

        scroll_view = (ScrollView)findViewById(R.id.scroll_view);
        save_progress = (ProgressBar)findViewById(R.id.save_progress);
        notice = (LinearLayout)findViewById(R.id.notice);
        button_layout = (LinearLayout)findViewById(R.id.test_button_layout);
        leftTitle = (TextView)findViewById(R.id.leftTitle);
        rightTitle = (TextView)findViewById(R.id.rightTitle);
        info = (TextView)findViewById(R.id.info);
        word = (TextView)findViewById(R.id.word);
        firstWords = (TextView)findViewById(R.id.firstWords);
        secondWords = (TextView)findViewById(R.id.secondWords);
        finishText = (TextView)findViewById(R.id.finish_text);
        fail = (ImageView)findViewById(R.id.fail);
        startButton = (Button)findViewById(R.id.test_start_button);
        leftButton = (Button)findViewById(R.id.leftButton);
        rightButton = (Button)findViewById(R.id.rightButton);
        finishButton = (Button)findViewById(R.id.finish_button);

        startButton.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);

        number = getIntent().getStringExtra("number");
        ArrayList<WordData> words = ParticipantGlobalData.getInstance().word_list.get(ParticipantGlobalData.getInstance().test_step);
        step_list = ParticipantGlobalData.getInstance().step_list.get(ParticipantGlobalData.getInstance().test_step);
        word_list = new String[step_list.size()][];
        stimulus_list = new String[step_list.size()][2][];
        trials = new int[step_list.size()];
        titles = new String[step_list.size()][2];
        for (int i = 0; i < step_list.size(); i++) {
            StepData cur_step = step_list.get(i);
            stimulus_list[i][0] = cur_step.getLeft();
            stimulus_list[i][1] = cur_step.getRight();
            trials[i] = cur_step.getTrial();
            titles[i][0] = cur_step.getTitle()[1].equals("") ? cur_step.getTitle()[0] : cur_step.getTitle()[0] + " or " + cur_step.getTitle()[1];
            titles[i][1] = cur_step.getTitle()[3].equals("") ? cur_step.getTitle()[2] : cur_step.getTitle()[2] + " or " + cur_step.getTitle()[3];
        }
        word_list[0] = new String[stimulus_list[0][0].length + stimulus_list[0][1].length];
        System.arraycopy(stimulus_list[0][0], 0, word_list[0], 0, stimulus_list[0][0].length);
        System.arraycopy(stimulus_list[0][1], 0, word_list[0], stimulus_list[0][0].length, stimulus_list[0][1].length);
        word_list[1] = new String[words.get(0).getWords().length + words.get(1).getWords().length + words.get(2).getWords().length];
        System.arraycopy(words.get(0).getWords(), 0, word_list[1], 0, words.get(0).getWords().length);
        System.arraycopy(words.get(1).getWords(), 0, word_list[1], words.get(0).getWords().length, words.get(1).getWords().length);
        System.arraycopy(words.get(2).getWords(), 0, word_list[1], words.get(0).getWords().length + words.get(1).getWords().length, words.get(2).getWords().length);

        setNotice();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("안내")
                .setMessage("지금 종료하면 검사 결과가 저장되지 않습니다. 그래도 검사를 종료하시겠습니까? (설문조사화면으로 돌아갑니다)")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(TestingActivity.this, SurveyTempActivity.class);
                        intent.putExtra("current", Integer.parseInt(number)-1);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("취소", null)
                .create()
                .show();
    }

    @Override
    public void onClick(View view) {
        if(stimulus+1 >= trials[block] && block+1 >= 5) { // 끝
            second_time = System.currentTimeMillis();
            if(stimulus != -1) timeList[stimulus] = (int)(second_time - first_time); // 5_40 반응시간 정보 저장

            TestData td = new TestData(block, timeList, wrongList);
            dataList.add(td);

            scroll_view.setVisibility(View.GONE);
            notice.setVisibility(View.GONE);
            info.setVisibility(View.GONE);
            word.setVisibility(View.GONE);
            fail.setVisibility(View.GONE);
            leftButton.setVisibility(View.GONE);
            rightButton.setVisibility(View.GONE);
            save_progress.setVisibility(View.VISIBLE);

            new JSONSaveDataTask().execute();
            return;
        }

        // 좌, 우 버튼은 틀렸을 경우만 체크
        if(view.getId() == leftButton.getId()) {
            if(!Arrays.asList(stimulus_list[block][0]).contains(w) && stimulus != -1) {
                fail.setVisibility(View.VISIBLE);
                wrongList[stimulus] += 1;
                return;
            }
        }
        else if(view.getId() == rightButton.getId()) {
            if(!Arrays.asList(stimulus_list[block][1]).contains(w) && stimulus != -1) {
                fail.setVisibility(View.VISIBLE);
                wrongList[stimulus] += 1;
                return;
            }
        }

        // 정답인 경우는 공통적으로 새로운 단어 출력 및 반응 결과 데이터 저장
        fail.setVisibility(View.INVISIBLE);
        w = setWord(); // 정답 또는 처음

        stimulus++;
        if(stimulus >= trials[block]) { // 다음 단계
            TestData td = new TestData(block, timeList, wrongList);
            dataList.add(td);

            block++; stimulus = -1;
            setNotice();
        }
        if(stimulus == 0) { // 단계 시작
            scroll_view.setVisibility(View.GONE);
            button_layout.setVisibility(View.VISIBLE);
            word.setVisibility(View.VISIBLE);
        }
    }

    // 단계 별 초기화
    void setNotice() {
        initVariables();
        scroll_view.scrollTo(0, 0); // 직관적 스크롤 업

        for(int i = 0; i < wrongList.length; i++)
            wrongList[i] = 0;
        leftTitle.setText(titles[block][0]);
        rightTitle.setText(titles[block][1]);
        info.setText(titles[block][0]+" 단어가 나오면 왼쪽 버튼을,\n"+titles[block][1]+" 단어가 나오면 오른쪽 버튼을 눌러주세요.\n가급적 빠르고 정확하게 실시해 주시면 됩니다.\n\n" +
        "틀리면 ×자가 화면에 뜰 것입니다.\n그럴 때는 다시 정답을 눌러주시면 됩니다.\n정답을 맞히시면 곧바로 다음 문제로 넘어가게 됩니다.\n\n" +
                "시작할 준비가 되셨으면 테스트 시작 버튼을 눌러주세요");
        String left_words = "";
        String right_words = "";
        left_words = titles[block][0] + "\n\n" + stimulus_list[block][0][0];
        for (int i = 1; i < stimulus_list[block][0].length; i++)
            left_words += ", " + stimulus_list[block][0][i];
        right_words = titles[block][1] + "\n\n" + stimulus_list[block][1][0];
        for (int i = 1; i < stimulus_list[block][1].length; i++)
            right_words += ", " + stimulus_list[block][1][i];
        firstWords.setText(left_words);
        secondWords.setText(right_words);

        scroll_view.setVisibility(View.VISIBLE);
        button_layout.setVisibility(View.GONE);
        word.setVisibility(View.GONE);
        fail.setVisibility(View.GONE);
    }
    // 랜덤한 단어 출력
    int rn = 0, temp = 0;
    String setWord() {
        second_time = System.currentTimeMillis();
        if(stimulus != -1) timeList[stimulus] = (int)(second_time - first_time); // 반응시간 정보 저장
        first_time = System.currentTimeMillis();

        if(stimulus < trials[block]-1) { // 단계가 끝날 때 호출되면 실행 안함
            Random rand = new Random(System.currentTimeMillis());

            if(block == 0) {
                while(rn == temp)
                    rn = rand.nextInt(randEnd-randStart)+randStart; // start ~ end 의 정수 난수
//                if(rn < 5) cnt_l++; else cnt_r++;
//                if(cnt_l >= limited_trials[block][1]) randStart = 5;
//                if(cnt_r >= limited_trials[block][2]) randEnd = 4;
            }
            else {
                while(rn == temp) {
                    rn = rand.nextInt(randEnd-randStart)+randStart; // start ~ end 의 정수 난수
//                    if(randMEnd == 0 && randMStart == 0) // 모든 단어 출력 || 공부 단어 제외 후 출력 || 혐오감 단어 제외 후 출력
//                        rn = rand.nextInt((randEnd-randStart))+randStart; // start ~ end 의 정수 난수
//                    else if(randStart > randMEnd) // 공부, 호감 단어 제외 후 출력
//                        rn = rand.nextInt((randEnd-randMStart))+randMStart;
//                    else if(randMStart > randEnd) // 호감, 혐오감 단어 제외 후 출력
//                        rn = rand.nextInt((randMEnd-randStart))+randStart;
//                    else { // 호감 단어 제외 후 출력
//                        rn = rand.nextInt(9);
//                        if(rn >= 5) rn += 5;
//                    }
                    if((block == 1 || block == 2) && rn > 14) rn -= 5; // 혐오감 가중치+5
                    if((block == 3 || block == 4) && rn > 14) rn -= 10; // 호감 가중치+5
                }
//                if(rn < 5) cnt_s++; else if(rn < 10) cnt_l++; else cnt_r++;
//
//                if(cnt_s >= limited_trials[block][0]) randStart = 5;
//                if(cnt_l >= limited_trials[block][1]) { randMEnd = 4; randMStart = 10; }
//                if(cnt_r >= limited_trials[block][2]) randEnd = 9;
            }
            temp = rn;
            word.setText(word_list[block == 0 ? 0 : 1][rn]);

            return word_list[block == 0 ? 0 : 1][rn];
        }
        return null;
    }
    void initVariables() {
        timeList = new int[trials[block]];
        wrongList = new int[trials[block]];
        cnt_s = 0; cnt_l = 0; cnt_r = 0;
        randStart = 0; randEnd = block == 0 ? 9 : 20;
        randMStart = 0; randMEnd = 0;
    }

    // IAT Question 결과 저장
    public class JSONSaveDataTask extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... strs) {
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
                // 마지막 행에 D점수
                ParticipantGlobalData.getInstance().putAnswer(number+"_"+(ParticipantGlobalData.getInstance().test_step + 1), str);
                ParticipantGlobalData.getInstance().putAnswer(number+"_"+(ParticipantGlobalData.getInstance().test_step + 1)+"_D", (z_avr_block_5 - z_avr_block_3)+"");
                return str;
            } catch (Exception e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            if (res.equals("")) {
                finishText.setText("결과 저장 실패!");
                finishButton.setText("재시도");
                finishButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new JSONSaveDataTask().execute();
                    }
                });
            }
            else {
                ParticipantGlobalData.getInstance().test_step++;
                if (!ParticipantGlobalData.getInstance().isDoneTest()) {
                    finishText.setText("테스트 완료!\n다음 화면에서도 이와 유사한 형식의 검사가 제시될 예정입니다.\n시작하실 준비가 되셨으면, 아래 버튼을 눌러주세요");
                    finishButton.setText("다음 테스트 시작하기");
                    finishButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), AlertActivity.class);
                            intent.putExtra("number", number);
                            startActivity(intent);
                        }
                    });
                } else {
                    finishText.setText("테스트 완료!\n설문을 계속 진행해주세요");
                    finishButton.setText("설문 진행하기");
                    finishButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ParticipantGlobalData.getInstance().word_list.clear();
                            Intent intent = new Intent(TestingActivity.this, SurveyTempActivity.class);
                            intent.putExtra("current", Integer.parseInt(number)); // 다음 문항으로 바로 넘어가도록 -1을 제거
                            startActivity(intent);
                        }
                    });
                }
            }

            finishText.setVisibility(View.VISIBLE);
            save_progress.setVisibility(View.GONE);
            finishButton.setVisibility(View.VISIBLE);
        }
    }
}

class TestData implements Serializable {
    private int block;
    private int[] reactionTime;
    private int[] wrong;

    public TestData(int b, int[] r, int[] w) {
        block = b + 1;
        reactionTime = r;
        wrong = w;
    }

    public int getBlock() { return block; }
    public int[] getReactionTime() { return reactionTime; }
    public int[] getWrongList() { return wrong; }

    @Override
    public String toString() {
        String str = block+", [";
        for(int r : reactionTime)
            str += r+", ";
        return str+"]";
    }
}

class WordData implements Serializable {
    private String subject;
    private String[] words;

    public WordData(String subject, String[] words) {
        this.subject = subject;
        this.words = words;
    }

    public String getSubject() { return subject; }
    public String[] getWords() { return words; }

    @Override
    public String toString() {
        return "";
    }
}

class StepData implements Serializable {
    private String[] title;
    private int trial;
    private String[] left, right;

    public StepData(String[] title, int trial, String[] left, String[] right) {
        this.title = title;
        this.trial = trial;
        this.left = left;
        this.right = right;
    }

    public String[] getTitle() { return title; }
    public int getTrial() { return trial; }
    public String[] getLeft() { return left; }
    public String[] getRight() { return right; }

    @Override
    public String toString() {
        return "";
    }
}