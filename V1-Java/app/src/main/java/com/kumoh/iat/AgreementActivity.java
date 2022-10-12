package com.kumoh.iat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AgreementActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView project_name, description, agreement;
    private Button agree_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);

        project_name = (TextView) findViewById(R.id.project_name);
        description = (TextView) findViewById(R.id.description);
        agreement = (TextView) findViewById(R.id.agreement);
        agree_button = (Button) findViewById(R.id.agree_button);

        project_name.setText(getIntent().getStringExtra("project_name"));
        description.setText(getIntent().getStringExtra("description"));
        agreement.setText(getIntent().getStringExtra("agreement"));
        agree_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}