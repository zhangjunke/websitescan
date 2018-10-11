package util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Data.Data;
public class RegexUtil {
    public static ArrayList<String> getMatcher_common(String regex, String source) {
        ArrayList<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        HttpRequestUtil hct=new HttpRequestUtil();
        for (int j = 0; j <= matcher.groupCount(); j++){
            while (matcher.find()) {
                String res=matcher.group(j);
                result.add(res);
                }
            }
        return result;
    }

    public static ArrayList<String> getMatcher(Data dt,String rooturl,String includeUrlRegex, String source) {
            ArrayList<String> result = new ArrayList<String>();
            Pattern pattern = Pattern.compile(includeUrlRegex);
            Matcher matcher = pattern.matcher(source);
            RegexUtil ru=new RegexUtil();
            for (int j = 0; j <= matcher.groupCount(); j++){
                while (matcher.find()) {
                    String res=matcher.group(j);
                    //把提取到的url放入allUrl集合中
                    res=res.replace("href=","");
                    res=res.replace("src=","");
                    res=res.replace("url(","");
                    res=res.replace(")","");
                    res=res.replace(";","");
                    res=res.replace("\"","");
                    if(!res.contains("http")&&res.startsWith("//")){//eg，src="/invest
                        res="http:"+res;
                    }
                    if(!res.contains("http")&&res.startsWith("./")){//eg，src="./invest
                        res=res.replace("./","/");
                        res=rooturl+res;
                    }
                    if(!res.contains("http")&&res.startsWith("../")){//eg，src="../invest
                        res=res.replace("../","/");
                        res=rooturl+res;
                    }
                    if(!res.contains("http")&&res.startsWith("/")){//eg，src="/invest
                        res=rooturl+res;
                    }
                    if(res.contains(".com//")){
                        res=res.replace(".com//",".com/");
                    }
                    if(res.contains(".cn//")){
                        res=res.replace(".cn//",".cn/");
                    }
                    String validUriRegex = "(https?|http)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";//合法url正则
                    if(ru.getMatcher_common(validUriRegex, res).size() != 0&&!dt.getIgnoreUrlList().contains(res)){
                        result.add(res);
                        dt.setAllUrl(res,0);
                    }
        }
     }
            return result;
    }

}
