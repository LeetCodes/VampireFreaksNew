package com.vampirefreaks.wrapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Home extends Activity implements OnClickListener
{

	private Button choose,upload;
	protected static final int IMAGE_CAMERA = 1;
	protected static final int IMAGE_GALLERY = 2;
	//private String picturePath;
	private HttpPost httpPost ;
	private TextView picture;
	private Bitmap bitmap;
	private String imagePath;
	private ImageUpload imageUplaod;
	//String email1,password1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		choose=(Button)findViewById(R.id.choosebutton);
		upload=(Button)findViewById(R.id.uploadbutton);
		picture=(TextView)findViewById(R.id.textViewpicture);

		choose.setOnClickListener(this);
		upload.setOnClickListener(this);

		//email1=getIntent().getExtras().getString("email");
		//password1=getIntent().getExtras().getString("password");

		/*choose.setOnClickListener(new OnClickListener() {

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
		});*/
	}

	@Override
	public void onClick(View v) {
		if(v == choose) {
			selectImage();
		} else if(v == upload) {
			if(imagePath != null && imagePath != "") {
				imageUplaod  = new ImageUpload();
				imageUplaod.execute();
			} else {
				Toast.makeText(getApplicationContext(), "Please select image", Toast.LENGTH_SHORT).show();
			}

		}
	}

	private class ImageUpload extends AsyncTask<Void,Void,Void> {

		ProgressDialog nDialog;
		boolean result = false;
		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			nDialog = new ProgressDialog(Home.this);
			nDialog.setMessage("Please wait..");

			nDialog.setIndeterminate(false);
			nDialog.setCancelable(true);
			nDialog.setCanceledOnTouchOutside(false);
			nDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();
			String urlString = "http://uploads.vampirefreaks.com/pictures/picupload_webservices.php?authUser="+PreferenceUtils.getEmail(getApplicationContext())+"&authPass="+PreferenceUtils.getPassword(getApplicationContext())+"&title=hell3&userfile="+imagePath+"&outputFormat=json";
			httpPost = new HttpPost(urlString);

			HttpResponse response;
			try {
				response = httpClient.execute(httpPost);
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				/*StringWriter stringWriter = new StringWriter();
				IOUtils.copy(is, stringWriter, "UTF-8");*/

				String result = convertInputStreamToString(is);
				Log.d("Http Response:", result);


			} catch (ClientProtocolException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void args) {
			// TODO Auto-generated method stub
			super.onPostExecute(args);

			if(result) {
				Toast.makeText(getApplicationContext(), "Picture Upload successfully", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "Picture not Upload", Toast.LENGTH_LONG).show();
			}

			if(nDialog != null) {
				nDialog.dismiss();
			}
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if(imageUplaod != null) {
			imageUplaod.cancel(true);
		}
	}



	public static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = null;
		StringBuffer result = new StringBuffer();
		while ((line = bufferedReader.readLine()) != null) {
			result.append(line);
		}
		//result += line;

		inputStream.close();
		return result.toString();

	}

	private void selectImage() {
		final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
		builder.setTitle("Add Photo!");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {

				if (options[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(android.os.Environment.getExternalStorageDirectory(),"temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, 1);
				} else if (options[item].equals("Choose from Gallery")) {
					Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 2);

				} else if (options[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}


	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 1) {



				try {
					File f = new File(Environment.getExternalStorageDirectory()
							.toString());
					for (File temp : f.listFiles()) {
						if (temp.getName().equals("temp.jpg")) {
							f = temp;
							break;
						}
					}

					imagePath = f.getAbsolutePath();
						/*Uri uri = data.getData();
						String[] filePathColumn = { MediaStore.Images.Thumbnails.DATA};
					    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
					    cursor.moveToFirst();
					    int idx = cursor.getColumnIndex(filePathColumn[0]);
					    //int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
				        imagePath = cursor.getString(idx);*/
					picture.setText(imagePath);

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == 2) {

				Uri selectedImage = data.getData();
				String[] filePath = { MediaStore.Images.Media.DATA };
				Cursor c = getApplicationContext().getContentResolver().query(
						selectedImage, filePath, null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePath[0]);
				imagePath = c.getString(columnIndex);
				c.close();
				picture.setText(imagePath);
			}
		}
	}




	/*class AlertDialogImageAdapter extends BaseAdapter
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
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater infla = getMenuInflater();
		infla.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId() == R.id.item1) {
			PreferenceUtils.clearAllPreference(getApplicationContext());

			Intent i = new Intent(Home.this,MainActivity.class);
			startActivity(i);

		}
		return super.onOptionsItemSelected(item);
	}


}