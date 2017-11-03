package dryseed.dstaxi.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import dryseed.dstaxi.DsApplication;
import dryseed.dstaxi.R;
import dryseed.dstaxi.account.PhoneInputDialog;
import dryseed.dstaxi.account.response.Account;
import dryseed.dstaxi.account.response.LoginResponse;
import dryseed.dstaxi.common.http.IHttpClient;
import dryseed.dstaxi.common.http.IRequest;
import dryseed.dstaxi.common.http.IResponse;
import dryseed.dstaxi.common.http.api.API;
import dryseed.dstaxi.common.http.biz.BaseBizResponse;
import dryseed.dstaxi.common.http.impl.BaseRequest;
import dryseed.dstaxi.common.http.impl.BaseResponse;
import dryseed.dstaxi.common.http.impl.OkHttpClientImpl;
import dryseed.dstaxi.common.storage.SharedPreferencesDao;
import dryseed.dstaxi.common.util.ToastUtil;

/**
 * 1 检查本地纪录(登录态检查)
 * 2 若用户没登录则登录
 * 3 登录之前先校验手机号码
 * 4 token 有效使用 token 自动登录
 * todo : 地图初始化
 */
public class MainActivity extends AppCompatActivity {


    private final static String TAG = "MainActivity";
    private IHttpClient mHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHttpClient = new OkHttpClientImpl();
        checkLoginState();
    }

    /**
     * 检查用户是否登录
     */
    private void checkLoginState() {
        // 获取本地登录信息
        SharedPreferencesDao dao = new SharedPreferencesDao(DsApplication.getInstance(), SharedPreferencesDao.FILE_ACCOUNT);
        final Account account = (Account) dao.get(SharedPreferencesDao.KEY_ACCOUNT, Account.class);

        // 登录是否过期
        boolean tokenValid = false;

        // 检查token是否过期
        if (account != null) {
            if (account.getExpired() > System.currentTimeMillis()) {
                // token 有效
                tokenValid = true;
            }
        }

        if (!tokenValid) {
            // 过期，弹出登录界面
            showPhoneInputDialog();
        } else {
            // 请求网络，完成自动登录
            new Thread() {
                @Override
                public void run() {
                    String url = API.Config.getDomain() + API.LOGIN_BY_TOKEN;
                    IRequest request = new BaseRequest(url);
                    request.setBody("token", account.getToken());
                    IResponse response = mHttpClient.post(request, false);
                    Log.d(TAG, response.getData());
                    if (response.getCode() == BaseResponse.STATE_OK) {
                        LoginResponse bizRes = JSON.parseObject(response.getData(), LoginResponse.class);
                        //new Gson().fromJson(response.getData(), LoginResponse.class);
                        if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                            // 保存登录信息
                            Account account = bizRes.getData();
                            // todo: 加密存储
                            SharedPreferencesDao dao = new SharedPreferencesDao(DsApplication.getInstance(), SharedPreferencesDao.FILE_ACCOUNT);
                            dao.save(SharedPreferencesDao.KEY_ACCOUNT, account);

                            // 通知 UI
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(MainActivity.this, getString(R.string.login_suc));
                                }
                            });
                        }
                        if (bizRes.getCode() == BaseBizResponse.STATE_TOKEN_INVALID) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showPhoneInputDialog();
                                }
                            });
                        }
                    } else {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.show(MainActivity.this, getString(R.string.error_server));
                            }
                        });
                    }

                }
            }.start();
        }

    }

    /**
     * 显示手机输入框
     */
    private void showPhoneInputDialog() {
        PhoneInputDialog dialog = new PhoneInputDialog(this);
        dialog.show();
    }

}
