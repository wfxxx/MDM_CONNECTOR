package com.definesys.mdm.connector.util;

import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Http通信工具类
 *
 * @author chen
 */
public class HttpUtil {

    /**
     * post请求传输map数据
     *
     * @param url url地址
     * @param map map数据
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String sendPostDataByMap(String url, Map<String, Object> map) throws ClientProtocolException, IOException {
        String result = "";
        // 创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        // 装填参数
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
            }
        }

        // 设置参数到请求对象中
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

        // 设置header信息
        // 指定报文头【Content-type】、【User-Agent】
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        // 执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = httpClient.execute(httpPost);
        // 获取结果实体
        // 判断网络连接状态码是否正常(0--200都是正常)
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            result = EntityUtils.toString(response.getEntity(), "utf-8");
        }
        // 释放连接
        response.close();

        return result;
    }

    /**
     * post请求传输json数据
     *
     * @param url  url地址
     * @param json json数据
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String sendPostDataByJson(String url,Map<String,String> headers, String json) throws ClientProtocolException, IOException {
        System.out.println("========>uri:"+url);
        System.out.println("========>payload"+json);
        System.out.println("========>header:"+headers);
        String result = "";

        // 创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        if(!"".equals(json) || json != null) {
            // 设置参数到请求对象中
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            stringEntity.setContentEncoding("utf-8");
            httpPost.addHeader("Content-type", "application/json");
            httpPost.setEntity(stringEntity);
        }
        if(headers!= null) {
            Iterator<String> iterator = headers.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String value = headers.get(key);
                httpPost.addHeader(key,value);
            }
        }

        // 执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = httpClient.execute(httpPost);

        // 获取结果实体
        // 判断网络连接状态码是否正常(0--200都是正常)
//        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            result = EntityUtils.toString(response.getEntity(), "utf-8");
//        }
        // 释放链接
        response.close();
        System.out.println("========>响应："+result);
        System.out.println();
        return result;
    }

    /**
     * get请求传输数据
     *
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String sendGetData(String url,Map<String,String> headers) throws ClientProtocolException, IOException {
        System.out.println("========>uri:"+url);
        System.out.println("========>header:"+headers);
        String result = "";

        // 创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建get方式请求对象
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Content-type", "application/json");
        if(headers!= null) {
            Iterator<String> iterator = headers.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String value = headers.get(key);
                httpGet.addHeader(key,value);
            }
        }

        // 通过请求对象获取响应对象
        CloseableHttpResponse response = httpClient.execute(httpGet);

        // 获取结果实体
        // 判断网络连接状态码是否正常(0--200都是正常)
//        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            result = EntityUtils.toString(response.getEntity(), "utf-8");
//        }
        // 释放链接
        response.close();
        System.out.println("========>响应："+result);
        System.out.println();
        return result;
    }

    public static String doPost(String httpUrl, String param,Map<String,String> headers,String auth) throws Exception {

        Map<String,String> map = new HashMap<>();
        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        String result = null;
        try {
            //"http://localhost:8888/dsgc/envinfo/queryEnvListDetail"
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接请求方式
            connection.setRequestMethod("POST");
            // 设置连接主机服务器超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取主机服务器返回数据超时时间：60000毫秒
            connection.setReadTimeout(60000);

            // 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
            connection.setDoOutput(true);
            // 默认值为：true，当前向远程服务读取数据时，设置为true，该参数可有可无
            connection.setDoInput(true);
            //  String auth = "123"+":"+"94248c5e3d6754c58c69846cc7935c21f31c65c2";
            if(StringUtil.isNotBlank(auth)){
                byte[] rel = Base64.encodeBase64(auth.getBytes());
                String res = new String(rel);
                connection.setRequestProperty("Authorization","Basic " + res);
            }
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");//设置参数类型是json格式

            Iterator<String> iterator = headers.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String value = map.get(key);
                connection.setRequestProperty(key,value);
            }
            // 通过连接对象获取一个输出流
            os = connection.getOutputStream();
            // 通过输出流对象将参数写出去/传输出去,它是通过字节数组写出的

//            jsonObject.put("con0","DAG");
            os.write(param.getBytes());
            // 通过连接对象获取一个输入流，向远程读取
            //if (connection.getResponseCode() == 200) {
            Map<String,List<String>> resHeadersMap = connection.getHeaderFields();
            String resHeaders =new Gson().toJson(resHeadersMap);
            map.put("resHeaders",resHeaders);
            is = connection.getInputStream();
            // 对输入流对象进行包装:charset根据工作项目组的要求来设置
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuffer sbf = new StringBuffer();
            String temp = null;
            // 循环遍历一行一行读取数据
            while ((temp = br.readLine()) != null) {
                sbf.append(temp);
//                sbf.append("\r\n");
            }
            result = sbf.toString();

            map.put("resMsg",result);

            // }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 断开与远程地址url的连接
            connection.disconnect();
        }
        return new Gson().toJson(result);
    }

    public static String addFirstParam(String uri,String paramName,String param){
        return uri+"?"+paramName+"="+param;
    }

    public static String addParam(String uri,String paramName,String param){
        return uri+"&"+paramName+"="+param;
    }


    public static String doGet(String httpurl,Map<String,String> headers,String auth) throws Exception {
        System.out.println("uri==============>："+httpurl);
        System.out.println("headers==========>："+headers);
        Map<String,String> map = new HashMap<>();
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;// 返回结果字符串
        try {
            // 创建远程url连接对象
            URL url = new URL(httpurl);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            if(StringUtil.isNotBlank(auth)){
                byte[] rel = Base64.encodeBase64(auth.getBytes());
                String res = new String(rel);
                connection.setRequestProperty("Authorization","Basic " + res);
            }
            if(headers !=null && headers.size() > 0){
                Iterator<String> iterator = headers.keySet().iterator();
                while (iterator.hasNext()){
                    String key = iterator.next();
                    String value = headers.get(key);
                    System.out.println("key："+key);
                    System.out.println("value："+value);
                    connection.setRequestProperty(key,value);
                }
            }
            // 发送请求
            connection.connect();
            // 通过connection连接，获取输入流
            //  if (connection.getResponseCode() == 200) {
            Map<String,List<String>> resHeadersMap = connection.getHeaderFields();
            String resHeaders = new Gson().toJson(resHeadersMap);
            map.put("resHeaders",resHeaders);
            is = connection.getInputStream();
            // 封装输入流is，并指定字符集
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            // 存放数据
            StringBuffer sbf = new StringBuffer();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sbf.append(temp);
                sbf.append("\r\n");
            }
            result = sbf.toString();
            map.put("resMsg",result);
//            map.keySet().stream().forEach(e->{
//                System.out.println(e);
//                System.out.println(map.get(e));
//            });
            //  }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            connection.disconnect();// 关闭远程连接
        }

        return new Gson().toJson(result);
    }


    public static void main(String[] args) throws IOException {
        String uri = "http://10.1.2.61//demdm-api/open/api/getUserInfoByCode?jobNumber=fuhe";
        Map<String,String> map =  new HashMap<>();
        map.put("demdmtoken","eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJkZW1kbXVzZXJpZCI6IjIzNTQ0NjIyNjY2Nzk5NTEzNiIsIkFQUF9JRCI6ImQzMzBjMDRkZWYwZTRjNGI4ZmQxMTU4ZTVkOTRlMWJiIiwiZXhwIjoxNjQ4ODAwNTgwLCJpYXQiOjE2NDg3OTMzODAsIkFQUF9LRVkiOiI1YjhlZjkwODBjMGM0YjMyYTZhZGZlZTQ2NTI4MjIyYiJ9.VquUZXOEj2pKy8B7-BA_Kf0p7e8TiyJ3v8y0ia4LdXTWymsD6YnocknoAMj23PWligHG8BZVwLKHm_Rk6aHNPg");
        System.out.println(sendGetData(uri,MdmAuthUtil.getMdmRequestHeader()));
    }
}
 