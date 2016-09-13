package com.cypien.leroy.utils;

import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.webkit.WebView;

import com.cypien.leroy.activities.CommunityDashboard;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Alex on 8/20/2015.
 */
public class PageLoaderCommunity extends AsyncTask<String,Void,String>{
    private WebView mWebView;
    private CommunityDashboard mainActivity;
    private Map<String,String > cookies;

    public PageLoaderCommunity(CommunityDashboard activity, WebView mWebView){
        this.mWebView=mWebView;
        this.mainActivity=activity;
        cookies=mainActivity.getCookies();
    }
    @Override
    protected String doInBackground(String... params) {
        if(cookies.size()>1){
            try {
                Document doc = Jsoup.connect(params[0]).cookies(cookies).get();
                doc.body().appendElement("style").prepend("#wrapper_et," +
                        "#qr_preview," +
                        "#footer-container," +
                        "#content_container," +
                        ".menu,.friends_wr," +
                        ".see_more_wrapper," +
                        ".cookie_bar," +
                        ".green_left," +
                        ".add_to_contest," +
                        ".above_body," +
                        ".menu menu_custom mobile2," +
                        ".see_more.reply_t.top_button{display: none;} " +
                        "#wrapper_et{visibility: hidden;} " +
                        ".body_wrapper.full_width {margin-top: 0px; position: relative;} " +
                        ".sectThBck{background-color: #ffffff;} " +
                        "div.thread_section_trending{background-color: #ffffff;} " +
                        ".forumbit_post_custom {background: #ffffff; box-shadow: 0 0 7px 2px #888888;}';" +
                        " document.documentElement.appendChild(styleTag); " +
                        "document.getElementsByClassName(\"index_sections_wp\")[0].style.cssText = 'background:" +
                        " #ffffff !important");

                return doc.outerHtml();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                Document doc = Jsoup.connect(params[0]).get();

                doc.body().appendElement("style").prepend("#wrapper_et," +
                        "#qr_preview,#footer-container," +
                        ".menu,.friends_wr,.see_more_wrapper," +
                        ".cookie_bar,.green_left,.add_to_contest," +
                        ".above_body,.menu menu_custom mobile2," +
                        ".see_more.reply_t.top_button{display: none;} " +
                        "#wrapper_et{visibility: hidden;} " +
                        ".body_wrapper.full_width {margin-top: 0px; position: relative;} " +
                        ".sectThBck{background-color: #ffffff;}" +
                        " div.thread_section_trending{background-color: #ffffff;} " +
                        ".forumbit_post_custom {background: #ffffff; box-shadow: 0 0 7px 2px #888888;}'; " +
                        "document.documentElement.appendChild(styleTag); " +
                        "document.getElementsByClassName(\"index_sections_wp\")[0].style.cssText = 'background: " +
                        "#ffffff !important");

                return doc.outerHtml();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    @Override
    protected void onPostExecute(String html) {
      mWebView.loadData(html, "text/html; charset=UTF-8", null);
        //mWebView.loadDataWithBaseURL(null,html,"text/html", "UTF-8",null);
        CookieManager.getInstance().setAcceptCookie(true);
        mainActivity.getHtmlStack().push(html);
    }
}