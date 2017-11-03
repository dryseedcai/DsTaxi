package dryseed.dstaxi.account.view;

/**
 * Created by caiminming on 2017/11/3.
 */

public interface IView {
    /**
     * 显示loading
     */
    void showLoading();

    /**
     * 显示错误
     */
    void showError(int Code, String msg);
}
