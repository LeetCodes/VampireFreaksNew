package com.vampirefreaks.wrapper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		new Thread()
		{
			@Override
			public void run() {
				super.run();
				try
				{
					sleep(3000);
					if(PreferenceUtils.getLoginSession(getApplicationContext())) {
						Intent i=new Intent(Splash.this,web.class);
						startActivity(i);
						finish();
					} else {
						Intent i=new Intent(Splash.this,MainActivity.class);
						startActivity(i);
						finish();
					}
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
