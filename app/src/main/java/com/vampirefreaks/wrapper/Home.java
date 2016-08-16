package com.vampirefreaks.wrapper;

import java.io.FileNotFoundException;
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



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends Activity 
{

	Button choose,upload;
	protected static final int IMAGE_CAMERA = 1;
	protected static final int IMAGE_GALLERY = 2;
	String picturePath;
	HttpPost httpPost ;
	TextView picture;
	//String email1,password1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		choose=(Button)findViewById(R.id.choosebutton);
		upload=(Button)findViewById(R.id.uploadbutton);
		picture=(TextView)findViewById(R.id.textViewpicture);
		//email1=getIntent().getExtras().getString("email");
		//password1=getIntent().getExtras().getString("password");

		choose.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
				builder.setIcon(R.drawable.ic_launcher);
				builder.setTitle("Choose Image");
				AlertDialogImageAdapter adapter = new AlertDialogImageAdapter();
				builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which)
					{
						if(which == 0)
							imageChooseFromGallery();
						if(which == 1)
							imageChooseFromCamera();
					}
					private void imageChooseFromCamera() {
						Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(intent, IMAGE_CAMERA);	
					}
					private void imageChooseFromGallery() {
						Intent intent = new  Intent(Intent.ACTION_PICK);
						intent.setType("image/*");
						startActivityForResult(intent, IMAGE_GALLERY);
					}
		
				});    
	   
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}
	
	class AlertDialogImageAdapter extends BaseAdapter
	{
		final CharSequence[] items = {"Choose from Gallery", "take a photo"};
		final int[] img = {R.drawable.ic_launcher , R.drawable.ic_launcher};
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return img.length;
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			  View view = convertView;
			LayoutInflater inflater = getLayoutInflater();
			view  = inflater.inflate(R.layout.alert_row, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.imageView1);
			TextView textView = (TextView) view.findViewById(R.id.textView1);
			imageView.setImageResource(img[position]);
			textView.setText(items[position]);
			return view;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != RESULT_CANCELED)
		{
			if (requestCode == IMAGE_GALLERY) {
				Uri uri = data.getData(); 
				String[] filePathColumn = { MediaStore.Images.Thumbnails.DATA};
		        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
		        cursor.moveToFirst();
		        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		        picturePath = cursor.getString(columnIndex);
		        cursor.close();
		        System.out.println("image path "+picturePath);
		        picture.setText(picturePath);
		        upload.setOnClickListener(new OnClickListener() {
		        	@Override
		        	public void onClick(View v) {
		        		// TODO Auto-generated method stub
		        		HttpClient httpClient = new DefaultHttpClient();
		        		String urlString = "http://uploads.vampirefreaks.com/pictures/picupload_webservices.php?authUser=apocyber&authPass=hubertseales&title=hell3&userfile="+picturePath+"&outputFormat=json";
		        		httpPost = new HttpPost(urlString);
		        		//  try {
		        		// ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		        		//  
		        		//friendID is an array of friends ID   
		        		//  for (int i=0;i<con.length;i++){
		        		//   nameValuePairs.add(new BasicNameValuePair("contactsphone[]", String.valueOf(con[i])));
		        		//   
		        		//   
		        		//  }
		        		//  httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        		//  
		        		//   } catch (IOException e) {
		        		// TODO Auto-generated catch block
		        		//   }
		        		HttpResponse response;
		        		try {
		        			response = httpClient.execute(httpPost);
		        			HttpEntity entity = response.getEntity();
		        			InputStream is = entity.getContent();
		        			StringWriter stringWriter = new StringWriter();
		        			IOUtils.copy(is, stringWriter, "UTF-8");
		        			System.out.println("res "+stringWriter.toString());
		        			System.out.println(response);
		        			Log.d("Http Response:", response.toString());
		        			Toast.makeText(getApplicationContext(),"Upload Successful",Toast.LENGTH_LONG).show();		           
		        		} catch (ClientProtocolException e) {
		        			// TODO Auto-generated catch block
		        			e.printStackTrace();
		        		} catch (IOException e) {
		        		    // TODO Auto-generated catch block
		        		    e.printStackTrace();
		        		}
		        	}
		        });
			}	
		}}

	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
	    // Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
	    BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);
	    // The new size we want to scale to
	    final int REQUIRED_SIZE = 100;
	    // Find the correct scale value. It should be the power of 2.
	    int width_tmp = o.outWidth, height_tmp = o.outHeight;
	    int scale = 1;
	    while (true) {
	    	if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
	        {
	        	break;
	        }
	        width_tmp /= 2;
	        height_tmp /= 2;
	        scale *= 2;
	    }
	    // Decode with inSampleSize
	    BitmapFactory.Options o2 = new BitmapFactory.Options();
	    o2.inSampleSize = scale;
	    return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater infla=getMenuInflater();
		infla.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==R.id.item1){
			Intent i=new Intent(Home.this,MainActivity.class);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}
}