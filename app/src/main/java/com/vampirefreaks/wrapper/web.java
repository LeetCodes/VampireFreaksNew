package com.vampirefreaks.wrapper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.util.EncodingUtils;

public class web extends Activity
{
	WebView web;
	String email1,password1;
	private String TAG = "** GCMPushDEMOAndroid**";
	private ValueCallback<Uri>	mUploadMessage;
	private final static int FILECHOOSER_RESULTCODE	= 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Log.v(TAG, "onCreate");
		
		setContentView(R.layout.web);
		web=(WebView)findViewById(R.id.web);
		//	web.setWebViewClient(new WebViewClient());
		//	web.loadUrl("http://ws.vampirefreaks.com/loginAuth.php?authUser=jet420&authPass=test123&outputFormat=json&redirect_me=1");

		/**
		 *  Previous code to get email & password
		 */

		/*email1    = getIntent().getExtras().getString("email");
		password1 = getIntent().getExtras().getString("password");*/

		email1      = PreferenceUtils.getEmail(getApplicationContext());
		password1   = PreferenceUtils.getPassword(getApplicationContext());
		//web.getSettings().setJavaScriptEnabled(true);
		//web.getSettings().setPluginsEnabled(true);

		SharedPreferences pref = getApplicationContext().getSharedPreferences("VampireFreaks", MODE_PRIVATE);

        String registrationId = pref.getString("regId", "");

//		String url="http://ws.vampirefreaks.com/loginAuth.php?authUser="+email1+"&authPass="+password1+"&outputFormat=json&os=android&redirect_me=1&regID="+Globals.regId;
        String url="http://vampirefreaks.com/loginAuth.php?authUser="+email1+"&authPass="+password1+"&outputFormat=json&os=android&redirect_me=1&regID="+registrationId;
		Log.v(TAG, "Calling URL: " + url);
		//	String postData = regId;
		//	System.out.println(postData);
		
//		web.postUrl(url, EncodingUtils.getBytes(Globals.regId, "base64"));
        web.postUrl(url, EncodingUtils.getBytes(registrationId, "base64"));
		web.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(final WebView view,final String url)
			{
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						view.getSettings().setJavaScriptEnabled(true);
						view.getSettings().setDomStorageEnabled(true);
						//view.getSettings().setPluginsEnabled(true); // Not worked after api level 19
						view.loadUrl(url);
						view.setWebChromeClient(new CustomWebChromeClient());
					}
				});


				if( url.contains("logout") ) // Example of the logout URL: http://m.vampirefreaks.com/login.php?logout=1&c=6e0a3bc1
		        {
					PreferenceUtils.clearAllPreference(getApplicationContext());
					Intent i=new Intent(web.this,MainActivity.class);
					startActivity(i);
					finish();
				}
				if(url.equals("http://uploads.vampirefreaks.com/mobile/picupload.php"))
				{
					Intent i = new Intent(web.this,Home.class);
					startActivity(i);
				}      
		        return false;
		    }
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
	     return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, "onPause");
		//GCMRegistrar.unregister(this);
	}
	
    @Override
    protected void onResume() {
        super.onResume();
		Log.v(TAG, "onResume");
		if( Globals.isNotificationWaiting == true) {
			Log.v(TAG, "NOTIFICATION WAITING");
			Globals.isNotificationWaiting = false;
			try {
				web.loadUrl(Globals.notificationUrl);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
    }
    
    @Override
    protected void onStop() {
        super.onStop();
		Log.v(TAG, "onStop");
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (web.canGoBack() == true) {
				web.goBack();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage) return;
			if (resultCode != RESULT_OK) {
				mUploadMessage.onReceiveValue(null);
				return;
			}
			Uri result = intent.getData();
			if (result == null) {
				mUploadMessage.onReceiveValue(null);
				return;
			}
			mUploadMessage.onReceiveValue(result);
			return;
		}
	}
    
    protected class CustomWebChromeClient extends WebChromeClient {

		// For Android > 4.1
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
			openFileChooser(uploadMsg);
		}

		// Andorid 3.0 +
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
			openFileChooser(uploadMsg);
		}

		// Android 3.0
		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
			mUploadMessage = uploadMsg;
			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
			i.setType("*/*");
			startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
		}

	}

	
}
