package com.fz.upload;

import com.fz.upload.http.ApiManager;
import com.fz.upload.models.AppInfoModel;
import com.fz.upload.models.Parameter;
import com.fz.upload.parser.IParser;
import com.fz.upload.utils.Utils;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.CompletableFuture;

public class Command implements Runnable {
    @Option(names = {"-f", "--filePath"}, description = "文件路径", required = true)
    private String filePath;
    @Option(names = {"-b", "--buildType"}, description = "编译类型", required = true)
    private String buildType;
    @Option(names = {"-s", "--serverIp"}, description = "文件服务器ip地址")
    private String serverIp;

    @SneakyThrows
    @Override
    public void run() {
        if (StringUtils.isEmpty(filePath)) {
            throw new NullPointerException("文件路径必须不为空。");
        }
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件 " + filePath + "不存在。");
        }
        if (StringUtils.isNotEmpty(serverIp) && !Utils.isIpAddress(serverIp)) {
            throw new IllegalArgumentException("服务器IP " + serverIp + "无效。");
        }
        Parameter parameter = new Parameter();
        parameter.setFile(file);
        parameter.setBuildType(buildType);
        parameter.setServerIp(serverIp);
        String fileName = file.getName();
        String extensionName = FilenameUtils.getExtension(fileName);
        System.out.println("uploadAppFile>>>fileName:" + fileName + ",extensionName:" + extensionName);
        IParser parser = IParser.createParser(extensionName);
        ApiManager.newInstance(Configs.checkServerIp(serverIp));
        CompletableFuture<AppInfoModel> future = parser.onParserProcess(parameter);
        AppInfoModel model = future.get();
        System.out.println("uploadAppFile>>>model:" + model);
    }
}
