package com.github.sky.entity;

/**
 * 图片链接
 */
public class UrlEntity {

    private String simpleUrl;
    private String originUrl;
    private String fileKey;

    public UrlEntity(){

    }

    public UrlEntity(String fileKey, String simpleUrl, String originUrl){
        this.fileKey = fileKey;
        this.simpleUrl = simpleUrl;
        this.originUrl = originUrl;
    }

    public UrlEntity(String fileKey, String simpleUrl){
        this.fileKey = fileKey;
        this.simpleUrl = simpleUrl;
    }

    public UrlEntity(String fileKey){
        this.fileKey = fileKey;
    }

    public String getSimpleUrl() {
        return simpleUrl;
    }

    public void setSimpleUrl(String simpleUrl) {
        this.simpleUrl = simpleUrl;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }
}
