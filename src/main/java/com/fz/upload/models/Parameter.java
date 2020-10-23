package com.fz.upload.models;

import lombok.ToString;

import java.io.File;

@ToString
public class Parameter {
    private File file;
    private String buildType;
    private String serverIp;
    private boolean isOnlyUploadFile = false;
    private boolean isOverwriteFile = false;
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public boolean isOnlyUploadFile() {
        return isOnlyUploadFile;
    }

    public boolean isOverwriteFile() {
        return isOverwriteFile;
    }

    public void setOnlyUploadFile(boolean onlyUploadFile) {
        isOnlyUploadFile = onlyUploadFile;
    }

    public void setOverwriteFile(boolean overwriteFile) {
        isOverwriteFile = overwriteFile;
    }
}
