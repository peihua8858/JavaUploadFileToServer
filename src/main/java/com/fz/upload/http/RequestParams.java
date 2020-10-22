package com.fz.upload.http;

import com.fz.upload.utils.ParseUtil;
import com.google.gson.Gson;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestParams {

    protected final static String LOG_TAG = "RequestParams";
    protected final ConcurrentHashMap<String, Object> urlParams = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, FileWrapper> fileParams = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, String> headers = new ConcurrentHashMap<>();
    protected final Gson mGson = new Gson();
    protected String jsonParams;
    protected boolean isJsonParams = true;
    protected MediaType mediaType;
    /**
     * 是否 重复尝试
     */
    protected boolean isRepeatable = true;
    protected String contentEncoding = "utf-8";
    /**
     * 是否显示对话框
     */
    protected boolean isShowDialog = true;

    /**
     * Constructs a new empty {@code RequestParams} instance.
     */
    public RequestParams() {
        this((Map<String, Object>) null);
    }

    /**
     * 创建一个是否支持读写缓存的构造函数；
     *
     * @param isReadCache 是否可读写缓存
     * @author dingpeihua
     * @date 2019/9/2 15:33
     * @version 1.0
     */
    public RequestParams(boolean isReadCache) {
        this("isOpenCache", isReadCache);
    }

    /**
     * 开启读写缓存
     *
     * @author dingpeihua
     * @date 2019/9/2 15:36
     * @version 1.0
     */
    public final RequestParams openCache() {
        put("isOpenCache", true);
        return this;
    }

    /**
     * 设置缓存有效时间
     *
     * @author dingpeihua
     * @date 2019/9/2 15:36
     * @version 1.0
     */
    public final RequestParams setlifeTime(long lifeTime) {
        put("lifeTime", lifeTime);
        return this;
    }

    /**
     * Adds a key/value string pair to the request.
     *
     * @param key   the key name for the new param.
     * @param value the value string for the new param.
     */
    public RequestParams addHeader(String key, String value) {
        if (key != null && value != null) {
            headers.put(key, value);
        }
        return this;
    }

    public RequestParams setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    /**
     * 设置是否开启缓存
     *
     * @author dingpeihua
     * @date 2019/9/2 16:35
     * @version 1.0
     */
    public final void setOpenCache(boolean isReadCache) {
        put("isOpenCache", isReadCache ? "true" : "false");
    }

    /**
     * Constructs a new RequestParams instance containing the key/value string params from the
     * specified map.
     *
     * @param source the source key/value string map to add.
     */
    public RequestParams(Map<String, Object> source) {
        if (source != null) {
            for (Map.Entry<String, Object> entry : source.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Constructs a new RequestParams instance and populate it with a single initial key/value
     * string param.
     *
     * @param key   the key name for the intial param.
     * @param value the value string for the initial param.
     */
    public RequestParams(final String key, final Object value) {
        this(new HashMap<String, Object>() {{
            put(key, value);
        }});
    }


    /**
     * Constructs a new RequestParams instance and populate it with multiple initial key/value
     * string param.
     *
     * @param keysAndValues a sequence of keys and values. Objects are automatically converted to
     *                      Strings (including the value {@code null}).
     * @throws IllegalArgumentException if the number of arguments isn't even.
     */
    public RequestParams(Object... keysAndValues) {
        int len = keysAndValues.length;
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Supplied arguments must be even");
        }
        for (int i = 0; i < len; i += 2) {
            String key = String.valueOf(keysAndValues[i]);
            String val = String.valueOf(keysAndValues[i + 1]);
            put(key, val);
        }
    }

    /**
     * Adds a key/value string pair to the request.
     *
     * @param key   the key name for the new param.
     * @param value the value string for the new param.
     */
    public RequestParams put(String key, Object value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
        return this;
    }

    public RequestParams putJsonParams(String params) {
        jsonParams = params;
        return this;
    }


    /**
     * Adds a file to the request.
     *
     * @param key  the key name for the new param.
     * @param file the file to add.
     * @throws FileNotFoundException throws if wrong File argument was passed
     */
    public RequestParams put(String key, File file) throws FileNotFoundException {
        put(key, file, null, null);
        return this;
    }

    /**
     * Adds a file to the request with custom provided file name
     *
     * @param key            the key name for the new param.
     * @param file           the file to add.
     * @param customFileName file name to use instead of real file name
     * @throws FileNotFoundException throws if wrong File argument was passed
     */
    public RequestParams put(String key, String customFileName, File file) throws FileNotFoundException {
        put(key, file, null, customFileName);
        return this;
    }

    /**
     * Adds a file to the request with custom provided file content-type
     *
     * @param key         the key name for the new param.
     * @param file        the file to add.
     * @param contentType the content type of the file, eg. application/json
     * @throws FileNotFoundException throws if wrong File argument was passed
     */
    public RequestParams put(String key, File file, String contentType) throws FileNotFoundException {
        put(key, file, contentType, null);
        return this;
    }

    /**
     * Adds a file to the request with both custom provided file content-type and file name
     *
     * @param key            the key name for the new param.
     * @param file           the file to add.
     * @param contentType    the content type of the file, eg. application/json
     * @param customFileName file name to use instead of real file name
     * @throws FileNotFoundException throws if wrong File argument was passed
     */
    public RequestParams put(String key, File file, String contentType, String customFileName) throws FileNotFoundException {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException();
        }
        if (key != null) {
            fileParams.put(key, new FileWrapper(file, contentType, customFileName));
        }
        return this;
    }


    /**
     * Adds a int value to the request.
     *
     * @param key   the key name for the new param.
     * @param value the value int for the new param.
     */
    public RequestParams put(String key, int value) {
        if (key != null) {
            urlParams.put(key, String.valueOf(value));
        }
        return this;
    }

    public void clearUrlParams() {
        urlParams.clear();
    }

    /**
     * Adds a long value to the request.
     *
     * @param key   the key name for the new param.
     * @param value the value long for the new param.
     */
    public RequestParams put(String key, long value) {
        if (key != null) {
            urlParams.put(key, String.valueOf(value));
        }
        return this;
    }


    /**
     * Removes a parameter from the request.
     *
     * @param key the key name for the parameter to remove.
     */
    public void remove(String key) {
        urlParams.remove(key);
        fileParams.remove(key);
    }

    /**
     * Check if a parameter is defined.
     *
     * @param key the key name for the parameter to check existence.
     * @return Boolean
     */
    public boolean has(String key) {
        return urlParams.get(key) != null ||
                fileParams.get(key) != null;
    }


    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public ConcurrentHashMap<String, Object> getUrlParams() {
        return urlParams;
    }

    public ConcurrentHashMap<String, FileWrapper> getFileParams() {
        return fileParams;
    }

    public ConcurrentHashMap<String, String> getHeaders() {
        return headers;
    }


    public String getJsonParams() {
        return jsonParams;
    }

    public void setJsonParams(String jsonParams) {
        this.jsonParams = jsonParams;
    }

    public void setJsonParams(boolean b) {
        this.isJsonParams = b;
    }

    public boolean isRepeatable() {
        return isRepeatable;
    }

    public void setRepeatable(boolean isRepeatable) {
        this.isRepeatable = isRepeatable;
    }

    public boolean isShowDialog() {
        return isShowDialog;
    }

    public void setShowDialog(boolean isShowDialog) {
        this.isShowDialog = isShowDialog;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, Object> entry : urlParams.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }

        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }

            result.append(entry.getKey());
            result.append("=");
            result.append("FILE");
        }

        return result.toString();
    }


    public void setIsRepeatable(boolean flag) {
        this.isRepeatable = flag;
    }


    public static class FileWrapper implements Serializable {
        public final File file;
        public String contentType;
        public String customFileName;

        public FileWrapper(File file, String contentType, String customFileName) {
            this.file = file;
            this.contentType = contentType;
            this.customFileName = customFileName;
            if (StringUtils.isEmpty(contentType)) {
                this.contentType = "image/*";
            }
            if (StringUtils.isEmpty(customFileName)) {
                this.customFileName = file.getName();
            }
        }
    }

    public RequestBody createRequestBody() {
        return createRequestBody(this);
    }

    public MultipartBody createFileRequestBody() {
        return createFileRequestBody(this);
    }


    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType FORM_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");

    /**
     * 构建请求参数
     *
     * @param params
     * @author dingpeihua
     * @date 2020/4/13 15:42
     * @version 1.0
     */
    public static String createRequestParams(RequestParams params) {
        Map<String, Object> paramsMap = params.urlParams;
        if (params.isJsonParams) {
            if (StringUtils.isEmpty(params.jsonParams)) {
                //json 参数
                Gson mGson = params.mGson;
                params.jsonParams = mGson.toJson(paramsMap);
            }
            return params.jsonParams;
        } else {
            StringBuilder builder = new StringBuilder();
            Iterator<String> it = paramsMap.keySet().iterator();
            // add 参数
            while (it.hasNext()) {
                String key = it.next();
                Object value = paramsMap.get(key);
                if (builder.length() > 0) {
                    builder.append("&");
                }
                builder.append(key);
                builder.append("=");
                if (value != null) {
                    builder.append(ParseUtil.toString(value));
                }
            }
            return builder.toString();
        }
    }

    /**
     * 请求参数
     *
     * @param params
     * @return
     */
    public static RequestBody createRequestBody(RequestParams params) {
        Map<String, Object> paramsMap = params.urlParams;
        if (params.isJsonParams) {
            MediaType mediaType = params.mediaType != null ? params.mediaType : JSON_TYPE;
            if (StringUtils.isEmpty(params.jsonParams)) {
                //json 参数
                Gson mGson = params.mGson;
                params.jsonParams = mGson.toJson(paramsMap);
            }
            return RequestBody.Companion.create(params.jsonParams, mediaType);
        } else {
            // Form表单
            FormBody.Builder builder = new FormBody.Builder();
            Iterator<String> it = paramsMap.keySet().iterator();
            // add 参数
            while (it.hasNext()) {
                String key = it.next();
                Object value = paramsMap.get(key);
                if (value != null) {
                    builder.add(key, ParseUtil.toString(value));
                }
            }
            if (params.headers.size() > 0) {
                Map<String, String> headers = params.headers;
                Iterator<String> keys = headers.keySet().iterator();
                while (keys.hasNext()) {
                    final String key = keys.next();
                    final String value = headers.get(key);
                    if (value != null) {
                        builder.add(key, value);
                    }
                }
            }
            return builder.build();
        }
    }

    /**
     * 文件上传
     *
     * @param params
     * @return
     */
    public static MultipartBody createFileRequestBody(RequestParams params) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        MediaType mediaType = params.mediaType != null ? params.mediaType : JSON_TYPE;
        //add 参数
        for (ConcurrentHashMap.Entry<String, Object> entry : params.urlParams.entrySet()) {
            builder.addFormDataPart(entry.getKey(), ParseUtil.toString(entry.getValue()));
        }
        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : params.fileParams.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue().customFileName,
                    RequestBody.create(MediaType.parse(entry.getValue().contentType), entry.getValue().file));
        }
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }
}
