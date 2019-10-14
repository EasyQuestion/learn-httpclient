package com.mmh.learnframe.myhttpclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmh.learnframe.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**json格式的post上传
 * @author muminghui
 * @date 2019/7/15 17:43
 */
@Slf4j
public class HttpClientUtils {

    public static String getData(String url){

//        无参GET请求
//        HttpGet httpGet = new HttpGet(url);

        //GET有参(方式一：直接拼接URL)
//        StringBuffer params = new StringBuffer();
//        try {
//            // 字符数据最好encoding以下;这样一来，某些特殊字符才能传过去(如:某人的名字就是“&”,不encoding的话,传不过去)
//            params.append("name=" + URLEncoder.encode("&", "utf-8"));
//            params.append("&");
//            params.append("age=24");
//        } catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }
//
//        HttpGet httpGet = new HttpGet(url+ "?" + params);


        //GET有参(方式二：使用URI获得HttpGet)
        URI uri = null;
        try {
            // 将参数放入键值对类NameValuePair中,再放入集合中
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("name", "&"));
            params.add(new BasicNameValuePair("age", "18"));

            uri = new URIBuilder().setScheme("http").setHost("localhost")
                    .setPort(12345).setPath("/doGetControllerTwo")
                    // 注:这里也支持一个键值对一个键值对地往里面放setParameter(String key, String value)
                    .setParameters(params).build();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        // 创建Get请求
        HttpGet httpGet = new HttpGet(uri);
        return getResponse(httpGet);
    }

    public static String getDataWithToken(String url,String token) {
        // 创建get请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-Type", "application/json;charset=utf-8");
        httpGet.setHeader("token", token);
        httpGet.setHeader("User-Agent", "platform");
        return getResponse(httpGet);
    }

    private static String getResponse(HttpGet httpGet){

        // 配置信息
        RequestConfig config = RequestConfig.custom()
                // 设置连接超时时间(单位毫秒)
                .setConnectTimeout(5000)
                // 设置请求超时时间(单位毫秒)
                .setConnectionRequestTimeout(5000)
                // socket读写超时时间(单位毫秒)
                .setSocketTimeout(5000)
                // 设置是否允许重定向(默认为true)
                .setRedirectsEnabled(true)
                .build();

        httpGet.setConfig(config);

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String responseContent = null;
        try {
            /**等价于      httpClient = HttpClientBuilder.create().build();*/
            httpClient = HttpClients.createDefault();
            response = httpClient.execute(httpGet);
            System.out.println("响应状态为:" + response.getStatusLine());
            /**响应内容*/
            HttpEntity entity = response.getEntity();
            System.out.println("响应内容长度为:" + entity.getContentLength());
            responseContent = EntityUtils.toString(entity, "UTF-8");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
        } catch (ParseException e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
        } catch (IOException e){
            e.printStackTrace();
            log.error(e.getMessage(),e);
        } finally {
            try {
                // 关闭连接,释放资源
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }
        return responseContent;
    }

    public static String postData(String url){
        //post无参
//        HttpPost httpPost = new HttpPost(url);

//        POST有参(普通参数)一直接在url后缀加上参数
//        StringBuffer params = new StringBuffer();
//        try {
//            params.append("name=" + URLEncoder.encode("&", "utf-8"));
//            params.append("&");
//            params.append("age=24");
//        } catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }
//        HttpPost httpPost = new HttpPost(url+ "?" + params);

        //POST有参(普通参数)二使用URI获得httpPost
//        URI uri = null;
//        try {
//            // 将参数放入键值对类NameValuePair中,再放入集合中
//            List<NameValuePair> params = new ArrayList<>();
//            params.add(new BasicNameValuePair("flag", "4"));
//            params.add(new BasicNameValuePair("meaning", "这是什么鬼？"));
//            uri = new URIBuilder().setScheme("http").setHost("localhost").setPort(12345)
//                    .setPath("/doPostControllerThree")
//                    // 注:这里也支持一个键值对一个键值对地往里面放setParameter(String key, String value)
//                    .setParameters(params).build();
//        } catch (URISyntaxException e1) {
//            e1.printStackTrace();
//        }
//
//        HttpPost httpPost = new HttpPost(uri);

        //POST有参(对象参数) json格式传输参数
        HttpPost httpPost = new HttpPost(url);
        User user = new User();
        user.setUserNo("123456789");
        user.setUsername("lee");
        String jsonStr = JSONObject.toJSONString(user);
        StringEntity reqEntity = new StringEntity(jsonStr,"utf-8");
        //下面的2行可以不写，因为设置了header
        //reqEntity.setContentEncoding("UTF-8");
        //reqEntity.setContentType("application/json");
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        httpPost.setEntity(reqEntity);

        //POST有参(普通参数 + 对象参数):普通参数加到链接后面，对象参数用json串来传递

        return postResponseData(httpPost);
    }

    public static String postData(String url, Map<String, Object> map){
        HttpPost httpPost = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(JSON.toJSONString(map), "UTF-8");
        stringEntity.setContentEncoding("UTF-8");
        stringEntity.setContentType("application/json");

        httpPost.setEntity(stringEntity);
        return postResponseData(httpPost);
    }

    public static String postDataWithToken(String url, String token, String jsonStr)throws Exception {

        // 创建Post请求
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        httpPost.setHeader("token", token);
        httpPost.setHeader("User-Agent", "platform");

        StringEntity reqEntity = new StringEntity(jsonStr,"utf-8");
        reqEntity.setContentEncoding("UTF-8");
        reqEntity.setContentType("application/json");
        httpPost.setEntity(reqEntity);
        return postResponseData(httpPost);
    }

    private static String postResponseData(HttpPost httpPost){
        RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).setRedirectsEnabled(true).build();
        httpPost.setConfig(config);

        String responseContent = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;

        try {
            httpClient = HttpClients.createDefault();
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                responseContent = EntityUtils.toString(entity, "UTF-8");
            }

        }  catch (ClientProtocolException e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
        } catch (ParseException e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
        } catch (IOException e){
            e.printStackTrace();
            log.error(e.getMessage(),e);
        } finally {
            try {
                // 关闭连接,释放资源
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }
        return responseContent;
    }

    public static void main(String[] args) {
        String resultStr = getData("http://localhost:8080/api/getWeather");
        System.out.println(resultStr);
    }
}
