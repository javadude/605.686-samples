package com.javadude.html;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

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

// LOAD CONTENT FROM THE WEB
//		webView.loadUrl("http://javadude.com");


// LOAD CONTENT FROM A STRING
//		String data = "<html>" +
//				"<body>" +
//				"<p>Hello <b>there</b></p>" +
//				"</body>" +
//				"</html>";
//		webView.loadData(data, "text/html","UTF-8");

// LOAD LOCAL HTML PAGES
		webView.loadUrl("file:///android_asset/html/sample.html");
	}

	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			super.onBackPressed();
		}
	}
}
