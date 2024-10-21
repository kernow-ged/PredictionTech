package com.ged.PWandroid.PWEngine.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class ViewWeb extends Activity {  
        @Override  
        public void onCreate(Bundle savedInstanceState) {  
            super.onCreate(savedInstanceState);
            setContentView(R.layout.help);  
            WebView wv;  
            wv = (WebView) findViewById(R.id.webview);  
            wv.loadUrl("file:///android_asset/help.html");   
        }  
    }