package com.interview.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.interview.config.XunfeiIatConfig;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Service
public class XunfeiIatService extends WebSocketListener {
    @Autowired
    private XunfeiIatConfig iatConfig;
    private static final Gson gson = new Gson();
    private volatile String resultText; // 线程安全
    private CountDownLatch latch;
    private byte[] audioData;
    private List<String> resultList; // 按词区间拼接识别结果

    public static byte[] convertToPcm16kMono(MultipartFile file) throws Exception {
        java.io.File tempInput = java.io.File.createTempFile("input", null);
        java.io.File tempOutput = java.io.File.createTempFile("output", ".pcm");
        file.transferTo(tempInput);
        String ffmpegCmd = String.format("ffmpeg -y -i \"%s\" -acodec pcm_s16le -ac 1 -ar 16000 -f s16le \"%s\"", tempInput.getAbsolutePath(), tempOutput.getAbsolutePath());
        System.out.println("[XunfeiIAT] 执行ffmpeg转码命令: " + ffmpegCmd);
        Process process = Runtime.getRuntime().exec(ffmpegCmd);
        int exitCode = process.waitFor();
        String stdOut = new String(process.getInputStream().readAllBytes());
        String stdErr = new String(process.getErrorStream().readAllBytes());
        System.out.println("[XunfeiIAT] ffmpeg标准输出: " + stdOut);
        System.err.println("[XunfeiIAT] ffmpeg错误输出: " + stdErr);
        if (exitCode != 0) {
            System.err.println("[XunfeiIAT] ffmpeg转码失败，exitCode=" + exitCode);
            tempInput.delete();
            tempOutput.delete();
            throw new RuntimeException("ffmpeg转码失败: " + stdErr);
        }
        byte[] pcmData = java.nio.file.Files.readAllBytes(tempOutput.toPath());
        System.out.println("[XunfeiIAT] 转码后PCM字节数: " + pcmData.length);
        tempInput.delete();
        tempOutput.delete();
        return pcmData;
    }

    public String recognize(MultipartFile audioFile) throws Exception {
        // 自动转码为16kHz/16bit/单声道PCM
        byte[] audioData = convertToPcm16kMono(audioFile);
        return recognize(audioData);
    }

