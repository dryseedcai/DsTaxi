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
import dryseed.dstaxi.account.presenter.CreatePasswordDialogPresenterImpl;
import dryseed.dstaxi.account.presenter.ICreatePasswordDialogPresenter;
import dryseed.dstaxi.common.http.IHttpClient;
import dryseed.dstaxi.common.http.impl.OkHttpClientImpl;
import dryseed.dstaxi.common.storage.SharedPreferencesDao;
import dryseed.dstaxi.common.util.ToastUtil;


/**
 * 密码创建/修改
 * Created by caiminming on 2017/11/3.
 */

public class CreatePasswordDialog extends Dialog implements ICreatePasswordDialogView {
    private TextView mPhone;
    private Button mBtnConfirm;
    private View mLoading;
    private TextView mPw;
    private TextView mPw1;
    private TextView mTips;
    private String mPhoneStr;
    private ICreatePasswordDialogPresenter mPresenter;

    public CreatePasswordDialog(Context context, String phone) {
        this(context, R.style.Dialog);
        // 上一个页面传来的手机号
        mPhoneStr = phone;
        IHttpClient httpClient = new OkHttpClientImpl();
        SharedPreferencesDao dao = new SharedPreferencesDao(DsApplication.getInstance(), SharedPreferencesDao.FILE_ACCOUNT);
        IAccountManager accountManager = new AccountManagerImpl(httpClient, dao);
        mPresenter = new CreatePasswordDialogPresenterImpl(this, accountManager);

    }

    public CreatePasswordDialog(Context context, int theme) {
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
        View root = inflater.inflate(R.layout.dialog_create_pw, null);
        setContentView(root);
        initViews();
    }

    private void initViews() {
        mPhone = (TextView) findViewById(R.id.phone);
        mPw = (EditText) findViewById(R.id.pw);
        mPw1 = (EditText) findViewById(R.id.pw1);
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

    @Override
    public void dismiss() {
        super.dismiss();
    }

    /**
     * 提交注册
     */
    private void submit() {
        String password = mPw.getText().toString();
        String password1 = mPw1.getText().toString();
        boolean check = mPresenter.checkPw(password, password1);
        if (check) {
            mPresenter.requestRegister(mPhoneStr, password1);
        }
    }

    @Override
    public void showPasswordNull() {
        mTips.setVisibility(View.VISIBLE);
        mTips.setText(getContext().getString(R.string.password_is_null));
        mTips.setTextColor(getContext().
                getResources().getColor(R.color.error_red));
    }

    @Override
    public void showPasswordNotEqual() {
        mTips.setVisibility(View.VISIBLE);
        mTips.setText(getContext().getString(R.string.password_is_not_equal));
        mTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
    }

    /**
     * 注册成功
     */
    @Override
    public void showRegisterSuc() {

        mLoading.setVisibility(View.VISIBLE);
        mBtnConfirm.setVisibility(View.GONE);
        mTips.setVisibility(View.VISIBLE);
        mTips.setTextColor(getContext().getResources().getColor(R.color.color_text_normal));
        mTips.setText(getContext().getString(R.string.register_suc_and_loging));
        // 请求网络，完成自动登录
        mPresenter.requestLogin(mPhoneStr, mPw.getText().toString());
    }

    /**
     * 登录成功
     */
    @Override
    public void showLoginSuc() {
        dismiss();
        ToastUtil.show(getContext(), getContext().getString(R.string.login_suc));
    }

    /**
     * 请求中 loading
     */
    @Override
    public void showLoading() {
        showOrHideLoading(true);
    }

    /**
     * 错误提示
     *
     * @param code
     * @param msg
     */
    @Override
    public void showError(int code, String msg) {
        showOrHideLoading(false);
        switch (code) {
            case IAccountManager.PW_ERROR:
                showLoginFail();
                break;
            case IAccountManager.SERVER_FAIL:
                showServerError();
                break;
        }
    }


    private void showOrHideLoading(boolean show) {
        if (show) {
            mLoading.setVisibility(View.VISIBLE);
            mBtnConfirm.setVisibility(View.GONE);
        } else {
            mLoading.setVisibility(View.GONE);
            mBtnConfirm.setVisibility(View.VISIBLE);
        }

    }


    private void showLoginFail() {
        dismiss();
        ToastUtil.show(getContext(), getContext().getString(R.string.error_server));
    }

    private void showServerError() {
        mTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
        mTips.setText(getContext().getString(R.string.error_server));
    }
}
