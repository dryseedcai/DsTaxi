package dryseed.dstaxi.impl;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import dryseed.dstaxi.common.http.IHttpClient;
import dryseed.dstaxi.common.http.IRequest;
import dryseed.dstaxi.common.http.IResponse;
import dryseed.dstaxi.common.http.api.API;
import dryseed.dstaxi.common.http.impl.BaseRequest;
import dryseed.dstaxi.common.http.impl.OkHttpClientImpl;

/**
 * Created by caiminming on 17/11/2.
 */
public class OkHttpClientImplTest {
    IHttpClient httpClient;

    @Before
    public void setUp() throws Exception {
        httpClient = new OkHttpClientImpl();
        API.Config.setDebug(true);
    }

    @Test
    public void get() throws Exception {
        // 设置 request 参数
        /*String url = API.Config.getDomain() + API.TEST_GET;
        IRequest request = new BaseRequest(url);
        request.setHeader("testHeader", "test header");
        request.setBody("uid", "123456");
        IResponse response = httpClient.get(request, false);
        System.out.println("stateCode = " + response.getCode());
        System.out.println("body =" + response.getData());*/

        String url = API.Config.getDomain() + API.GET_SMS_CODE;
        IRequest request = new BaseRequest(url);
        request.setBody("phone", "15618975555");
        IResponse response = httpClient.get(request, false);
        System.out.println("body =" + response.getData());
    }

    @Test
    public void post() throws Exception {
        // 设置 request 参数
        /*String url = API.Config.getDomain() + API.TEST_POST;
        IRequest request = new BaseRequest(url);
        request.setHeader("testHeader", "test header");
        request.setBody("uid", "123456");
        IResponse response = httpClient.post(request, false);
        System.out.println("stateCode = " + response.getCode());
        System.out.println("body =" + response.getData());*/

        String url = API.Config.getDomain() + API.LOGIN_BY_TOKEN;
        IRequest request = new BaseRequest(url);
        request.setBody("token", "123");
        IResponse response = httpClient.post(request, false);
        System.out.println("body =" + response.getData());
    }

}