    public String recognize(byte[] audioData) throws Exception {
        this.audioData = audioData;
        this.resultText = null;
        this.resultList = new ArrayList<>();
        this.latch = new CountDownLatch(1);
        String hostUrl = iatConfig.getUrl();
        String authUrl = getAuthUrl(hostUrl, iatConfig.getApiKey(), iatConfig.getApiSecret());
        OkHttpClient client = new OkHttpClient.Builder().build();
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
        System.out.println("[XunfeiIAT] WebSocket连接地址: " + url);
        Request request = new Request.Builder().url(url).build();
        client.newWebSocket(request, this);
        // 等待识别完成（不再超时，直到latch.countDown）
        latch.await();
        if (resultText == null || resultText.isBlank()) {
            System.err.println("[XunfeiIAT] 识别结果为空！");
        } else {
            System.out.println("[XunfeiIAT] 最终识别结果: " + resultText);
        }
        return resultText;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        System.out.println("[XunfeiIAT] WebSocket连接已建立");
        new Thread(() -> {
            int frameSize = 1280;
            int intervel = 40;
            int status = 0;
            int seq = 0;
            int offset = 0;
            try {
                while (offset < this.audioData.length) {
                    seq++;
                    int len = Math.min(frameSize, this.audioData.length - offset);
                    byte[] buffer = Arrays.copyOfRange(this.audioData, offset, offset + len);
                    offset += len;
                    String audioBase64 = Base64.getEncoder().encodeToString(buffer);
                    String json;
                    if (status == 0) {
                        // 首帧
                        json = "{" +
                                "\"common\":{\"app_id\":\"" + iatConfig.getAppid() + "\"}," +
                                "\"business\":{\"language\":\"zh_cn\",\"domain\":\"iat\",\"accent\":\"mandarin\",\"vad_eos\":5000,\"dwa\":\"wpgs\"}," +
                                "\"data\":{\"status\":0,\"format\":\"audio/L16;rate=16000\",\"encoding\":\"raw\",\"audio\":\"" + audioBase64 + "\"}}";
                        status = 1;
                    } else if (offset >= this.audioData.length) {
                        // 最后一帧
                        json = "{\"data\":{\"status\":2,\"format\":\"audio/L16;rate=16000\",\"encoding\":\"raw\",\"audio\":\"" + audioBase64 + "\"}}";
                        status = 2;
                    } else {
                        // 中间帧
                        json = "{\"data\":{\"status\":1,\"format\":\"audio/L16;rate=16000\",\"encoding\":\"raw\",\"audio\":\"" + audioBase64 + "\"}}";
                    }
                    System.out.println("[XunfeiIAT] 发送帧: " + json.substring(0, Math.min(json.length(), 200)) + (json.length() > 200 ? "..." : ""));
                    webSocket.send(json);
                    if (status == 2) break;
                    Thread.sleep(intervel);
                }
            } catch (Exception e) {
                System.err.println("[XunfeiIAT] onOpen Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        System.out.println("[XunfeiIAT] 收到讯飞返回: " + text);
        JsonObject jsonParse = gson.fromJson(text, JsonObject.class);
        if (jsonParse != null && jsonParse.has("code")) {
            int code = jsonParse.get("code").getAsInt();
            if (code != 0) {
                System.err.println("[XunfeiIAT] API返回错误 code=" + code + ", message=" + jsonParse.get("message"));
                this.resultText = null;
                latch.countDown();
                webSocket.close(1000, "");
                return;
            }
        }
        if (jsonParse != null && jsonParse.has("data")) {
            JsonObject data = jsonParse.getAsJsonObject("data");
            if (data.has("result")) {
                JsonObject result = data.getAsJsonObject("result");
                if (result.has("ws")) {
                    // ====== wpgs 按词区间拼接修正版 ======
                    String pgs = result.has("pgs") ? result.get("pgs").getAsString() : null;
                    int[] rg = null;
                    if (result.has("rg")) {
                        try {
                            var rgArr = result.getAsJsonArray("rg");
                            if (rgArr.size() == 2) {
                                rg = new int[]{rgArr.get(0).getAsInt(), rgArr.get(1).getAsInt()};
                            }
                        } catch (Exception e) {
                            System.err.println("[XunfeiIAT] 解析rg字段异常: " + e.getMessage());
                        }
                    }
                    // 解析本次 ws 的所有词
                    List<String> words = new ArrayList<>();
                    for (var ws : result.getAsJsonArray("ws")) {
                        for (var cw : ws.getAsJsonObject().getAsJsonArray("cw")) {
                            words.add(cw.getAsJsonObject().get("w").getAsString());
                        }
                    }
                    if ("rpl".equals(pgs) && rg != null && rg.length == 2) {
                        // 1. 补齐长度到 rg[0]
                        while (resultList.size() < rg[0]) resultList.add("");
                        // 2. 删除 rg[0] ~ rg[1] 区间
                        int removeCount = Math.min(rg[1], resultList.size()) - rg[0];
                        for (int i = 0; i < removeCount; i++) {
                            resultList.remove(rg[0]);
                        }
                        // 3. 在 rg[0] 位置插入 words
                        resultList.addAll(rg[0], words);
                    } else {
                        resultList.addAll(words);
                    }
                }
                if (data.has("status") && data.get("status").getAsInt() == 2) {
                    // 拼接所有词为最终文本
                    StringBuilder sb = new StringBuilder();
                    for (String w : resultList) sb.append(w);
                    this.resultText = sb.toString();
                    System.out.println("[XunfeiIAT] 最终识别结果: " + this.resultText);
                    latch.countDown();
                    webSocket.close(1000, "");
                }
            }
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        System.err.println("[XunfeiIAT] WebSocket连接失败: " + t.getMessage());
        if (response != null) {
            System.err.println("[XunfeiIAT] Response: " + response.toString());
        }
        this.resultText = null;
        latch.countDown();
    }

    private String buildFrameJson(String appid, byte[] buffer, int seq, int status) {
        String audioBase64 = Base64.getEncoder().encodeToString(buffer);
        StringBuilder sb = new StringBuilder();
        sb.append("{\"header\":{\"app_id\":\"").append(appid).append("\",\"status\":").append(status).append("},");
        if (status == 0) {
            sb.append("\"parameter\":{\"iat\":{\"domain\":\"slm\",\"language\":\"zh_cn\",\"accent\":\"mandarin\",\"eos\":6000,\"vinfo\":1,\"dwa\":\"wpgs\",\"result\":{\"encoding\":\"utf8\",\"compress\":\"raw\",\"format\":\"json\"}}},");
        }
        sb.append("\"payload\":{\"audio\":{\"encoding\":\"raw\",\"sample_rate\":16000,\"channels\":1,\"bit_depth\":16,\"seq\":").append(seq).append(",\"status\":").append(status).append(",\"audio\":\"").append(audioBase64).append("\"}}}");
        return sb.toString();
    }

    private String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        StringBuilder builder = new StringBuilder("host: ").append(url.getHost()).append("\n").
                append("date: ").append(date).append("\n").
                append("GET ").append(url.getPath()).append(" HTTP/1.1");
        Charset charset = Charset.forName("UTF-8");
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(charset), "hmacsha256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(builder.toString().getBytes(charset));
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        HttpUrl httpUrl = HttpUrl.parse("https://" + url.getHost() + url.getPath()).newBuilder().
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(charset))).
                addQueryParameter("date", date).
                addQueryParameter("host", url.getHost()).
                build();
        return httpUrl.toString();
    }

    public static void main(String[] args) throws Exception {
        // Spring环境外手动new config并赋值（仅测试用）
        XunfeiIatService service = new XunfeiIatService();
        XunfeiIatConfig config = new XunfeiIatConfig();
        config.setAppid("ce130cad"); // 替换为你的appid
        config.setApiKey("1a87ba822266e04b634cea830e9b9b8e"); // 替换为你的apiKey
        config.setApiSecret("NjQwNzgxMzI0ZWEwMmJmZWZmNmVkNThh"); // 替换为你的apiSecret
        config.setUrl("https://iat-api.xfyun.cn/v2/iat");
        service.iatConfig = config;
        // 读取官方demo音频
        java.nio.file.Path path = java.nio.file.Paths.get("backend/src/main/resources/16k_10.pcm");
        byte[] audioData = java.nio.file.Files.readAllBytes(path);
        String result = service.recognize(audioData);
        System.out.println("[DEMO测试] 官方音频识别结果: " + result);
    }
} 