package dryseed.dstaxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dryseed.dstaxi.splash.SplashActivity;

public class MainActivity extends Activity {

    private Unbinder mUnbinder = null;

    @OnClick(R.id.start_btn)
    void onClickStartBtn(){
        startActivity(new Intent(this, SplashActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mUnbinder){
            mUnbinder.unbind();
        }
    }
}
