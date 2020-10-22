package com.fz.upload;

import org.apache.commons.lang3.StringUtils;

public class Configs {

    /**
     * 上传服务器IP地址
     */
    static final String SERVER_IP = "10.32.5.200";
    /**
     * 上传APP信息接口路径
     */
    public static final String URL_UPLOAD_APP = "app/uploadInfo";
    /**
     * 上传APP文件接口路径
     */
    public static final String URL_UPLOAD_FILE = "app/uploadFile";

    public static String getServerHost() {
        return checkServerIp(SERVER_IP);
    }

    public static String checkServerIp(String serverIp) {
        if (StringUtils.isEmpty(serverIp)) {
            serverIp = SERVER_IP;
        }
        return "http://" + serverIp + ":8090/";
    }
}
