package util;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Data.Data;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class ThreadPool {
    private String htmlReportString1 ="<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "    <title>测试开放平台</title>\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html;\" />\n" +
            "    <link href=\"../html/style/adminStyle.css\" rel=\"stylesheet\" type=\"text/css\" />\n" +
            "    <script src=\"js/jquery.js\"></script>\n" +
            "</head>\n" +
            "<body>\n" +
            "<style>\n" +
            "    body{ text-align:right}\n" +
            "    .div2{ margin:0cm 0cm 0cm 0cm; width:2500px; height:35px; border:1px solid #FFFFFF;text-decoration:underline;}\n" +
            "</style>\n" +
            "<div class=\"wrap\">\n" +
            "    <div class=\"page-title\">\n" +
            "        <span class=\"modular fl\"><i class=\"edit_user\"></i><em>扫描结果</em></span>\n" +
            "    </div>\n" +
            "    <div class=\"div2\">" ;
    private String htmlReportString2 ="</div>\n" +
            "    <div id=\"resultList\">  </div>\n" +
            "    <style>\n" +
            "        body{ text-align:center}\n" +
            "        .div1{ margin:0 auto; width:200px; height:35px; border:1px solid #FFFFFF}\n" +
            "    </style>\n" +
            "\n" +
            "    <table class=\"list-style\" border=\"1\" bgcolor=\"#E6E6FA\">\n" +
            "            <tr>\n" +
            "                <th>id</th>\n" +
            "                <th>url</th>\n" +
            "                <th>response_code</th>\n" +
            "                <th>response_msg</th>\n" +
            "                <th>checkKey</th>\n" +
            "                <th>isContainKey</th>\n" +
            "                <th>response_type</th>\n" +
            "                <th>response_size</th>\n" +
            "                <th>links_out</th>\n" +
            "                <th>links_in</th>\n" +
            "            </tr>\n";
    public ThreadPool(Data dt,long timeStamp){
          }
           public void run(Data dt,long timeStamp) throws Exception {
               String rootpath1=this.getClass().getClassLoader().getResource("/").getPath();
               //将测试报告模版拷贝至tmp目录
               String rootpath=rootpath1.replace("WEB-INF/classes/", "");
               String templateFile=rootpath+"Properties/"+"template.xlsx";
               String scanResultFileName=String.valueOf(timeStamp);
               String scanResultFile_xlsx=rootpath+"TestReport/"+scanResultFileName+".xlsx";
               String scanResultFile_html=rootpath+"TestReport/"+scanResultFileName+".html";
               FileOperation fo=new FileOperation();
               fo.copy(templateFile,scanResultFile_xlsx);//拷贝xlsx报告模版

               PrintStream printStream = new PrintStream(new FileOutputStream(scanResultFile_html));
               StringBuilder sb = new StringBuilder();//生成html报告


                //当集合ThreadStatus中0和1的数量相等时说明线程全部执行完毕
               int count1=0;
               int count2=1;
               while(count1!=count2){
                   count1=Collections.frequency(dt.getThreadStatus(),0);
                   count2=Collections.frequency(dt.getThreadStatus(),1);
                   System.out.println(count1);
                   System.out.println(count2);
                   Thread.sleep(1000);
               }
               //生成测试报告
               sb.append(htmlReportString1);
               sb.append("<a href=\"/websitescan/DownScanResult/timeStamp="+scanResultFileName+"\">点击下载测试报告</a>");
               sb.append(htmlReportString2);
               ConcurrentMap<String, ArrayList<String>> urlRelationship = dt.getUrlRelationship();
               ConcurrentMap<String, HashMap<String, String>> urlTestresult = dt.getUrlTestresult();
               HashMap<String,String > checkKeyInUrl=dt.getCheckKeyInUrl();
               ConcurrentMap<String, Integer> allUrl=dt.getAllUrl();
               HashMap<String,Integer>  links_inMap=new HashMap<String,Integer>();
               List<Integer> rownum=new ArrayList<Integer>();
               List<Integer> coloumnum=new ArrayList<Integer>();
               List<String> writevalue=new ArrayList<String>();
               //获得被测url的links_in数量
               int links_in=0;
               for(Map.Entry<String,Integer> e: allUrl.entrySet()){
                   String uri=e.getKey();
                   links_in=e.getValue();
                   links_inMap.put(uri,links_in);
                   for(Map.Entry<String, ArrayList<String>> e1: urlRelationship.entrySet() ){
                       ArrayList<String> child=e1.getValue();
                       for(int j=0;j<child.size();j++){
                           RegexUtil ru=new RegexUtil();
                           if(child.get(j).contains(uri)){
                               links_in++;
                               links_inMap.put(uri,links_in);
                           }
                       }
                   }
               }

               //获得其他字段的值
               int k=0;
               for(Map.Entry<String, Integer> e: dt.getAllUrl().entrySet() ){
                   String uri=e.getKey();
                   HashMap<String, String> resultMap=urlTestresult.get(uri);
                   String checkKey="";
                   if(null!=checkKeyInUrl.get(uri)){
                       checkKey=checkKeyInUrl.get(uri);
                   }
                   System.out.println("获取url扫描数据中："+uri);
                   String response_code=resultMap.get("code");
                   String response_msg=resultMap.get("msg");
                   String response_type=resultMap.get("type");
                   String response_size=resultMap.get("size");
                   String isContainKey=resultMap.get("isContainKey");
                   int links_out=0;
                   if(null!=urlRelationship.get(uri)){
                       links_out=urlRelationship.get(uri).size();
                   }
                   for(int j=0;j<9;j++) {
                       rownum.add(k+1);
                       coloumnum.add(j);
                       switch(j){
                           case 0:
                               writevalue.add(uri);break;
                           case 1:
                               writevalue.add(response_code);break;
                           case 2:
                               writevalue.add(response_msg);break;
                           case 3:
                               writevalue.add(checkKey);break;
                           case 4:
                               writevalue.add(isContainKey);break;
                           case 5:
                               writevalue.add(response_type);break;
                           case 6:
                               writevalue.add(response_size);break;
                           case 7:
                               writevalue.add(String.valueOf(links_out));break;
                           case 8:
                               writevalue.add(String.valueOf(links_inMap.get(uri)));break;
                       }
                   }
                   k++;
                   //生成完整html报告
                   sb.append(" <tr bgcolor=#FFFFFF>");
                   sb.append("<td><div style=\"word-wrap:break-word;\" ></div>"+String.valueOf(k)+"</td>");
                   sb.append("<td><div style=\"word-wrap:break-word;\" ></div>"+uri+"</td>");
                   if(!response_code.startsWith("2")&&!response_code.startsWith("3")){
                       sb.append("<td bgColor=red><div style=\"word-wrap:break-word;\" ></div>" + response_code + "</td>");
                   }else {
                       sb.append("<td><div style=\"word-wrap:break-word;\" ></div>" + response_code + "</td>");
                   }
                   sb.append("<td><div style=\"word-wrap:break-word;\" ></div>" + response_msg + "</td>");
                   sb.append("<td><div style=\"word-wrap:break-word;\" ></div>"+checkKey+"</td>");
                   if(isContainKey.equals("否")){
                       sb.append("<td bgColor=red><div style=\"word-wrap:break-word;\" ></div>" + isContainKey + "</td>");
                   }else {
                       sb.append("<td><div style=\"word-wrap:break-word;\" ></div>" + isContainKey + "</td>");
                   }
                   sb.append("<td><div style=\"word-wrap:break-word;\" ></div>"+response_type+"</td>");
                   sb.append("<td><div style=\"word-wrap:break-word;\" ></div>"+response_size+"</td>");
                   sb.append("<td><div style=\"word-wrap:break-word;\" ></div>"+String.valueOf(links_out)+"</td>");
                   sb.append("<td><div style=\"word-wrap:break-word;\" ></div>"+String.valueOf(links_inMap.get(uri))+"</td>");
                   sb.append("</tr>");
               }
               sb.append("</table>\n" +
                       "</div>\n" +
                       "</body>\n" +
                       "</html>");
               String reportContent="";
               reportContent=sb.toString();
               printStream.println(reportContent);//生成html报告
               printStream.close();

               ExcelAnalyze ea=new ExcelAnalyze();
               //System.out.println("rownum:"+rownum+"***coloumnum:"+coloumnum+"***writevalue:"+writevalue);
               try {
                   ea.writeTestResult(scanResultFile_xlsx, rownum, coloumnum, writevalue);//生成xlsx报告
               } catch (InvalidFormatException e) {
                   e.printStackTrace();
               }
          }
    public static  class Worker implements Callable<String> {
        //多线程访问url
        public  Worker(Data dt,String rooturl,String testurl) throws Exception {
            HttpRequestUtil hct=new HttpRequestUtil();
            hct.scanChildurl(dt,rooturl,testurl);
        }
        @Override
        public String call() throws Exception {
            return "Thread Done!";
        }
    }

    public static void main(String[] args){
        /*ArrayList<String> test1=new ArrayList<String>();
        test1.add("sdf1112");
        String test="111";
        for(int i=0;i<test1.size();i++){
            Pattern pattern = Pattern.compile(test);
            Matcher matcher = pattern.matcher(test1.get(i));
            System.out.println(matcher.find());
        }
        HashMap<String,Integer>  links_inMap=new HashMap<String,Integer>();
        HashMap<String,Integer> allurl=new HashMap<String,Integer>();
        allurl.put("111",0);
        allurl.put("222",0);
        allurl.put("333",0);
        allurl.put("444",0);
        ArrayList<String> childurl1=new ArrayList<>();
        childurl1.add("333");
        childurl1.add("444");
        ArrayList<String> childurl2=new ArrayList<>();
        childurl2.add("222");
        childurl2.add("333");
        childurl2.add("444");
        ConcurrentMap<String, ArrayList<String>> urlRelationship=new ConcurrentHashMap<String, ArrayList<String>>();
        urlRelationship.put("222",childurl1);
        urlRelationship.put("111",childurl2);
        //获得被测url的links_in数量
        int links_in=0;
        for(Map.Entry<String,Integer> e: allurl.entrySet()){
            String uri=e.getKey();
            links_in=e.getValue();
            links_inMap.put(uri,links_in);
            for(Map.Entry<String, ArrayList<String>> e1: urlRelationship.entrySet() ){
                ArrayList<String> child=e1.getValue();
                for(int k=0;k<child.size();k++){
                    RegexUtil ru=new RegexUtil();
                    if(ru.getMatcher_common(uri,child.get(k)).size()>0){
                        links_in++;
                        links_inMap.put(uri,links_in);
                    }
                }
            }

        }
        System.out.println(links_inMap);*/
        HashMap<String,Integer>  links_inMap=new HashMap<String,Integer>();
        links_inMap.put("https://www.hao123.com",1);
        System.out.println(links_inMap.get(String.valueOf("https://www.hao123.com")));
    }
}