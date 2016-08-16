package com.vampirefreaks.wrapper;

import static com.vampirefreaks.wrapper.CommonUtilities.SENDER_ID;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gcm.GCMRegistrar;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity 
{
	private String TAG = "** GCMPushDEMOAndroid**";
	EditText email;
	EditText password;
	Button login;
	String email1,password1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
		
		checkNotNull(SENDER_ID, "SENDER_ID");
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals(""))
		{
			Log.v(TAG, "Registering GCM");
			GCMRegistrar.register(this, SENDER_ID);
			regId = GCMRegistrar.getRegistrationId(this);
		}
		else
		{
			Log.v(TAG, "GCM Already registered");
		}
		Log.v(TAG, "Setting Globals.regId to: " + regId);
		Globals.regId  = regId;
	}
	
	private void checkNotNull(Object reference, String name) {
		if (reference == null)
		{
			throw new NullPointerException(getString(R.string.error_config,	name));
		}
	}
	
	private String GetIMEI() {
		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String IMEINumber = "";
		IMEINumber = telephonyManager.getDeviceId();
		Log.v(TAG, "IMEI Acquired: " + IMEINumber);
		return IMEINumber;

	}
	
}