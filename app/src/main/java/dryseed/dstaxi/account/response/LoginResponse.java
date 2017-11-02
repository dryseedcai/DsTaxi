package dryseed.dstaxi.account.response;


import dryseed.dstaxi.common.http.biz.BaseBizResponse;

/**
 * Created by caiminming on 2017/11/2.
 */

public class LoginResponse extends BaseBizResponse {
    Account data;

    public Account getData() {
        return data;
    }

    public void setData(Account data) {
        this.data = data;
    }
}
