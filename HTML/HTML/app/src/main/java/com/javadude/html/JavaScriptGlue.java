package com.javadude.html;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JavaScriptGlue {
	private Context context;

	public JavaScriptGlue(Context context) {
		this.context = context;
	}

	@JavascriptInterface
	public void toast(String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
}
