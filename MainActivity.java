package com.siliconx.studymaterials;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    private WebView myWeb;


    LottieAnimationView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loading = findViewById(R.id.loading);

        myWeb = findViewById(R.id.myWeb);


        // Check network connectivity before loading the web page
        if (isNetworkAvailable()) {
            initializeWebView();
        }


    }


    private void initializeWebView() {
        WebSettings webSettings = myWeb.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript

        myWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loading.setVisibility(View.INVISIBLE); // Hide loading animation when page finishes loading
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (isGoogleDriveLink(url)) {
                    // Launch external browser with the Google Drive link
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                    return true; // Indicate that the URL loading is handled externally
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            private boolean isGoogleDriveLink(String url) {
                // Check if the URL matches a Google Drive link pattern
                return url.startsWith("https://drive.google.com/");
            }
        });


        myWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    loading.setVisibility(View.INVISIBLE); // Hide loading animation when progress reaches 100%
                } else {
                    loading.setVisibility(View.VISIBLE); // Show loading animation when progress is not yet 100%
                }
            }
        });

        // Set up download listener
        myWeb.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                // Create a request for MediaStore
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_TITLE, URLUtil.guessFileName(url, contentDisposition, mimeType));
                startActivityForResult(intent, 123); // Request code 123 is arbitrary; you can use any integer
            }
        });

        myWeb.loadUrl("https://ali-sadik.github.io/StudyMaterialsKUETECE/");
    }

    @Override
    public void onBackPressed() {
        if (myWeb.canGoBack()) {
            myWeb.goBack(); // Go back in WebView's history if possible
        } else {
            super.onBackPressed(); // If not, let the default behavior execute (exit the app)
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                // Start download using DownloadManager
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(myWeb.getUrl()));
                request.setDestinationUri(uri);
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                if (downloadManager != null) {
                    downloadManager.enqueue(request);
                } else {
                    Toast.makeText(MainActivity.this, "DownloadManager is not available", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}
