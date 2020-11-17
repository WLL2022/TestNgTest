package com.testcase;

import com.alibaba.fastjson.JSONArray;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.utils.FakerUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * 全数据验证
 */
public class TestAllData {

    @BeforeClass
    static void beforeAll() {
        WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(9000)); //No-args constructor will start on port 8080, no HTTPS
        wireMockServer.start();
        configureFor("127.0.0.1", 9000);
    }


    @Test
    public void api_all() {
        stubFor(post(urlEqualTo("/api/xf"))
                .withHeader("content-Type", equalTo("text/plain; charset=UTF-8"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "text/plain; charset=UTF-8")
                        .withBody(FakerUtils.get_response_string("src/main/resources/data/response.json"))));

        stubFor(post(urlEqualTo("/api/qd"))
                .withHeader("content-Type", equalTo("text/plain; charset=UTF-8"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "text/plain; charset=UTF-8")
                        .withBody(FakerUtils.get_response_string("src/main/resources/data/response.json"))));

        String xf_resp = null;
        String qd_resp = null;
        try {
            xf_resp = FakerUtils.doPost("http://127.0.0.1:9000/api/xf", "");
            qd_resp = FakerUtils.doPost("http://127.0.0.1:9000/api/qd", "");
            //下发接口返回数据转JSONArray
            JSONArray xf_json = (JSONArray) JSONArray.parse(xf_resp);
            //抢单接口返回数据转JSONArray
            JSONArray qd_json = (JSONArray) JSONArray.parse(qd_resp);
            //System.out.println(xf_json.getJSONObject(0).get("leadsLevel").toString());
            //得到csv读取出的数据
            Object[][] clues_json = FakerUtils.getCSVData("/src/main/resources/data/clues.csv");
            //取出第一行
            List<String> tableTop = new ArrayList<String>();

            //表头
            if (clues_json.length <= 1) {
                Assert.assertEquals(xf_json.size(), 0);
            }
            for (int i = 0; i < clues_json[0].length; i += 1) {
                tableTop.add(clues_json[0][i].toString());
            }

            // 从第二行开始检查
            for (int row = 1; row < clues_json.length; row++) {
                for (int col = 0; col < tableTop.size(); col += 1) {
                    //判断该条数据runable是否需要验证
                    if (clues_json[row][7].equals("1")) {
                        //验证source值是否为1或2
                        if (!clues_json[row][8].equals("1") && !clues_json[row][8].equals("2")) {
                            System.out.println("source值错误，请排查！");
                            Assert.assertTrue(false, "source值错误，请排查！");
                        }
                        //为1或2，验证数据
                        if (clues_json[row][8].equals("1")) {
                            String a = clues_json[row][col].toString();
                            String header = tableTop.get(col);
                            String b = xf_json.getJSONObject(row - 1).get(header).toString();
                            Assert.assertEquals(a, b);
                        } else {
                            String a = clues_json[row][col].toString();
                            String header = tableTop.get(col);
                            String b = qd_json.getJSONObject(row - 1).get(header).toString();
                            Assert.assertEquals(a, b);
                        }
                    } else {
                        System.out.println("该数据无需验证");
                    }
                }
                System.out.println("第" + row + "行数据验证通过！");
            }
            //遍历，验证
            /*for(int i=0; i < clues_json.length; i++){
                for(int j=0; j < clues_json[i].length; j++){
                    if(i == 0){
                        tableTop.add(clues_json[i][j].toString());
                        System.out.println("表头已存放list");
                    }else{
                        if(clues_json[i][7].equals("1")){
                            if(clues_json[i][8].equals("1")){
                                Assert.assertEquals(clues_json[i][j], xf_json.getJSONObject(i-1).get(tableTop.get(j)).toString());
                                System.out.println("source=1遍历完成！");
                            }else if(clues_json[i][8].equals("2")){
                                System.out.println("source=2");
                            }else{
                                System.out.println("source值错误，请排查！");
                            }
                        }else{
                            System.out.println("该数据无需验证");
                        }
                    }
                }
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //copy("/Users/wll/aaa.txt","/Users/wll/copy.txt");
    }

    /**
     * 输入输出练手
     *
     * @param file1
     * @param file2
     */
    public static void copy(String file1, String file2) {
        BufferedInputStream bufIn = null;
        BufferedOutputStream bufOut = null;
        //读文件
        try {
            bufIn = new BufferedInputStream(new FileInputStream(file1));
            bufOut = new BufferedOutputStream(new FileOutputStream(file2));

            byte[] by = new byte[1024];
            while ((bufIn.read(by)) != -1) {
                bufOut.write(by);
            }
            System.out.println("成功！");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufIn != null) {
                    bufIn.close();
                }
                if (bufOut != null) {
                    bufOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}

