package com.interview.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "xunfei.spark")
public class XunfeiSparkConfig {
    private String appid;
    private String apiKey;
    private String apiSecret;
    private String url;

    public String getAppid() { return appid; }
    public void setAppid(String appid) { this.appid = appid; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getApiSecret() { return apiSecret; }
    public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
} 