package Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Data {
    private ConcurrentMap<String, ArrayList<String>> urlRelationship = new ConcurrentHashMap<String, ArrayList<String>>();
    private ConcurrentMap<String, HashMap<String, String>> urlTestresult = new ConcurrentHashMap<String, HashMap<String, String>>();
    private ArrayList<String> ignoreUrlList=new ArrayList<>();
    private HashMap<String,String > checkKeyInUrl=new HashMap<String,String >();
    private ConcurrentMap<String, Integer> allUrl = new ConcurrentHashMap<String, Integer>();
    private ArrayList<Integer> threadStatus=new ArrayList<>();
    private ConcurrentMap<String, Integer> urlScanStatus = new ConcurrentHashMap<String, Integer>();

    public ConcurrentMap<String, Integer> getUrlScanStatus() {
        return urlScanStatus;
    }

    public void setUrlScanStatus(String url,Integer i) {
        this.urlScanStatus.put(url,i);
    }


    public ArrayList<Integer> getThreadStatus() {
        return threadStatus;
    }

    public void setThreadStatus(Integer i) {
        this.threadStatus.add(i );
    }

    public ConcurrentMap<String, Integer> getAllUrl() {
        return allUrl;
    }

    public void setAllUrl(String key,Integer value) {
        this.allUrl.put(key,value);
    }


    public ArrayList<String> getIgnoreUrlList() {
        return ignoreUrlList;
    }

    public void setIgnoreUrlList(String ignoreurl) {
        this.ignoreUrlList.add(ignoreurl);
    }

    public HashMap<String, String> getCheckKeyInUrl() {
        return checkKeyInUrl;
    }

    public void setCheckKeyInUrl(HashMap<String, String> checkKeyInUrl) {
        this.checkKeyInUrl = checkKeyInUrl;
    }


    public ConcurrentMap<String, ArrayList<String>> getUrlRelationship() {
        return urlRelationship;
    }

    public void setUrlRelationship(String key, ArrayList<String> value) {
        this.urlRelationship.put(key,value);
    }

    public ConcurrentMap<String, HashMap<String, String>> getUrlTestresult() {
        return urlTestresult;
    }

    public void setUrlTestresult(String key, HashMap<String, String> value) {
        this.urlTestresult.put(key,value);
    }


}
