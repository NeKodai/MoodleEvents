package com.example.scheduleapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

public class AboutAppFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // 先ほどのレイアウトをここでViewとして作成します
        return inflater.inflate(R.layout.about_app_layout, container, false);
    }

    /**
     * ビューが作成さてた後の処理
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView license = view.findViewById(R.id.license_text);
        getActivity().setTitle(R.string.this_app);
        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OssLicensesMenuActivity.class);
                startActivity(intent);
            }
        });
    }
}
