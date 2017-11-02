package dryseed.dstaxi.common.http;

/**
 * HttpClient 抽象接口
 * Created by caiminming on 17/11/2.
 */

public interface IHttpClient {
    IResponse get(IRequest request, boolean forceCache);
    IResponse post(IRequest request, boolean forceCache);
}
