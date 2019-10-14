package com.mmh.learnframe.myhttpclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**实体类型的json上传
 * @author muminghui
 * @date 2019/7/15 17:53
 */
@Slf4j
public class HttpClientUtils2 {

    public static void main(String[] args) {
        System.out.println(getRequest("http://localhost:8080/api/getWeather"));
    }

    //方式一：get无参
    public static String getRequest(String url) {
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = template.getForEntity(url, String.class);
        return response.getBody();
    }
    //方式二：get 文件下载
    public boolean downFileByGet(String url, String token, File targetFile) {
        CloseableHttpClient client = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
        // 设置超时时间
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).build();
        httpGet.setConfig(requestConfig);

        httpGet.setHeader("token", token);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
        } catch (IOException e) {
            log.error("", e);
        }
        assert response != null;
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            try {
                // 写入文件
                response.getEntity().writeTo(new FileOutputStream(targetFile));
            } catch (IOException e) {
                log.error("", e);
            }
        }
        return true;
    }

    //post 表单提交
    public String postFormWithHttp(Map<String, Object> map, String url, String token) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(url);
        // 设置超时时间
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).build();
        httpPost.setConfig(requestConfig);

        httpPost.setHeader("token", token);
        ContentType contentType = ContentType.create("text/plain", Consts.UTF_8);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.addPart(entry.getKey(), new StringBody(entry.getValue().toString(), contentType));
        }
        HttpEntity httpEntity = null;
        try {
            httpEntity = builder.setCharset(CharsetUtils.get("UTF-8")).build();
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }
        httpPost.setEntity(httpEntity);

        return execute(client, httpPost);
    }

    private String execute(CloseableHttpClient client, HttpRequestBase httpPost) throws IOException {
        if (client == null || httpPost == null) {
            return "";
        }
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                return EntityUtils.toString(resEntity);
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            response.close();
        }
        return "";
    }


    public static String uploadFile(String url, File file, String targetPath, String fileName) throws Exception {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // 文件路径
        builder.addBinaryBody("file", file);
        builder.addTextBody("targetPath", targetPath);
        builder.addTextBody("fileName", fileName);
        return getResponseBody(builder, url);
    }

    private static String getResponseBody(MultipartEntityBuilder builder, String url) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        //生成HTTP POST实体
        HttpEntity entity = builder.build();
        //设置请求参数
        post.setEntity(entity);
        CloseableHttpResponse response = client.execute(post);
        HttpEntity results = response.getEntity();
        String returnStr = "";
        if (results != null) {
            //按指定编码转换结果实体为String类型
            returnStr = EntityUtils.toString(results, "UTF-8");
            log.info("FTP接口返回信息：" + returnStr);
        }
        //释放
        EntityUtils.consume(entity);
        //释放链接
        response.close();
        return returnStr;
    }
}
