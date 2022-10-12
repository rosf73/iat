package com.kumoh.iat;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParticipantGlobalData {
    static LinearLayout survey_root_parent;
    static String phone_num;
    static int day;
    static JSONArray questions;
    static ArrayList<ArrayList<WordData>> word_list;
    static ArrayList<ArrayList<StepData>> step_list;
    static int test_step;
    static JSONArray answers;
    static int static_fidelity;
    static int fidelity;
    static int giftable;


    public void setRootParentView(LinearLayout view) {
        survey_root_parent = view;
    }
    public void setRootProgress() {
        ((ProgressBar) ((LinearLayout) survey_root_parent.getChildAt(4)).getChildAt(0)).setProgress(getAnsweredLength());
    }
    public void setPhoneNum(String phone_num) { this.phone_num = phone_num; }
    public void setQuestions(JSONArray questions) { this.questions = questions; }
    public void putAnswer(String number, String answer) {
        try {
            for (int i = 0; i < answers.length(); i++)
                if (answers.getJSONObject(i).has("number") && answers.getJSONObject(i).getString("number").equals(number)) {
                    answers.getJSONObject(i).put("answer", answer);
                    return;
                }
            JSONObject json = new JSONObject();
            json.put("number", number);
            json.put("answer", answer);
            answers.put(json);

            int cnt = ((ProgressBar) ((LinearLayout) survey_root_parent.getChildAt(4)).getChildAt(0)).getProgress();
            ((ProgressBar) ((LinearLayout) survey_root_parent.getChildAt(4)).getChildAt(0)).setProgress(cnt + 1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setFidelity(int fidelity) {
        if (fidelity == 0) {
            this.fidelity = 0;
            this.static_fidelity = 1;
        } else {
            this.fidelity = fidelity;
            this.static_fidelity = fidelity;
        }
    }

    public int getAnsweredLength() {
        try {
            int count = 0;
            for (int i = 0; i < answers.length(); i++)
                if (!answers.getJSONObject(i).getString("answer").equals(""))
                    count++;

            return count;
        }
        catch (Exception e) {
            return 0;
        }
    }
    public int getQuestionsLength() { return questions.length(); }
    public String getAnswer(String number) {
        try {
            for (int i = 0; i < answers.length(); i++)
                if (answers.getJSONObject(i).getString("number").equals(number))
                    return answers.getJSONObject(i).getString("answer");

            return "";
        }
        catch (Exception e) {
            return "";
        }
    }

    public boolean isDoneTest() {
        if (test_step >= word_list.size())
            return true;
        return false;
    }
    public boolean isAllAnswered(int number) {
        int q = 0, a = 0;
        try {
            for (int i = 0; i < answers.length(); i++)
                if (answers.getJSONObject(i).getString("number").split("_")[0].equals(number+"")) {
                    q++;
                    if (!answers.getJSONObject(i).getString("answer").equals(""))
                        a++;
                }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (q == a) return true;
        else return false;
    }
    public boolean isFidelity() {
//        Log.e("진정성 :: 전체 --- ", fidelity + " :: " + static_fidelity);
        if ((double) fidelity / (double) static_fidelity >= 0.4) // 충성도가 40% 이상이면
            return true;
        return false;
    }
    public void clear() {
        survey_root_parent = null;
        phone_num = "";
        day = 1;
        questions = new JSONArray();
        word_list = new ArrayList<>();
        step_list = new ArrayList<>();
        test_step = 0;
        answers = new JSONArray();
        static_fidelity = 10;
        fidelity = 10;
    }


    private static ParticipantGlobalData instance = null;

    public static synchronized ParticipantGlobalData getInstance(){
        if(null == instance){
            instance = new ParticipantGlobalData();
            questions = new JSONArray();
            word_list = new ArrayList<>();
            step_list = new ArrayList<>();
            test_step = 0;
            answers = new JSONArray();
        }
        return instance;
    }
}

// 설문 답변 저장용 Singleton 패턴 클래스