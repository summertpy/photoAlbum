package com.example.photoalbum;
import com.example.photoalbum.AlbumDAO;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.Menu;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private final int PICKER = 1;
	private int currentPic = 0;
	private Gallery picGallery;
	private PicAdapter imgAdapt;
	private EditText etEvent;
	private EditText etAvenue;
	private EditText etDate;
	private EditText etWithWho;
	private Button edit;
	private ImageButton takePhoto;
	private boolean isclick = false, editable = false;
	private int x;
	private AlbumDAO albumdao = new AlbumDAO(this);
	final static int cameraData = 0;
	Bitmap bmp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("asd");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		etEvent = (EditText)findViewById(R.id.editevent);
		etAvenue = (EditText)findViewById(R.id.editplace);
		etDate = (EditText)findViewById(R.id.editdate);
		etWithWho = (EditText)findViewById(R.id.editwho);
		//get the gallery view
		picGallery = (Gallery) findViewById(R.id.gallery);
		//create a new adapter
		imgAdapt = new PicAdapter(this);
		//set the gallery adapter
		picGallery.setAdapter(imgAdapt);
		edit = (Button) findViewById(R.id.editORsave);
		edit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// change from edit to save button, details in R&W mode
				if(editable)
					if (!isclick){
						edit.setText("Save");
						etEvent.setEnabled(true);
						etAvenue.setEnabled(true);
						etDate.setEnabled(true);
						etWithWho.setEnabled(true);
						isclick = true;	
					}else{
						albumdao.open();
						albumdao.updateData((String)edit.getTag(), etEvent.getText().toString(), etAvenue.getText().toString(), etDate.getText().toString(), etWithWho.getText().toString());
						albumdao.close();
				
						edit.setText("Edit");
						etEvent.setEnabled(false);
						etAvenue.setEnabled(false);
						etDate.setEnabled(false);
						etWithWho.setEnabled(false);
						isclick = false;
					}
			}
		});
		//set long click listener for each gallery thumbnail item
		picGallery.setOnItemLongClickListener(new OnItemLongClickListener() {
		    //handle long clicks
		    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
		    	showBigImage(position);
		    	return true;
		    }
		});
		//set the click listener for each item in the thumbnail gallery,display details
		picGallery.setOnItemClickListener(new OnItemClickListener() {
		    //handle clicks
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		    	editable = true;
		    	edit.setTag(imgAdapt.getPath(position));

				albumdao.open();
				String d = albumdao.getData("");
				albumdao.close();
				String [] t = d.split(";");
				etEvent.setText(t[0]);
				etAvenue.setText(t[1]);
				etDate.setText(t[2]);
				etWithWho.setText(t[3]);
		    }
		});
		takePhoto = (ImageButton)findViewById(R.id.imageButton1);
		takePhoto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//initiate camera, take photo then save file in directory
				Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(i, cameraData);
				File f;
				String dirc = "ntg";
				try {
					f = createImageFile();
					dirc = f.getAbsolutePath();
					i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					albumdao.open();
					albumdao.insertData(dirc);
					albumdao.close();
					Log.d("saved, img dirc: " , dirc);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("Fail to save image, img dirc: " , dirc);
				}
			}
		});
	}
	
	public void showBigImage(int position){
    	Dialog d = new Dialog(this);
    	d.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	ImageView iv = new ImageView(this);
    	iv.setImageBitmap(imgAdapt.getPic(position));
    	d.setContentView(iv);
    	d.show();
	}
	
	public class PicAdapter extends BaseAdapter {
		//use the default gallery background image
		int defaultItemBackground;
		//gallery context
		private Context galleryContext;
		//array to store bitmaps to display
		private ArrayList<Bitmap> imageBitmaps;
		//placeholder bitmap for empty spaces in gallery
		Bitmap [] placeholder;
		ArrayList<String> al;
		
		public PicAdapter(Context c) {
		    //instantiate context
		    galleryContext = c;
		    
			albumdao.open();
			al = albumdao.getAllImgPath();
		    
		    imageBitmaps  = new ArrayList<Bitmap>();
		    placeholder  = new Bitmap[al.size()];

		    for(int i = 0; i < al.size(); i++){
		    	placeholder[i] = BitmapFactory.decodeFile(al.get(i));
		    }

		    for(int i=0; i < al.size(); i++)
		        if(placeholder[i] != null)
		        	imageBitmaps.add(placeholder[i]);
		        else{
		        	albumdao.deleteData(al.get(i));
		        }

			albumdao.close();		
			
		    TypedArray styleAttrs = galleryContext.obtainStyledAttributes(R.styleable.PicGallery);

		    defaultItemBackground = styleAttrs.getResourceId(
		        R.styleable.PicGallery_android_galleryItemBackground, 0);

		    styleAttrs.recycle();
		}
		
		//return number of data items i.e. bitmap images
		public int getCount() {
		    return imageBitmaps.size();
		}
		
		public String getPath(int position){
			if(al.size() >= position)
				return al.get(position);
			else
				return null;
		}
		
		//return item at specified position
		public Object getItem(int position) {
		    return position;
		}
		
		//return item ID at specified position
		public long getItemId(int position) {
		    return position;
		}
		
		//get view specifies layout and display options for each thumbnail in the gallery
		public View getView(int position, View convertView, ViewGroup parent) {
		    //create the view
		    ImageView imageView = new ImageView(galleryContext);
		    //specify the bitmap at this position in the array
		    imageView.setImageBitmap(imageBitmaps.get(position));
		    //set layout options
		    imageView.setLayoutParams(new Gallery.LayoutParams(300, 200));
		    //scale type within view area
		    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		    //set default gallery item background
		    imageView.setBackgroundResource(defaultItemBackground);
		    //return the view
		    return imageView;
		}
		
		//helper method to add a bitmap to the gallery when the user chooses one
		public void addPic(Bitmap newPic)
		{
		    //set at currently selected index
		    imageBitmaps.add(newPic);
		    notifyDataSetChanged();
		}
		
		//return bitmap at specified position for larger display
		public Bitmap getPic(int posn)
		{
		    //return bitmap at posn index
		    return imageBitmaps.get(posn);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK){
			Bundle extras = data.getExtras();
			bmp = (Bitmap)extras.get("data");
			imgAdapt.addPic(bmp);
		}
	}
	
	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = new File(
					  Environment.getExternalStoragePublicDirectory(
							    Environment.DIRECTORY_PICTURES
							  ), 
							  "CameraSample"
							);

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}
	
	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = 
	        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "IMG_" + timeStamp + "_";
	    File image = File.createTempFile(imageFileName, ".jpg", getAlbumDir());
	    return image;
	}
	/*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
		    //check if we are returning from picture selection
		    if (requestCode == PICKER) {
		        //import the image
		    	//the returned picture URI
		    	Uri pickedUri = data.getData();
		    	//declare the bitmap
		    	Bitmap pic = null;
		    	//declare the path string
		    	String imgPath = "";
		    	//retrieve the string using media data
		    	String[] medData = { MediaStore.Images.Media.DATA };
		    	//query the data
		    	Cursor picCursor = managedQuery(pickedUri, medData, null, null, null);
		    	if(picCursor!=null)
		    	{
		    	    //get the path string
		    	    int index = picCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    	    picCursor.moveToFirst();
		    	    imgPath = picCursor.getString(index);
		    	}
		    	else
		    	    imgPath = pickedUri.getPath();
		    	//if we have a new URI attempt to decode the image bitmap
		    	if(pickedUri!=null) {
		    		//set the width and height we want to use as maximum display
		    		int targetWidth = 600;
		    		int targetHeight = 400;
		    		//create bitmap options to calculate and use sample size
		    		BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
		    		//first decode image dimensions only - not the image bitmap itself
		    		bmpOptions.inJustDecodeBounds = true;
		    		BitmapFactory.decodeFile(imgPath, bmpOptions);
		    		//image width and height before sampling
		    		int currHeight = bmpOptions.outHeight;
		    		int currWidth = bmpOptions.outWidth;
		    		//variable to store new sample size
		    		int sampleSize = 1;
		    		//calculate the sample size if the existing size is larger than target size
		    		if (currHeight>targetHeight || currWidth>targetWidth)
		    		{
		    		    //use either width or height
		    		    if (currWidth>currHeight)
		    		        sampleSize = Math.round((float)currHeight/(float)targetHeight);
		    		    else
		    		        sampleSize = Math.round((float)currWidth/(float)targetWidth);
		    		}
		    		//use the new sample size
		    		bmpOptions.inSampleSize = sampleSize;
		    		//now decode the bitmap using sample options
		    		bmpOptions.inJustDecodeBounds = false;
		    		//get the file as a bitmap
		    		pic = BitmapFactory.decodeFile(imgPath, bmpOptions);
		    		//pass bitmap to ImageAdapter to add to array
		    		imgAdapt.addPic(pic);
		    		//redraw the gallery thumbnails to reflect the new addition
		    		picGallery.setAdapter(imgAdapt);
		    	}
		    }
		}
		//superclass method
		super.onActivityResult(requestCode, resultCode, data);

	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
