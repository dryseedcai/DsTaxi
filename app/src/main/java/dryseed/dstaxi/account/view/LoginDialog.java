package dryseed.dstaxi.account.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import dryseed.dstaxi.DsApplication;
import dryseed.dstaxi.R;
import dryseed.dstaxi.account.model.AccountManagerImpl;
import dryseed.dstaxi.account.model.IAccountManager;
import dryseed.dstaxi.account.presenter.ILoginDialogPresenter;
import dryseed.dstaxi.account.presenter.LoginDialogPresenterImpl;
import dryseed.dstaxi.common.http.IHttpClient;
import dryseed.dstaxi.common.http.impl.OkHttpClientImpl;
import dryseed.dstaxi.common.storage.SharedPreferencesDao;
import dryseed.dstaxi.common.util.ToastUtil;


/**
 * 登录框
 * Created by caiminming on 2017/11/3.
 */

public class LoginDialog extends Dialog implements ILoginView {


    private static final String TAG = "LoginDialog";
    private TextView mPhone;
    private EditText mPw;
    private Button mBtnConfirm;
    private View mLoading;
    private TextView mTips;
    private String mPhoneStr;
    private ILoginDialogPresenter mPresenter;

    public LoginDialog(Context context, String phone) {
        this(context, R.style.Dialog);
        mPhoneStr = phone;
        IHttpClient httpClient = new OkHttpClientImpl();
        SharedPreferencesDao dao = new SharedPreferencesDao(DsApplication.getInstance(), SharedPreferencesDao.FILE_ACCOUNT);
        IAccountManager accountManager = new AccountManagerImpl(httpClient, dao);
        mPresenter = new LoginDialogPresenterImpl(this, accountManager);

    }

    public LoginDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.dialog_login_input, null);
        setContentView(root);
        initViews();

    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void initViews() {
        mPhone = (TextView) findViewById(R.id.phone);
        mPw = (EditText) findViewById(R.id.password);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mLoading = findViewById(R.id.loading);
        mTips = (TextView) findViewById(R.id.tips);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        mPhone.setText(mPhoneStr);

    }

    /**
     * 提交登录
     */
    private void submit() {
        String password = mPw.getText().toString();
        //  网络请求登录
        mPresenter.requestLogin(mPhoneStr, password);
    }


    /**
     * 显示／隐藏 loading
     *
     * @param show
     */
    private void showOrHideLoading(boolean show) {
        if (show) {
            mLoading.setVisibility(View.VISIBLE);
            mBtnConfirm.setVisibility(View.GONE);
        } else {
            mLoading.setVisibility(View.GONE);
            mBtnConfirm.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 处理登录成功 UI
     */
    @Override
    public void showLoginSuc() {
        showOrHideLoading(false);
        mTips.setVisibility(View.VISIBLE);
        mTips.setTextColor(getContext().getResources().getColor(R.color.color_text_normal));
        mTips.setText(getContext().getString(R.string.login_suc));
        ToastUtil.show(getContext(), getContext().getString(R.string.login_suc));
        dismiss();

    }

    @Override
    public void showLoading() {
        showOrHideLoading(true);
    }

    @Override
    public void showError(int code, String msg) {

        switch (code) {
            case IAccountManager.PW_ERROR:
                // 密码错误
                showPasswordError();
                break;
            case IAccountManager.SERVER_FAIL:
                // 服务器错误
                showServerError();
                break;
        }
    }

    /**
     * 显示服服务器出错
     */
    private void showServerError() {
        showOrHideLoading(false);
        mTips.setVisibility(View.VISIBLE);
        mTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
        mTips.setText(getContext().getString(R.string.error_server));
    }


    /**
     * 密码错误
     */
    private void showPasswordError() {
        showOrHideLoading(false);
        mTips.setVisibility(View.VISIBLE);
        mTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
        mTips.setText(getContext().getString(R.string.password_error));
    }
}
