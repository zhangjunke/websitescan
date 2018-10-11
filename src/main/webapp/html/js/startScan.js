var rooturl="";
var ignoreUrl="";
var checkKeyInUrl="";
var urlPrefix="";
var startScanUrl="/startScan";
var loadingUrl=urlPrefix+"/html/loading.html"
function startScan() {
    rooturl=document.getElementById('rooturl').value;
    var ignoreUrl1=document.getElementById('ignore1').value;
    var ignoreUrl2=document.getElementById('ignore2').value;
    var ignoreUrl3=document.getElementById('ignore3').value;
    var checkUrl1=document.getElementById('url1').value;
    var checkUrl2=document.getElementById('url2').value;
    var checkUrl3=document.getElementById('url3').value;
    var checkKey1=document.getElementById('key1').value;
    var checkKey2=document.getElementById('key2').value;
    var checkKey3=document.getElementById('key3').value;
    var reg=/^([hH][tT]{2}[pP]:\/\/|[hH][tT]{2}[pP][sS]:\/\/)(([A-Za-z0-9-~]+)\.)+([A-Za-z0-9-~\/])+$/;
    if(!reg.test(rooturl)){
        alert("url必须是以http://或https://开头的合法网址！");
    }
    else{
        //window.open(loadingUrl);
        var loadingcenter=document.getElementById('loading-center-absolute');
        var loading=document.getElementById('loading');
        loadingcenter.style.display="";//显示loading样式
        loading.style.display="";//显示loading样式

        ignoreUrl=ignoreUrl1+"&"+ignoreUrl2+"&"+ignoreUrl3;
        checkKeyInUrl=checkUrl1+"="+checkKey1+"&"+checkUrl2+"="+checkKey2+"&"+checkUrl3+"="+checkKey3;
        var data = {'rootUrl': rooturl, 'ignoreUrl': ignoreUrl, 'checkKeyInUrl': checkKeyInUrl};
        var url=urlPrefix+startScanUrl;
        $.post(url, data, function (list1) {
            var data = JSON.parse(list1);
            if (data.result.toString()=="-1") {
                alert("url必须是以http://或https://开头的合法网址！");
            } else {
                var timestamp=data.timeStamp;
                var url=urlPrefix+"/TestReport/"+timestamp+".html";
                location.href=url;
            }
        })
    }
}