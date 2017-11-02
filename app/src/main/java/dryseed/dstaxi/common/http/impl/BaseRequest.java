package dryseed.dstaxi.common.http.impl;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

import dryseed.dstaxi.common.http.IRequest;


/**
 * Created by caiminming on 17/11/2.
 * 封装参数的实现
 */

public class BaseRequest implements IRequest {
    private String method = POST;
    private String url;
    private Map<String, String> header;
    private Map<String, Object> body;

    public BaseRequest(String url) {
        /**
         *  公共参数及头部信息
         */
        this.url = url;
        header = new HashMap();
        body = new HashMap<>();
        header.put("Application-Id", "myTaxiID");
        header.put("API-Key", "myTaxiKey");

    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    public void setHeader(String key, String value) {
        header.put(key, value);
    }

    public void setBody(String key, String value) {
        body.put(key, value);
    }

    @Override
    public String getUrl() {
        if (GET.equals(method)) {
            // 组装 Get 请求参数
            for (String key : body.keySet()) {
                url = url.replace("${" + key + "}", body.get(key).toString());
            }
        }

        return url;
    }

    @Override
    public Map<String, String> getHeader() {
        return header;
    }

    @Override
    public String getBody() {
        if (body != null) {
            // 组装 POST 方法请求参数
            return JSON.toJSONString(this.body);
            //return new Gson().toJson(this.body, HashMap.class);
        } else {
            return "{}";
        }

    }
}
