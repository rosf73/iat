package com.kumoh.iat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CheckingActivity extends AppCompatActivity implements View.OnClickListener {

    Button yes, no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking);

        yes = (Button) findViewById(R.id.yes_button);
        no = (Button) findViewById(R.id.no_button);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == yes.getId()) {
            Intent intent = new Intent(getApplicationContext(), SurveyTempActivity.class);
            intent.putExtra("current", 0);

            startActivity(intent);

            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        else
            new AlertDialog.Builder(this)
                    .setTitle("안내")
                    .setMessage("진행 중인 공부가 없으시다면 다음 문항을 진행할 수 없습니다. 참여해주셔서 감사합니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                    })
                    .create()
                    .show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("안내")
                .setMessage("지금 종료하면 결과가 저장되지 않습니다. 그래도 테스트를 종료하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("취소", null)
                .create()
                .show();
    }
}