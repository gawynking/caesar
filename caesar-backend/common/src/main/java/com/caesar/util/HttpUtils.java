package com.caesar.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class HttpUtils {

    private static final CloseableHttpClient httpClient = HttpClients
            .custom()
            .setDefaultHeaders(Arrays.asList(
                    new BasicHeader("Content-Type", "application/x-www-form-urlencoded"),
                    new BasicHeader("Accept","application/json")
                )
            )
            .build();

    public static String doGet(String url, Map<String, String> params, String token) throws IOException {
        StringBuilder uriBuilder = new StringBuilder(url);
        if (params != null && !params.isEmpty()) {
            uriBuilder.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                uriBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            uriBuilder.deleteCharAt(uriBuilder.length() - 1); // Remove the last "&"
        }

        HttpGet get = new HttpGet(uriBuilder.toString());
        if (token != null) {
            get.setHeader("token", token);
        }

        try (CloseableHttpResponse response = execute(get)) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        }
    }

    public static String doPostForm(String url, Map<String, String> formParams, String token) throws IOException {
        HttpPost post = new HttpPost(url);

        if (formParams != null && !formParams.isEmpty()) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            for (Map.Entry<String, String> entry : formParams.entrySet()) {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        }

        if (token != null) {
            post.setHeader("token", token);
        }

        try (CloseableHttpResponse response = execute(post)) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        }
    }



    public static String doPostBody(String url, Map<String, String> bodyParams, String token) throws IOException {
        HttpPost post = new HttpPost(url);

        if (bodyParams != null && !bodyParams.isEmpty()) {
            StringBuilder bodyBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : bodyParams.entrySet()) {
                if (bodyBuilder.length() > 0) {
                    bodyBuilder.append("&");
                }
                bodyBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            }
            post.setEntity(new StringEntity(bodyBuilder.toString()));
        }

        if (token != null) {
            post.setHeader("token", token);
        }

        try (CloseableHttpResponse response = execute(post)) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        }
    }

    public static String doPut(String url, Map<String, String> bodyParams, String token) throws IOException {
        HttpPut put = new HttpPut(url);

        if (bodyParams != null && !bodyParams.isEmpty()) {
            StringBuilder bodyBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : bodyParams.entrySet()) {
                if (bodyBuilder.length() > 0) {
                    bodyBuilder.append("&");
                }
                bodyBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            }
            put.setEntity(new StringEntity(bodyBuilder.toString()));
        }

        if (token != null) {
            put.setHeader("token", token);
        }

        try (CloseableHttpResponse response = execute(put)) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        }
    }

    public static String doDelete(String url, Map<String, String> params, String token) throws IOException {
        StringBuilder uriBuilder = new StringBuilder(url);
        if (params != null && !params.isEmpty()) {
            uriBuilder.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                uriBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            uriBuilder.deleteCharAt(uriBuilder.length() - 1); // Remove the last "&"
        }

        HttpDelete delete = new HttpDelete(uriBuilder.toString());

        if (token != null) {
            delete.setHeader("token", token);
        }

        try (CloseableHttpResponse response = execute(delete)) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        }
    }

    private static CloseableHttpResponse execute(org.apache.http.client.methods.HttpRequestBase request) throws IOException {
        return httpClient.execute(request);
    }
}