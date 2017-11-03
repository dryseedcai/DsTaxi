package dryseed.dstaxi.account.presenter;

/**
 * Created by caiminming on 2017/11/3.
 */

public interface ICreatePasswordDialogPresenter {
    /**
     * 校验密码输入合法性
     */
    boolean checkPw(String pw, String pw1);
    /**
     *  提交注册
     */
    void requestRegister(String phone, String pw);

    /**
     * 登录
     */
    void requestLogin(String phone, String pw);
}
