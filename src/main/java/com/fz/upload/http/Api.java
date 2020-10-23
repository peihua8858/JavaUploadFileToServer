package com.fz.upload.http;


import com.fz.upload.Configs;
import com.fz.upload.models.AppInfoModel;
import com.fz.upload.models.HttpResponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 日志服务器
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/10/17 17:35
 */
public interface Api {

    /**
     * upload file
     *
     * @author dingpeihua
     * @date 2019/10/17 17:35
     * @version 1.0
     */
    @POST(Configs.URL_UPLOAD_FILE)
    Call<HttpResponse<String>> uploadFile(@Body RequestBody params);

    /**
     * only upload file
     *
     * @author dingpeihua
     * @date 2019/10/17 17:35
     * @version 1.0
     */
    @POST(Configs.URL_ONLY_UPLOAD_FILE)
    Call<HttpResponse<String>> onlyUploadFile(@Body RequestBody params);

    /**
     * 上传APP数据包
     *
     * @author dingpeihua
     * @date 2019/10/17 17:35
     * @version 1.0
     */
    @POST(Configs.URL_UPLOAD_APP)
    Call<HttpResponse<String>> uploadInfo(@Body AppInfoModel params);
}
