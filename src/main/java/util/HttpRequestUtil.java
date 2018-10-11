package util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import Data.Data;
public class HttpRequestUtil {
    private  String code="";
    private  String msg="";
    private  String type="";
    private  String size="";
    private  String isContainKey="";
    private  String content="";
    private  HashMap<String, String> testResult = new HashMap<String, String>();
    private  RegexUtil ru = new RegexUtil();
    private  String validUriRegex = "(https?|http)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";//合法url正则
    private  String includeUrlRegex = "(src|href){1,}=(\"){0,}((https|http){0,}(:/){0,}[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|])|(url\\(([-A-Za-z0-9+&@#/%?=~_|!:,.]+[-A-Za-z0-9+&@#/%=~_|])\\))";

    public HashMap<String,String> scanChildurl(Data dt,String rooturl,String testurl){
        {
            ArrayList<String> ignoreUrlList = dt.getIgnoreUrlList();
            HashMap<String, String> checkKeyInUrl = dt.getCheckKeyInUrl();
              //用户选择跳过检测或被检测url非法，返回空集合
            if (ignoreUrlList.contains(testurl) || ru.getMatcher_common(validUriRegex, testurl).size() == 0) {
                return testResult;
            } else {
                URL url = null;// 根据链接（字符串格式），生成一个URL对象
                try {
                    url = new URL(testurl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();// 打开URL
                    code = Integer.toString(urlConnection.getResponseCode());
                    msg = urlConnection.getResponseMessage();
                    type = urlConnection.getContentType();
                    size = String.valueOf(urlConnection.getContentLength());
                    ArrayList<String> childUrl = new ArrayList<String>();
                    //被检测url与根目录不在同一个域的和图片资源，只获取code、msg、type、size
                    if (testurl.startsWith(rooturl)&&!testurl.contains(".jpg")&&!testurl.contains(".png")&&!testurl.contains(".ico")) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                urlConnection.getInputStream(), "utf-8"));// 得到输入流，即获得了网页的内容
                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            content += line;
                        }

                        if (checkKeyInUrl.get(testurl) != null) {//需要检查网页关键字
                            if (content.contains(checkKeyInUrl.get(testurl))) {
                                isContainKey = "true";//找到关键字
                            } else {
                                isContainKey = "false";
                            }
                        }
                        //提取网页内容包含的url
                         childUrl = ru.getMatcher(dt, rooturl, includeUrlRegex, content);
                        //放入url关系集合中
                        dt.setUrlRelationship(testurl, childUrl);
                        HttpRequestUtil hru=new HttpRequestUtil();
                        ExecutorService executorService = Executors.newFixedThreadPool(childUrl.size());//线程数设置=子url总数

                        for(int i=0;i<childUrl.size();i++){
                            System.out.println("[检测到url:"+childUrl.get(i));
                            dt.setThreadStatus(1);//设置线程池状态为1：未关闭
                            if(null==dt.getUrlScanStatus().get(childUrl.get(i))){
                                System.out.println("[准备打开url:"+childUrl.get(i));
                                dt.setUrlScanStatus(childUrl.get(i),0);//设置url状态为已检测
                                executorService.submit(new ThreadPool.Worker(dt,rooturl,childUrl.get(i)));//多线程打开url
                            }
                        }
                        System.out.println("跳出循环准备执行shutdown");
                        executorService.shutdown();
                        System.out.println("执行完shutdown");
                        try {
                            while(!executorService.awaitTermination(1, TimeUnit.HOURS)){
                                System.out.println("线程池没有关闭");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        for(int i=0;i<childUrl.size();i++) {
                            dt.setThreadStatus(0);//线程池关闭后设置线程池状态为0：已关闭
                        }
                    }else{
                        testResult.put("code", code);
                        testResult.put("type", type);
                        testResult.put("msg", msg);
                        testResult.put("size", size);
                        testResult.put("content", content);
                        testResult.put("isContainKey", isContainKey);
                        dt.setUrlTestresult(testurl, testResult);//放入url测试结果集合中
                        return testResult;
                    }
                } catch (MalformedURLException e) {
                    code="无响应";
                    msg ="未知";
                    type ="未知";
                    size ="未知";
                } catch (UnsupportedEncodingException e) {
                    code="无响应";
                    msg ="未知";
                    type ="未知";
                    size ="未知";
                } catch (IOException e) {
                    code="无响应";
                    msg ="未知";
                    type ="未知";
                    size ="未知";
                }finally {
                    testResult.put("code", code);
                    testResult.put("type", type);
                    testResult.put("msg", msg);
                    testResult.put("size", size);
                    testResult.put("content", content);
                    testResult.put("isContainKey", isContainKey);
                    dt.setUrlTestresult(testurl, testResult);//放入url测试结果集合中
                    return testResult;
                }
            }
        }


    }
    public HashMap<String,String> scanRooturl(Data dt,String rooturl) {
        RegexUtil ru = new RegexUtil();
        HashMap<String, String> checkKeyInUrl = dt.getCheckKeyInUrl();
        //被检测url非法，返回空集合
        if (ru.getMatcher_common(validUriRegex, rooturl).size() == 0) {
            return testResult;
        } else {
            dt.setUrlScanStatus(rooturl,0);//设置url状态为已检测
            URL url = null;// 根据链接（字符串格式），生成一个URL对象
            try {
                url = new URL(rooturl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();// 打开URL
                code = Integer.toString(urlConnection.getResponseCode());
                msg = urlConnection.getResponseMessage();
                type = urlConnection.getContentType();
                size = String.valueOf(urlConnection.getContentLength());
                ArrayList<String> childUrl = new ArrayList<String>();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(), "utf-8"));// 得到输入流，即获得了网页的内容
                String line = "";
                while ((line = reader.readLine()) != null) {
                    content += line;
                }

                if (checkKeyInUrl.get(rooturl) != null) {//需要检查网页关键字
                    if (content.contains(checkKeyInUrl.get(rooturl))) {
                        isContainKey = "是";//找到关键字
                    } else {
                        isContainKey = "否";
                    }
                }
                //提取网页内容包含的url
                childUrl = ru.getMatcher(dt, rooturl, includeUrlRegex, content);
                //放入url关系集合中
                dt.setUrlRelationship(rooturl, childUrl);
                HttpRequestUtil hru=new HttpRequestUtil();
                ExecutorService executorService = Executors.newFixedThreadPool(childUrl.size());//线程数设置=子url总数

                for(int i=0;i<childUrl.size();i++){
                    dt.setThreadStatus(1);//设置线程池状态为1：未关闭
                    System.out.println(childUrl.get(i));
                    executorService.submit(new ThreadPool.Worker(dt,rooturl,childUrl.get(i)));//多线程打开url
                }
                executorService.shutdown();
                try {
                    while(!executorService.awaitTermination(1, TimeUnit.HOURS)){
                        System.out.println("线程池没有关闭");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for(int i=0;i<childUrl.size();i++) {
                    dt.setThreadStatus(0);//线程池关闭后设置线程池状态为0：已关闭
                }
            } catch (Exception e) {
                code = "无响应";
                msg = "未知";
                type = "未知";
                size = "未知";
            } finally {
                testResult.put("code", code);
                testResult.put("type", type);
                testResult.put("msg", msg);
                testResult.put("size", size);
                testResult.put("isContainKey", isContainKey);
                dt.setUrlTestresult(rooturl, testResult);//放入url测试结果集合中
                return testResult;
            }
        }
    }


    public static void main (String[] argx){
        String url="http://v.baidu.com/";
        String newurl="https://www.baidu.com/";
        //System.out.println(url.startsWith(newurl));
        URL url1 = null;// 根据链接（字符串格式），生成一个URL对象
        try {
            url1 = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();// 打开URL
            System.out.println(urlConnection.getResponseCode());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
