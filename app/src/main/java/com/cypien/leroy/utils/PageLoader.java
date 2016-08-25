package com.cypien.leroy.utils;

import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.webkit.WebView;

import com.cypien.leroy.activities.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Alex on 8/20/2015.
 */
public class PageLoader extends AsyncTask<String,Void,String>{
    private WebView mWebView;
    private MainActivity mainActivity;
    private Map<String,String > cookies;

    public PageLoader(MainActivity activity, WebView mWebView){
        this.mWebView=mWebView;
        this.mainActivity=activity;
        cookies=mainActivity.getCookies();
    }
    @Override
    protected String doInBackground(String... params) {
        if(cookies.size()>1){
            try {
                Document doc = Jsoup.connect(params[0]).cookies(cookies).get();
                doc.body().appendElement("style").prepend(
                        "#wrapper_et,\n"+
                        "#qr_preview, \n" +
                        "#footer-container, \n" +
                        ".menu,\n" +
                        ".friends_wr,\n" +
//                        ".user_middle_tabs,\n" +
                        ".see_more_wrapper,\n" +
                            ".add_to_contest,\n" +
                        ".cookie_bar,\n" +
                        ".green_left,\n" +
                        ".carousel,\n" +
                        ".see_more.reply_t.top_button\n"+
//                        "ol"+
                                "{\n" +
                        "    display: none;\n" +
                        "}  \n" +
                        "#wrapper_et," +
                        ".home_icon{\n" +
                        "\tvisibility: hidden;\n" +
                        "}" +
                        "\n");
                return doc.outerHtml();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                Document doc = Jsoup.connect(params[0]).get();
                doc.body().appendElement("style").prepend(
                    "#wrapper_et,\n" +
                        "#qr_preview, \n" +
                        "#footer-container, \n" +
                        "#content_container, \n" +
                        ".menu,\n" +
                        ".friends_wr,\n" +
//                        ".user_middle_tabs,\n" +
                        ".see_more_wrapper,\n" +
                        ".add_to_contest,\n" +
                        ".cookie_bar,\n" +
                        ".green_left,\n" +
                        ".carousel,\n" +
                        ".see_more.reply_t.top_button\n" +
//                        "ol"+
                        "{\n" +
                        "    display: none;\n" +
                        "}  \n" +
                        "#wrapper_et{\n" +
                        "\tvisibility: hidden;\n" +
                        "}\n");
              return doc.outerHtml();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    @Override
    protected void onPostExecute(String html) {
        mWebView.loadDataWithBaseURL(null,html,"text/html", "UTF-8",null);
        CookieManager.getInstance().setAcceptCookie(true);
        mainActivity.getHtmlStack().push(html);
    }
}
