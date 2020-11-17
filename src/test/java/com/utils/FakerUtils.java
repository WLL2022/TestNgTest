package com.utils;

import com.alibaba.fastjson.JSONArray;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:数据伪造工具类
 * @author:wanglingling
 */
public class FakerUtils {

    /**
     * 读取json文件
     * @param path
     * @return
     */
    public static String get_response_string(String path){
        BufferedReader bufferedReader = null;
        char[] ch = null;
        String s = "";
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
            //创建字节数组
            ch = new char[102400];
            while((bufferedReader.read(ch)) != -1){
                s = new String(ch);
                //System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if(bufferedReader != null){
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return s;
    }

    /**
     * 读取json文件
     * @param path
     * @return
     */
    public static JSONArray get_response_json(String path){
        String s = get_response_string(path);
        Object result = JSONArray.parse(s);
        return (JSONArray) result;

/*        FileReader reader = null;
        JSONArray response = null;
         try
         {
             reader = new FileReader(path);
             //Read JSON file
             BufferedReader br = new BufferedReader(reader);
             String data = "";
             while (true){
                 String buf = br.readLine();
                 if (buf == null){
                     break;
                 }
                 data += buf;
             }
             Object obj = JSONArray.parse(data);

             response = (JSONArray) obj;
             System.out.println(response);

         } catch (IOException e) {
             e.printStackTrace();
         }
         return response;*/
    }

    /**
     * 将file文件数据读出来，存入testng可识别的二维数组中
     * @param filename
     * @return
     * @throws IOException
     */
    public static Object[][] getCSVData(String filename) throws IOException {
        //拿到文件的绝对路径
        String path = new File("").getAbsolutePath();
        //定义List存放从表里读出来的每一行数据
        List<Object[]> records = new ArrayList<Object[]>();
        //定义每一行的数据input_list.csv
        String record;

        //bufferedreader读取缓存数据
        BufferedReader file = new BufferedReader(new InputStreamReader(new FileInputStream(path + filename)));
        //遍历所有行，并存储在list中
        while ((record = file.readLine()) != null){
            String fileLine[] = record.split(",");
            records.add(fileLine);
        }
        //关闭流
        file.close();

        //定义Object[][]二维数组，并将list转换为二维数组
        Object[][] obj = new Object[records.size()][];
        //设置二维数组每行的值，每一行是Object对象
        for(int i=0; i < records.size(); i++){
            obj[i] = records.get(i);
        }
        return obj;
    }

    /**
     * 模拟post请求
     * @param url
     * @param params
     * @return
     */
    public static String doPost(String url, String params) throws IOException {
        //创建Httpclient对象
        CloseableHttpClient hc = HttpClients.createDefault();
        //创建httpPost对象
        HttpPost httpPost = new HttpPost(url);
/*        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");*/
        StringEntity entity = new StringEntity(params, "UTF-8");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;

        response = hc.execute(httpPost);
        StatusLine status = response.getStatusLine();
        if(status.getStatusCode() == HttpStatus.SC_OK){
            HttpEntity responseEntity = response.getEntity();
            Assert.assertFalse(responseEntity == null,url+"接口返回数据为空，请排查！");
            String result = EntityUtils.toString(responseEntity);
            return result;
        }else{
            Assert.assertFalse(true,"接口返回状态码错误");
            return "";
        }
    }

}
