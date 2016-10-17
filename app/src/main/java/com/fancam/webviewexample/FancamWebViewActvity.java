package com.fancam.webviewexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

/**
 * Created by nealshail on 14/10/2016.
 */

public class FancamWebViewActvity extends Activity {

    private WebView mWebView;           // main web view
    private WebView mWebviewPop;        // for handling facebook / twitter / login popups
    private FrameLayout mContainer;
    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FancamWebViewActivity", "onCreate");

        setContentView(R.layout.activity_webview);
        mWebView = (WebView)findViewById(R.id.webview_display);
        mContainer = (FrameLayout) findViewById(R.id.webview_frame);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(mWebView, true);
        }

        String url = getIntent().getStringExtra("url");

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.setWebViewClient(new UriWebViewClient(url));
        mWebView.setWebChromeClient(new UriWebChromeClient(url));

        mWebView.loadUrl(url);  // load the web page

        mContext=this.getApplicationContext();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private class UriWebViewClient extends WebViewClient {

        String mOriginUrl;

        public UriWebViewClient(String originUrl) {
            mOriginUrl = (originUrl == null || !originUrl.contains("?")) ? originUrl : originUrl.substring(0, originUrl.indexOf("?") );
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String host = Uri.parse(url).getHost();
            Log.d("FancamWebViewActivity", "shouldOverrideUrlLoading: " + url);

            if( url.startsWith("http:") || url.startsWith("https:") ) {
                // the sites below are handled by us:

                // facebook popups (via the mWebviewPop)
                if (    host.equals("m.facebook.com") ||
                        host.equals("www.facebook.com") ||
                        host.equals("facebook.com")) {
                    return false; // pass this on so that ChromeClient will handle in onCreateWindow
                }

                // links within this fancam
                if (url.contains(mOriginUrl)) { // from me
                    return false;
                }

                // another fancam?
                if (host.contains("fancam")){
                    return false;
                }

                // Otherwise, the link is not for an external web page.
                // Send to native web browser so we are sure it will be handled correctly.
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }

            // use native calling
            else if (url.startsWith("tel:")) {
                Intent tel = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(tel);
                return true;
            }

            // use native email
            else if (url.startsWith("mailto:")) {
                Intent mail = new Intent(Intent.ACTION_SEND);
                mail.setType("application/octet-stream");
                String address = new String(url.replace("mailto:" , "")) ;
                mail.putExtra(Intent.EXTRA_EMAIL, new String[]{ address });
                mail.putExtra(Intent.EXTRA_SUBJECT, "");
                mail.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(mail);
                return true;
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("FancamWebViewActivity", "onPageFinished: " + url);
            super.onPageFinished(view, url);
        }
    }

    private class UriWebChromeClient extends WebChromeClient {

        String mOriginUrl;

        public UriWebChromeClient(String originUrl) {
            mOriginUrl = originUrl;
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            Log.d("FancamWebViewActivity", "onCreateWindow:" + view.getUrl());

            mWebviewPop = new WebView(mContext);
            mWebviewPop.setVerticalScrollBarEnabled(false);
            mWebviewPop.setHorizontalScrollBarEnabled(false);
            mWebviewPop.setWebViewClient(new UriWebViewClient(mOriginUrl));
            mWebviewPop.setWebChromeClient(new UriWebChromeClient(mOriginUrl));
            mWebviewPop.getSettings().setJavaScriptEnabled(true);
            mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mContainer.addView(mWebviewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPop);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            Log.d("FancamWebViewActivity", "onCloseWindow: " + window.getUrl());

            if(mWebviewPop!=null)
            {
                mWebviewPop.setVisibility(View.GONE);
                mContainer.removeView(mWebviewPop);
                mWebviewPop=null;
            }
        }

    }

}