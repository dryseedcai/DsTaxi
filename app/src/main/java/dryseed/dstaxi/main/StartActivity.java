package dryseed.dstaxi.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import dryseed.dstaxi.R;
import dryseed.dstaxi.splash.SplashActivity;

/**
 * Created by caiminming on 2017/11/2.
 */

public class StartActivity extends Activity {

    @OnClick(R.id.start_btn)
    void onClickStartBtn() {
        startActivity(new Intent(this, SplashActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


}
