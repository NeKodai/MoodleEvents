package com.example.scheduleapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

/**
 * このアプリケーションについて説明するフラグメント
 */
public class AboutAppFragment extends Fragment {

    /**
     * ビューを作成する処理
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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
        ImageView twitter = view.findViewById(R.id.twitter_icon);
        getActivity().setTitle(R.string.this_app);
        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OssLicensesMenuActivity.class);
                startActivity(intent);
            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getString(R.string.twitter));
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
    }
}
