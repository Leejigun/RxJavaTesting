package com.openit.jglee.rxjavatesting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.Observable;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //세상에서 가장 간단한 Rx
        TextView textView = (TextView)findViewById(R.id.txtHello);
        Observable.just(textView.getText().toString())
                .map(s -> s+ " Rx!")
                .subscribe(text->textView.setText(text));


        TextView textView1 = (TextView)findViewById(R.id.textView);

        Button btnMulti =(Button)findViewById(R.id.btnMulti);
        EditText inputText = (EditText)findViewById(R.id.editText);

        inputText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE)
                {
                    btnMulti.callOnClick();
                    return true;
                }
                return false;
            }
        });

        btnMulti.setOnClickListener(view -> {
            textView1.setText("");
            Observable.just(inputText.getText().toString())
                    .map(den->Integer.parseInt(den))
                    .filter(den -> den>1&&den<10)
                    .flatMap(den -> Observable.range(1,9),
                            (den, row) -> den+" * "+row+" = "+(den*row)+"\n")
                    .subscribe(
                            textView1::append,
                            e-> Toast.makeText(this,"Only number",Toast.LENGTH_LONG).show());
        });
    }

}
