package com.vampirefreaks.wrapper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class MainActivity extends Activity 
{
	private String TAG = "** GCMPushDEMOAndroid**";
	EditText email;
	EditText password;
	Button login;
	String email1,password1;
	private boolean isReceiverRegistered;
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final String REGISTRATION_COMPLETE = "registrationComplete";

	//Creating a broadcast receiver for gcm registration
	private BroadcastReceiver mRegistrationBroadcastReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Initializing our broadcast receiver
		mRegistrationBroadcastReceiver = new BroadcastReceiver() {

			//When the broadcast received
			//We are sending the broadcast from GCMRegistrationIntentService

			@Override
			public void onReceive(Context context, Intent intent) {
				//If the broadcast has received with success
				//that means device is registered successfully
				if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
					//Getting the registration token from the intent
					String token = intent.getStringExtra("token");
					//Displaying the token as toast
					System.out.println("Registration token:" + token);

					Log.v(TAG, "Setting Globals.regId to: " + token);
					Globals.regId  = token;

					//if the intent is not with success then displaying error messages
				} else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
					System.out.println("GCM registration error!");
				} else {
					System.out.println("Error occurred");
				}
			}
		};


		// Registering BroadcastReceiver
		registerReceiver();

		if (checkPlayServices()) {
			// Start IntentService to register this application with GCM.
			Intent intent = new Intent(this, GCMRegistrationIntentService.class);
			startService(intent);
		}
		
		View titleView = getWindow().findViewById(android.R.id.title);
	    if (titleView != null) {
	    	ViewParent parent = titleView.getParent();
	    	if (parent != null && (parent instanceof View)) {
	    		View parentView = (View)parent;
	    		parentView.setBackgroundColor(Color.BLACK);
	    	}
	    }
	    
	
		email = (EditText) findViewById(R.id.editText1);
		password = (EditText) findViewById(R.id.editText2);
		login = (Button) findViewById(R.id.button1);
		
		if( GetIMEI().equals("A00000223B9F3C") ) {
			email.setText("apocyber");
			password.setText("hubertseales");
		}
		
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		/*
		email.setFilters(new InputFilter[]
		{
			new InputFilter() 
		    {
		        public CharSequence filter(CharSequence src, int start,	int end, Spanned dst, int dstart, int dend) 
		        {
		        	if(src.equals(""))
		        	{ // for backspace
		        		return src;
		        	}
		        	if (src.toString().matches( "[a-z A-Z 0-9 @ . _ -]+"))
		        	{
		        		return src;
		        	}
		        	return "";
		        }
		    }
		});
		password.setFilters(new InputFilter[]
		{
		    new InputFilter() {
		        public CharSequence filter(CharSequence src, int start,
		                int end, Spanned dst, int dstart, int dend)
		        {
		            if(src.equals(""))
		            { // for backspace
		                return src;
		            }
		            if(src.toString().matches("[a-zA-Z 0-9 - _ .]+"))
		            {
		                return src;
		            }
		            return "";
		        }
		    }
		});
		*/
		login.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				email1 = email.getText().toString();
				password1 = password.getText().toString();
				if(email1.length() == 0)
				{
					Toast.makeText(getApplicationContext(), "Please enter username", Toast.LENGTH_SHORT).show();
				}
				else
				{
					if(password1.length() == 0)
					{
						Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
					}else if(email1.length() > 0)
					{
						System.out.println(" name "+email1);
						System.out.println(" pwd "+password1);
						loginData(email1 , password1);
					}
					else
					{
					}
				}
			}
			private void loginData(String email1, String password1) 
			{
				HttpClient httpClient = new DefaultHttpClient();
		        // Creating HTTP Post
				HttpPost httpPost = new HttpPost("http://ws.vampirefreaks.com/loginAuth.php?authUser="+email1+"&authPass="+password1+"&outputFormat=json");
		        try
		        {
		        	HttpResponse response = httpClient.execute(httpPost);
		            // writing response to log
		            HttpEntity entity = response.getEntity();
		            InputStream is = entity.getContent();
		            StringWriter stringWriter = new StringWriter();
		            IOUtils.copy(is, stringWriter, "UTF-8");
		            System.out.println("res "+stringWriter.toString());
		            Log.d("Http Response:", response.toString());
		            try {
		            	JSONObject jsonObject = new JSONObject(stringWriter.toString());
		            	//String status = jsonObject.getString("status");
						String msg = jsonObject.getString("success");
						String details=jsonObject.getString("details");
						System.out.println("SUCCESS: " + msg);
						System.out.println("DETAILS: " + details);
						//String f = "No User Found";
						//String t = "Logged in Successfully";
						if(msg.equals("1")){		
							//startActivity(new Intent(Login.this , Home.class));
							Intent i=new Intent(MainActivity.this,web.class);
							Bundle b=new Bundle();
							b.putString("email", email1);
	                        Bundle b1=new Bundle();
							b1.putString("password",password1);
							i.putExtras(b);
							i.putExtras(b1);
							startActivity(i);
						} else {
							Toast.makeText(getApplicationContext(), "Login failure occured (1)", Toast.LENGTH_SHORT).show();
						}
		            } catch (JSONException e) {
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), "Login failure occured (2)", Toast.LENGTH_SHORT).show();
					}     
		      
		        }
		        catch (ClientProtocolException e) 
		        {
		        	System.out.println(""+e.getMessage());
		        	e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		            System.out.println(""+e.getMessage());	 
		        }
			}
		});

	}

	private String GetIMEI() {
		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String IMEINumber = "";
		IMEINumber = telephonyManager.getDeviceId();
		Log.v(TAG, "IMEI Acquired: " + IMEINumber);
		return IMEINumber;

	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver();
	}

	@Override
	protected void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
		isReceiverRegistered = false;
		super.onPause();
	}

	private void registerReceiver(){
		if(!isReceiverRegistered) {
			LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
					new IntentFilter(REGISTRATION_COMPLETE));
			isReceiverRegistered = true;
		}
	}
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}
	
}