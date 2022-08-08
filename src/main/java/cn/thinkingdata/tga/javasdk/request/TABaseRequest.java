package cn.thinkingdata.tga.javasdk.request;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import java.io.Closeable;
import java.net.URI;

import static cn.thinkingdata.tga.javasdk.TAConstData.LIB_VERSION;

abstract  public class TABaseRequest implements Closeable {

    public URI getServerUri() {
        return serverUri;
    }
    private final URI serverUri;
    public String getAppId() {
        return appId;
    }
    private final String appId;
    public Integer getConnectTimeout() {
        return connectTimeout;
    }
    private Integer connectTimeout = null;
    public static CloseableHttpClient getHttpClient() {
        return httpClient;
    }
    private static CloseableHttpClient httpClient;
    public TABaseRequest(URI server_uri, String appId, Integer timeout) {
        this(server_uri, appId);
        this.connectTimeout = timeout;
    }
    public TABaseRequest(URI server_uri, String appId) {
        if (httpClient == null) {
            httpClient = TAHttpRequestClient.getHttpClient();
        }
        this.serverUri = server_uri;
        this.appId = appId;
    }

    public synchronized void send(final String data, int dataSize)
    {
        HttpPost httpPost = new HttpPost(getServerUri());
        HttpEntity params = getHttpEntity(data);
        httpPost.setEntity(params);
        httpPost.addHeader("appid", getAppId());
        httpPost.addHeader("TA-Integration-Type", "Java");
        httpPost.addHeader("TA-Integration-Version", LIB_VERSION);
        httpPost.addHeader("TA-Integration-Count", String.valueOf(dataSize));

        if (this.getConnectTimeout() != null) {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(this.getConnectTimeout() + 5000).setConnectTimeout(this.getConnectTimeout()).build();
            httpPost.setConfig(requestConfig);
        }
        sendRequest(httpPost);
    }
    abstract void sendRequest(HttpPost httpPost);
    abstract HttpEntity getHttpEntity(final String data);
    abstract void checkingRetCode(JSONObject resultJson);
    @Override
    public void close() {
        httpClient = null;
    }

}


