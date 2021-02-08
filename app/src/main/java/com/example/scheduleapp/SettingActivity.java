package com.example.scheduleapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;


public class SettingActivity extends AppCompatActivity {


    private UserStatus user; //ユーザ
    private TextInputEditText user_id_text;
    private TextInputEditText password_text;
    private TextInputEditText auth_key_text;

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

        if(user.getUserId()!=null){
            this.user_id_text.setText(user.getUserId());
        }
        if(user.getPassword()!=null){
            this.password_text.setText(user.getPassword());
        }
        if(user.getAuthKey()!=null){
            this.auth_key_text.setText(user.getAuthKey());
        }
        Button save = (Button)findViewById(R.id.save);
        save.setOnClickListener(v -> {this.saveClick();});
        return;
    }

    /**
     * saveボタンを押した時の処理
     */
    private void saveClick(){
        this.user.setUserId(this.getText(this.user_id_text));
        this.user.setPassword(this.getText(this.password_text));
        this.user.setAuthKey(this.getText(this.auth_key_text));
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
