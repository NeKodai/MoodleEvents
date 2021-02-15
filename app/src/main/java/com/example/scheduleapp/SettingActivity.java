package com.example.scheduleapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * 設定画面のビュー
 */
public class SettingActivity extends AppCompatActivity {

    private UserStatus user; //ユーザ
    private TextInputEditText user_id_text;
    private TextInputEditText password_text;
    private TextInputEditText auth_key_text;
    private Spinner before_spinner;
    private Spinner after_spinner;

    /**
     * ビューの初期設定
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.setting_view);
        this.user= new UserStatus();
        this.user.readUserStatus();
        this.user_id_text = (TextInputEditText)findViewById(R.id.user_id_input_text);
        this.password_text = (TextInputEditText)findViewById(R.id.password_input_text);
        this.auth_key_text = (TextInputEditText)findViewById(R.id.auth_key_input_text);
        this.before_spinner = (Spinner)findViewById(R.id.before_spinner);
        this.after_spinner = (Spinner)findViewById(R.id.after_spinner);

        if(user.getUserId()!=null){
            this.user_id_text.setText(user.getUserId());
        }
        if(user.getPassword()!=null){
            this.password_text.setText(user.getPassword());
        }
        if(user.getAuthKey()!=null){
            this.auth_key_text.setText(user.getAuthKey());
        }

        //期間設定用スピナの設定
        List<SpinnerItem> beforeItem = SpinnerItem.getBeforeItems();
        List<SpinnerItem> afterItem = SpinnerItem.getAfterItems();
        ArrayAdapter<SpinnerItem> afterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,afterItem);
        ArrayAdapter<SpinnerItem> beforeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,beforeItem);
        Integer beforeIndex = beforeItem.indexOf(user.getBeforeSpinnerItem());
        Integer afterIndex = afterItem.indexOf(user.getAfterSpinnerItem());

        this.before_spinner.setAdapter(beforeAdapter);
        this.after_spinner.setAdapter(afterAdapter);
        this.before_spinner.setSelection((beforeIndex==-1)? 0 : beforeIndex);
        this.after_spinner.setSelection((afterIndex==-1) ? 0:afterIndex);
        this.before_spinner.setOnItemSelectedListener(new periodSelectedListener(item->this.user.setBeforeSpinnerItem(item)));
        this.after_spinner.setOnItemSelectedListener(new periodSelectedListener(item->this.user.setAfterSpinnerItem(item)));

        Button save = (Button)findViewById(R.id.save);
        save.setOnClickListener(v -> {this.saveClick();});

        return;
    }


    /**
     * 期間設定のスピナのリスナを定義するクラス
     */
    private static class periodSelectedListener implements AdapterView.OnItemSelectedListener{

        Consumer<SpinnerItem> process;

        /**
         * コンストラクタ
         * @param process 選択された時に行う処理
         */
        public periodSelectedListener(Consumer<SpinnerItem> process){
            this.process = process;
        }
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            Spinner spinner = (Spinner)adapterView;
            SpinnerItem anItem = (SpinnerItem)spinner.getAdapter().getItem(position);
            process.accept(anItem);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }


    /**
     * saveボタンを押した時の処理
     */
    private void saveClick(){
        this.user.setUserId(this.getText(this.user_id_text).trim());
        this.user.setPassword(this.getText(this.password_text).trim());
        this.user.setAuthKey(this.getText(this.auth_key_text).trim());
        this.user.writeUserStatus();
        Toast.makeText(this,"保存しました",Toast.LENGTH_SHORT).show();
        return;
    }

    /**
     * TextInputEditTextから入力した文字を取得する
     * @param aEditText 入力欄
     * @return 入力した文字
     */
    private String getText(TextInputEditText aEditText){
        String aString = "";
        if(aEditText!=null) {
            Editable editable = aEditText.getText();
            if (editable != null) {
                SpannableStringBuilder sb = (SpannableStringBuilder) editable;
                aString = sb.toString();
            }
        }
        return aString;
    }

    /**
     * 戻るボタンを押した時の処理
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}

