package dryseed.dstaxi.main.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import dryseed.dstaxi.DsApplication;
import dryseed.dstaxi.R;
import dryseed.dstaxi.account.model.AccountManagerImpl;
import dryseed.dstaxi.account.model.IAccountManager;
import dryseed.dstaxi.account.view.PhoneInputDialog;
import dryseed.dstaxi.common.http.IHttpClient;
import dryseed.dstaxi.common.http.impl.OkHttpClientImpl;
import dryseed.dstaxi.common.storage.SharedPreferencesDao;
import dryseed.dstaxi.common.util.ToastUtil;
import dryseed.dstaxi.main.presenter.IMainPresenter;
import dryseed.dstaxi.main.presenter.MainPresenterImpl;


/**
 * 1 检查本地纪录(登录态检查)
 * 2 若用户没登录则登录
 * 3 登录之前先校验手机号码
 * 4 token 有效使用 token 自动登录
 * todo : 地图初始化
 */
public class MainActivity extends AppCompatActivity
        implements IMainView {
    private final static String TAG = "MainActivity";
    private IMainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IHttpClient httpClient = new OkHttpClientImpl();
        SharedPreferencesDao dao = new SharedPreferencesDao(DsApplication.getInstance(), SharedPreferencesDao.FILE_ACCOUNT);
        IAccountManager manager = new AccountManagerImpl(httpClient, dao);
        mPresenter = new MainPresenterImpl(this, manager);
        mPresenter.loginByToken();

    }

    /**
     * 自动登录成功
     */
    @Override
    public void showLoginSuc() {
        ToastUtil.show(this, getString(R.string.login_suc));
    }

    /**
     * 显示 loading
     */
    @Override
    public void showLoading() {
        // TODO: 17/5/14   显示加载框
    }

    /**
     * 错误处理
     *
     * @param code
     * @param msg
     */
    @Override
    public void showError(int code, String msg) {
        switch (code) {
            case IAccountManager.TOKEN_INVALID:
                // 登录过期
                ToastUtil.show(this, getString(R.string.token_invalid));
                showPhoneInputDialog();
                break;
            case IAccountManager.SERVER_FAIL:
                // 服务器错误
                showPhoneInputDialog();
                break;

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
