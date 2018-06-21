package com.renwq.creditplatedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.renwq.creditplate.CreditCheckView;

public class MainActivity extends AppCompatActivity {
    private CreditCheckView creditCheckView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        creditCheckView = findViewById(R.id.ccv_credit);
    }


    public void begin(View view) {
        creditCheckView.setCurrentCreditValue(600);
    }
}
