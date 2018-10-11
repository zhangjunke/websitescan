package servlet;

import javafx.concurrent.Worker;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import util.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;

import Data.Data;

public class StartScanServlet extends javax.servlet.http.HttpServlet {
    private static final long serialVersionUID = 1L;
    public static void main(String[] args) {
        String checkKeyInUrlS = "=&=&=dsf";
        String[] list = checkKeyInUrlS.split("&");
        HashMap<String, String> checkKeyInUrl = new HashMap<String, String>();

            for(int j=0;j<list.length;j++){
                String[] tmp=list[j].split("=");
                if(tmp.length==2&&tmp[0].length()>0){
                    String key = list[j].split("=")[0];
                    String value = list[j].split("=")[1];
                    checkKeyInUrl.put(key, value);
                }
            }

        System.out.println(checkKeyInUrl);
    }
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        long starttime = System.currentTimeMillis();
        request.setCharacterEncoding("utf-8");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        HttpRequestUtil hct = new HttpRequestUtil();
        String rootUrl = request.getParameter("rootUrl");
        String ignoreUrlS = request.getParameter("ignoreUrl");//eg:ignoreUrlS=/abcd/djke|/jlsd.css
        String checkKeyInUrlS = request.getParameter("checkKeyInUrl");//eg:checkKeyInUrl=/abcd=key1|/kjdf=key2
        RegexUtil ru = new RegexUtil();
        String validUriRegex = "(https?|http)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";//合法url正则
        if (ru.getMatcher_common(validUriRegex, rootUrl).size() == 0) {//检测rooturl是否合法
            String value = "{\"result\":\"-1\",\"msg\":\"url非法！\"}";
            out.println(value);
            out.flush();
            out.close();
        } else {
            Data dt = new Data();
            if (ignoreUrlS != null && ignoreUrlS.length() != 0) {
                String[] ignoreUrllist = ignoreUrlS.split("&");
                for (int i = 0; i < ignoreUrllist.length; i++) {
                    dt.setIgnoreUrlList(ignoreUrllist[i]);//将跳过url存入ignoreUrlList
                }
            }
            System.out.println("ignoreUrlS:" + dt.getIgnoreUrlList());
            if (checkKeyInUrlS != null && checkKeyInUrlS.length() != 0) {
                String[] list = checkKeyInUrlS.split("&");
                HashMap<String, String> checkKeyInUrl = new HashMap<String, String>();
                for(int j=0;j<list.length;j++){
                    String[] tmp=list[j].split("=");
                    if(tmp.length==2&&tmp[0].length()>0){
                        String key = list[j].split("=")[0];
                        String value = list[j].split("=")[1];
                        checkKeyInUrl.put(key, value);
                    }
                }
                dt.setCheckKeyInUrl(checkKeyInUrl);//将重点检测url存入checkKeyInUrl
            }

            //打开网站首页
            try {
                dt.setAllUrl(rootUrl, 0);//allurl集合设置rooturl的初始值
                ArrayList<String> urlRelationshipInit = new ArrayList<>();
                urlRelationshipInit.add("");
                dt.setUrlRelationship(rootUrl, urlRelationshipInit);//urlRelationship集合设置rooturl的初始值
                HashMap<String, String> testResultMapInit = new HashMap<String, String>();
                testResultMapInit.put("", "");
                dt.setUrlTestresult(rootUrl, testResultMapInit);//testResult集合设置rooturl的初始值
                hct.scanRooturl(dt, rootUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }

            long endtime = System.currentTimeMillis();
            long time = endtime - starttime;
            ConcurrentMap<String, HashMap<String, String>> urlTestresult = dt.getUrlTestresult();
            String timeConsuming = String.valueOf(time);
            long timeStamp = System.currentTimeMillis();
            String value = "{";
            value = value + "\"result\": \"success\"," + "\"timeStamp\":\"" + String.valueOf(timeStamp) + "\"," + "\"totalAmount\":\"" + dt.getAllUrl().size() + "\"," + "\"testAmount\":\"" + urlTestresult.size() + "\","+ "\"timeConsuming\":\"" + timeConsuming+"\"}";
            out.println(value);
            //另起一个线程生成测试报告
            ThreadPool tp = new ThreadPool(dt, timeStamp);
            try {
                tp.run(dt, timeStamp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            out.flush();
            out.close();
        }
    }
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }
}
