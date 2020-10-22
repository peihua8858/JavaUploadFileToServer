package com.fz.upload.models;

import lombok.ToString;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;
@ToString
public class AppInfoModel {
    private Long id;
    private String name;
    private String versionCode;
    private String versionName;
    private Long fileSize;
    private String filePath;
    private Integer downloadCount;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer buildNumber;
    private String fileName;
    private String changeLog;
    private String platform;
    private Long duration;
    private String projectName;
    private String downloadUrl;
    private String bundleId;
    /**
     * 插件版本号
     */
    private String pluginVersion;
    private String iconPath;
    private String buildType;
    /**
     * apk 文件路径，1表示Jenkins下载地址
     * 其他表示服务器内部文件
     */
    @FileType
    private Long filePathType;
    /**
     * IOS plist 文件路径
     */
    private String plistUrl;

    public AppInfoModel() {
    }

    public AppInfoModel(Long id) {
        this.id = id;
    }

    public AppInfoModel(String versionName, String platform, String bundleId, String buildType) {
        this.versionName = versionName;
        this.platform = platform;
        this.bundleId = bundleId;
        this.buildType = buildType;
    }

    public void downloadIncreasing() {
        ++downloadCount;
    }

    public boolean wasJenkinsFile() {
        return filePathType != null && filePathType == FileType.JENKINS;
    }

    public boolean wasIOS() {
        return "ios".equalsIgnoreCase(platform);
    }

    public boolean wasAndroid() {
        return "android".equalsIgnoreCase(platform);
    }

    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.PARAMETER, ElementType.FIELD})
    public @interface FileType {
        /**
         * jenkins 打包路径
         */
        long JENKINS = 1;
        /**
         * 服务器地址
         */
        long LOG_SERVICE = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(Integer buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public Long getFilePathType() {
        return filePathType;
    }

    public void setFilePathType(Long filePathType) {
        this.filePathType = filePathType;
    }

    public String getPlistUrl() {
        return plistUrl;
    }

    public void setPlistUrl(String plistUrl) {
        this.plistUrl = plistUrl;
    }
}
