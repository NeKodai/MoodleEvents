package com.example.scheduleapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.function.Consumer;

public class SettingFragment extends Fragment {

    private UserStatus user; //ユーザ
    private TextInputEditText user_id_text;
    private TextInputEditText password_text;
    private TextInputEditText auth_key_text;
    private Spinner before_spinner;
    private Spinner after_spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // 先ほどのレイアウトをここでViewとして作成します
        return inflater.inflate(R.layout.setting_view, container, false);
    }

    // Viewが生成し終わった時に呼ばれるメソッド
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.user= new UserStatus();
        this.user.readUserStatus();
        this.user_id_text = (TextInputEditText)view.findViewById(R.id.user_id_input_text);
        this.password_text = (TextInputEditText)view.findViewById(R.id.password_input_text);
        this.auth_key_text = (TextInputEditText)view.findViewById(R.id.auth_key_input_text);
        this.before_spinner = (Spinner)view.findViewById(R.id.before_spinner);
        this.after_spinner = (Spinner)view.findViewById(R.id.after_spinner);

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
        ArrayAdapter<SpinnerItem> afterAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,afterItem);
        ArrayAdapter<SpinnerItem> beforeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,beforeItem);
        Integer beforeIndex = beforeItem.indexOf(user.getBeforeSpinnerItem());
        Integer afterIndex = afterItem.indexOf(user.getAfterSpinnerItem());

        this.before_spinner.setAdapter(beforeAdapter);
        this.after_spinner.setAdapter(afterAdapter);
        this.before_spinner.setSelection((beforeIndex==-1)? 0 : beforeIndex);
        this.after_spinner.setSelection((afterIndex==-1) ? 0:afterIndex);
        this.before_spinner.setOnItemSelectedListener(new SettingFragment.periodSelectedListener(item->this.user.setBeforeSpinnerItem(item)));
        this.after_spinner.setOnItemSelectedListener(new SettingFragment.periodSelectedListener(item->this.user.setAfterSpinnerItem(item)));

        Button save = (Button)view.findViewById(R.id.save);
        save.setOnClickListener(v -> {this.saveClick();});

        getActivity().setTitle(R.string.action_settings);

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
        Toast.makeText(getContext(),"保存しました",Toast.LENGTH_SHORT).show();
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
}
