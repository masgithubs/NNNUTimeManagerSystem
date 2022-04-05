package cn.edu.nnnu;

import static cn.edu.tools.Statics.accept;
import static cn.edu.tools.Statics.baseUrl;
import static cn.edu.tools.Statics.outputHtml;
import static cn.edu.tools.Statics.postUserGetHeader;
import static cn.edu.tools.Statics.urlCourseCenter;
import static cn.edu.tools.Statics.urlCourseStart;
import static cn.edu.tools.Statics.userUrl;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.tools.ACache;
import cn.edu.tools.ListData;
import cn.edu.tools.MapDataIntent;
import cn.edu.tools.Sheets;

public class LoginActivity extends Sheets {

    private Map<String, String> cookie;
    private WebView webView;
    private MaterialButton button;

    private ACache aCache;
    private String encodeName, encodePwd;

    private DrawerLayout drawerLayout;
    private CardView cardView;

    private List<String> stringList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();
        aCache = ACache.get(this);
    }


    public void init() {

        cardView = findViewById(R.id.login_view);
        drawerLayout = findViewById(R.id.drawer);

        button = findViewById(R.id.btn_login);
        webView = findViewById(R.id.webview_noview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webView.setWebViewClient(new WebViewClient() {
            boolean isLogin;
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.contentEquals(userUrl)){
                    isLogin = true;
                }else {
                    isLogin = false;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (isLogin){
                    outputHtml();
                }else {
                    Map<String, String> cookiesMap = new HashMap<>();
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookie = cookieManager.getCookie(url);
                    String cookieFormat[] = cookie.split("; ");
                    for (String result : cookieFormat) {
                        String[] split = result.split("=");
                        cookiesMap.put(split[0], split[1]);
                    }
                    aCache.put("cookie", new MapDataIntent(cookiesMap));

                }
            }
        });
        webView.loadUrl(baseUrl);

        webView.addJavascriptInterface(new JsInterface(), "jss");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl(userUrl);
            }
        });
    }


    public void outputHtml() {
        webView.evaluateJavascript(outputHtml, null);
    }




    /**
     * 提供javascript 接口的类
     */
    class JsInterface {

        @JavascriptInterface
        public void result(String acc, String pwd) {
            encodeName = acc;
            encodePwd = pwd;
        }

        @JavascriptInterface
        public void html(String html) {
            Document parse = Jsoup.parse(html);
            Elements elementsByClass = parse.getElementsByClass(getString(R.string.title_right));
            Element element = elementsByClass.get(0);
            Elements select = element.select(getString(R.string.option));
            String semester = select.get(0).text();
//            System.out.println(semester);
            if (stringList == null) {
                stringList = new ArrayList<>();
            }
            stringList.add(semester);
            for (int i=0; i<select.size(); i++) {
                Element ele = select.get(i);
                if (ele.toString().contains("周")) {
                    stringList.add(ele.attributes().get(getString(R.string.value)));
                    new CourseGetter(urlCourseStart+ele.attributes().get(getString(R.string.value))+urlCourseCenter+semester,"week"+(i-2)).start();
                }
            }
            aCache.put("list", new ListData(stringList));
            aCache.put("list_size",stringList.size()-1+"");
            ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage(getString(R.string.importing));
            progressDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                finish();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    class CourseGetter extends Thread {
        private String url;
        private String tempName;

        public CourseGetter(String url,String tempName) {
            this.url = url;
            this.tempName = tempName;
        }

        @Override
        public void run() {
            MapDataIntent mapDataIntent = (MapDataIntent) aCache.getAsObject("cookie");
            Map<String, String> map = mapDataIntent.getMap();
            System.out.println(map.toString());
            Connection connection = Jsoup.connect(url)
                    .header(accept, postUserGetHeader)
                    .userAgent("Mozilla")
                    .method(Connection.Method.GET)
                    .data(map).timeout(3000);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                connection.cookie(entry.getKey(), entry.getValue());
            }
            try{
                Connection.Response execute = connection.execute();
                format(execute.body(),tempName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 解析
     * @param str 解析的内容
     * @param key 缓存的key
     */
    public void format(String str,String key){
        List<List<String>> lists = new ArrayList<>();
        Document parse = Jsoup.parse(str);
        Elements tbody = parse.select("tr");
        for (Element element : tbody){
            List<String> list = new ArrayList<>();
            if (element.toString().contains("td")){
                Elements td = element.select("td");
                for (Element element1 : td){
                    if (element1.toString().contains("<p")){
                        String text = element1.select("p").get(0).attributes().get("title").replaceAll("<br/>","\n");
                        list.add(text);
                    }else {
                        list.add("");
                    }
                }
            }
            lists.add(list);
            list = null;

        }
        aCache.put(key,new ListData<List<String>>(lists));
        lists = null;
    }


}
