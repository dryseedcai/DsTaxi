package dryseed.dstaxi.account;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.alibaba.fastjson.JSON;

import java.lang.ref.SoftReference;

import dryseed.dstaxi.DsApplication;
import dryseed.dstaxi.R;
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
import dryseed.dstaxi.common.util.DevUtil;
import dryseed.dstaxi.common.util.ToastUtil;


/**
 * 密码创建/修改
 * Created by caiminming on 2017/11/2.
 */

public class CreatePasswordDialog extends Dialog {

    private static final String TAG = "CreatePasswordDialog";
    private static final int REGISTER_SUC = 1;
    private static final int SERVER_FAIL = 100;
    private static final int LOGIN_SUC = 2;
    private TextView mTitle;
    private TextView mPhone;
    private EditText mPw;
    private EditText mPw1;
    private Button mBtnConfirm;
    private View mLoading;
    private TextView mTips;
    private IHttpClient mHttpClient;
    private String mPhoneStr;
    private MyHandler mHandler;

    /**
     * 接收子线程消息的 Handler
     */
    static class MyHandler extends Handler {

        // 软引用
        SoftReference<CreatePasswordDialog> codeDialogRef;

        public MyHandler(CreatePasswordDialog codeDialog) {
            codeDialogRef = new SoftReference<CreatePasswordDialog>(codeDialog);
        }

        @Override
        public void handleMessage(Message msg) {
            CreatePasswordDialog dialog = codeDialogRef.get();
            if (dialog == null) {
                return;
            }
            // 处理UI 变化
            switch (msg.what) {
                case REGISTER_SUC:
                    dialog.showRegisterSuc();
                    break;
                case LOGIN_SUC:
                    dialog.showLoginSuc();
                    break;
                case SERVER_FAIL:
                    dialog.showServerError();
                    break;
            }

        }
    }

    public CreatePasswordDialog(Context context, String phone) {
        this(context, R.style.Dialog);
        // 上一个页面传来的手机号
        mPhoneStr = phone;
        mHttpClient = new OkHttpClientImpl();
        mHandler = new MyHandler(this);
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
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        mTitle = (TextView) findViewById(R.id.dialog_title);
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
        if (checkPassword()) {
            final String password = mPw.getText().toString();
            final String phonePhone = mPhoneStr;
            // 请求网络， 提交注册
            new Thread() {
                @Override
                public void run() {
                    String url = API.Config.getDomain() + API.REGISTER;
                    IRequest request = new BaseRequest(url);
                    request.setBody("phone", phonePhone);
                    request.setBody("password", password);
                    request.setBody("uid", DevUtil.UUID(getContext()));

                    IResponse response = mHttpClient.post(request, false);
                    Log.d(TAG, response.getData());
                    if (response.getCode() == BaseResponse.STATE_OK) {
                        BaseBizResponse bizRes = JSON.parseObject(response.getData(), BaseBizResponse.class);
                        //new Gson().fromJson(response.getData(), BaseBizResponse.class);
                        if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                            mHandler.sendEmptyMessage(REGISTER_SUC);
                        } else {
                            mHandler.sendEmptyMessage(SERVER_FAIL);
                        }
                    } else {
                        mHandler.sendEmptyMessage(SERVER_FAIL);
                    }

                }
            }.start();
        }
    }

    /**
     * 检查密码输入
     *
     * @return
     */
    private boolean checkPassword() {
        String password = mPw.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mTips.setVisibility(View.VISIBLE);
            mTips.setText(getContext().getString(R.string.password_is_null));
            mTips.setTextColor(getContext().
                    getResources().getColor(R.color.error_red));
            return false;
        }
        if (!password.equals(mPw1.getText().toString())) {
            mTips.setVisibility(View.VISIBLE);
            mTips.setText(getContext()
                    .getString(R.string.password_is_not_equal));
            mTips.setTextColor(getContext()
                    .getResources().getColor(R.color.error_red));
            return false;
        }
        return true;
    }

    public void showOrHideLoading(boolean show) {
        if (show) {
            mLoading.setVisibility(View.VISIBLE);
            mBtnConfirm.setVisibility(View.GONE);
        } else {
            mLoading.setVisibility(View.GONE);
            mBtnConfirm.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 处理注册成功
     */
    public void showRegisterSuc() {
        mLoading.setVisibility(View.VISIBLE);
        mBtnConfirm.setVisibility(View.GONE);
        mTips.setVisibility(View.VISIBLE);
        mTips.setTextColor(getContext()
                .getResources()
                .getColor(R.color.color_text_normal));
        mTips.setText(getContext()
                .getString(R.string.register_suc_and_loging));

        // 请求网络，完成自动登录
        new Thread() {
            @Override
            public void run() {
                String url = API.Config.getDomain() + API.LOGIN;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", mPhoneStr);
                String password = mPw.getText().toString();
                request.setBody("password", password);


                IResponse response = mHttpClient.post(request, false);
                Log.d(TAG, response.getData());
                if (response.getCode() == BaseResponse.STATE_OK) {
                    LoginResponse bizRes = JSON.parseObject(response.getData(), LoginResponse.class);
                    //new Gson().fromJson(response.getData(), LoginResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        // 保存登录信息
                        Account account = bizRes.getData();
                        SharedPreferencesDao dao =
                                new SharedPreferencesDao(DsApplication.getInstance(),
                                        SharedPreferencesDao.FILE_ACCOUNT);
                        dao.save(SharedPreferencesDao.KEY_ACCOUNT, account);

                        // 通知 UI
                        mHandler.sendEmptyMessage(LOGIN_SUC);
                    } else {
                        mHandler.sendEmptyMessage(SERVER_FAIL);
                    }
                } else {
                    mHandler.sendEmptyMessage(SERVER_FAIL);
                }

            }
        }.start();
    }

    public void showLoginSuc() {
        dismiss();
        ToastUtil.show(getContext(), getContext().getString(R.string.login_suc));
    }


    public void showUserExist(boolean exist) {
        if (exist) {
            mTitle.setText(getContext().getString(R.string.modify_pw));
        } else {
            mTitle.setText(getContext().getString(R.string.create_pw));
        }
        mLoading.setVisibility(View.GONE);
        mTips.setVisibility(View.GONE);
    }


    public void showServerError() {
        mTips.setTextColor(getContext()
                .getResources().getColor(R.color.error_red));
        mTips.setText(getContext().getString(R.string.error_server));
    }

}
