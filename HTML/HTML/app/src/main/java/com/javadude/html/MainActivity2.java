package com.javadude.html;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity2 extends AppCompatActivity {

	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		webView = (WebView) findViewById(R.id.webView);
		assert webView != null;

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Uri uri = Uri.parse(url);
				if (uri.getHost().toLowerCase().endsWith("javadude.com")) {
					return false;
				}
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
				return true;
			}
		});

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				Log.w("OOPS! Alert!", message);
				return super.onJsAlert(view, url, message, result);
			}
		});
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new JavaScriptGlue(this), "Android");
		webView.loadUrl("file:///android_asset/html/js-test1.html");
	}

	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_message:
				webView.loadUrl("javascript:setMessage('We made it back into the JS!')");
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
