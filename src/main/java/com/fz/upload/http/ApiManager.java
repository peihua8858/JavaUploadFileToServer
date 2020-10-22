package com.fz.upload.http;

import com.fz.upload.Configs;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

public class ApiManager {
    static ApiManager dataManager;
    private RetrofitClient mClient;

    protected ApiManager(String serverHost) {
        this(RetrofitClient.newBuilder()
                .setHttpClient(new OkHttpClient.Builder()
                        .connectTimeout(20_000, TimeUnit.MILLISECONDS)
                        .writeTimeout(20_000, TimeUnit.MILLISECONDS)
                        .readTimeout(20_000, TimeUnit.MILLISECONDS)
                        .build())
                .setBaseUrl(serverHost)
                .build());
    }

    protected ApiManager(RetrofitClient client) {
        setRetrofit(client);
    }

    public static ApiManager newInstance(String serverHost) {
        if (dataManager == null) {
            synchronized (ApiManager.class) {
                if (dataManager == null) {
                    if (StringUtils.isEmpty(serverHost)) {
                        serverHost = Configs.getServerHost();
                    }
                    dataManager = new ApiManager(serverHost);
                }
            }
        }
        return dataManager;
    }

    public static ApiManager newInstance() {
        return dataManager;
    }

    public void setRetrofit(RetrofitClient client) {
        if (client == null) {
            throw new NullPointerException("client is null.");
        }
        this.mClient = client;
    }


    public <T> T createApi(Class<T> clazz) {
        return mClient.createRetrofit(clazz);
    }

    public static Api api() {
        return newInstance().createApi(Api.class);
    }
}
