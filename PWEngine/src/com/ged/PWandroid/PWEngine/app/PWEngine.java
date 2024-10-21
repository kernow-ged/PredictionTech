package com.ged.PWandroid.PWEngine.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;


public class PWEngine extends Activity implements OnGesturePerformedListener {
    private GestureLibrary mLibrary;
    private GestureLibrary nLibrary;
    private static final int OUTPUT_NUMBER = 0;
    private static final int OUTPUT_TEXT = 1;
    private static final int TEST_MODE = 0;
    private static final int WP_MODE = 1;
    private static final int HTTPOUT_MODE = 2;
    private static final int BROADCAST_MODE = 3;    
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private int orient=0; 
    private int output=0;
    private int mode=0;
    private int no_screens=5;  
    private float speed=0.5f;
    private boolean autothump=false;
    private boolean vibrateOnPanic=false;
    private boolean vibOnHTTP=false;  
    private boolean vibOnWPChange=false; 
    private boolean vibOnBroadcast=false;    
  //  private boolean spellcheck=false; 
    private boolean customwallpaper=false;
    private StringBuilder outstr;
    private String myURL;
    private long store;
    private Vibrator v;
    private GestureDetector gestureDetector;
    private String ppath;
    private int dirtouse=0;


	private void buildDirs()
	{
		String path = Environment.getExternalStorageDirectory() + "/PWEngine-images";
		File dir1 = new File(path);
		try{
			  if(dir1.mkdir()) {
			  } else {
			     System.out.println("Directory is not created");
			}}catch(Exception e){}

		ppath=path + "/";
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("ppath", ppath);
        editor.commit();
	}
    
    
    
	public void set_screen(InputStream path) throws IOException {	  	
  		Bitmap bitmap = BitmapFactory.decodeStream(path);
  		int width = bitmap.getWidth();
  		int height = bitmap.getHeight();
  	  Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	 int newWidth, newHeight;
	 if(orient==1){
	 newWidth = display.getWidth();
	 newHeight = display.getHeight();
	 }
	 else{
	 newWidth = display.getHeight(); // because it is oriented differently to 
	 newHeight = display.getWidth(); //	the home screen, which never changes	 
	 }
	 float scaleWidth = ((float) newWidth) / width;
	 float scaleHeight = ((float) newHeight * 0.97f) / height;
	 Matrix matrix = new Matrix();
	 matrix.postScale(scaleWidth, scaleHeight);
	 Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	 width = resizedBitmap.getWidth();
	 height = resizedBitmap.getHeight();
	 Bitmap b = Bitmap.createBitmap((width*no_screens), height, Bitmap.Config.ARGB_8888);
	 Canvas c = new Canvas(b);
	 for(int x=0; x<no_screens; x++)
	 	{
	 	c.drawBitmap (resizedBitmap, (float)(x * width), 0, null);
	 	}
	 WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
	 wallpaperManager.clear();
	 wallpaperManager.setBitmap(b);
	 if(vibOnWPChange){v.vibrate(100);}
	}

    public void act() 	{
     	switch (mode) {
    	case TEST_MODE:
    		Intent mIntent = new Intent(this, TestResult.class);
    		Bundle bundle = new Bundle();
    		bundle.putLong("number", store); 
    		bundle.putBoolean("thump", autothump);
    		bundle.putInt("orient", orient);
    		bundle.putFloat("speed", speed);
    		bundle.putInt("output", output);
     		bundle.putString("outstr", outstr.toString());
    		mIntent.putExtras(bundle);
    		store=0;
    		startActivity(mIntent);
    		outstr.setLength(0);
    		return;

    	case WP_MODE: 
    		if(customwallpaper){
    			String c;
    			if(output==OUTPUT_NUMBER){
    			c = ppath + Long.toString(store);
    			}
    			else {
    			c = ppath + outstr.toString();	
    			}
    	      	String path = c + ".jpg";
        		File file= new File(path);
        		if(file.exists())
        			{       			
        			try {
        				InputStream p = new FileInputStream(path);
        				set_screen(p);
        				} 
        			catch (IOException e) {
        				store=0; outstr.setLength(0); 
        				Intent i = new Intent(Intent.ACTION_MAIN);
                		i.addCategory(Intent.CATEGORY_HOME);
                		startActivity(i);
                		outstr.setLength(0);
        				return;
        					}     			
        			}
        		else 
        			{
        			path = c + ".png";
            		File pfile= new File(path);
            		if(pfile.exists())
            			{          			
            			try {
            				InputStream p = new FileInputStream(path);
            				set_screen(p);
            				} 
            			catch (IOException e) {
             				store=0; outstr.setLength(0); 
            				Intent i = new Intent(Intent.ACTION_MAIN);
            				i.addCategory(Intent.CATEGORY_HOME);
            				startActivity(i);
            				outstr.setLength(0);
            				return;      							
            				
            					}  
            			}
            		else
            			{
        				try {
        					String dpath = ppath + "default.jpg";
        					InputStream p = new FileInputStream(dpath);
               				set_screen(p);
        					}
        				catch (IOException ee){
               				Intent i = new Intent(Intent.ACTION_MAIN);
            				i.addCategory(Intent.CATEGORY_HOME);
            				startActivity(i);
            				outstr.setLength(0);
        					}
            			}
        			}
    			store=0;
        		Intent i = new Intent(Intent.ACTION_MAIN);
        		i.addCategory(Intent.CATEGORY_HOME);
        		startActivity(i);
        		outstr.setLength(0);
        		return;	
    		}
    		else 
    		{
    		if(store>52){store=52;}
    		StringBuilder builder = new StringBuilder("cards/");
    		builder.append(Long.toString(store));
    		builder.append(".png");
    		String s = builder.toString(); 		
     		try {
     			InputStream p = getAssets().open(s);
				set_screen(p);
			} catch (IOException e) {
				store=0; outstr.setLength(0); 
				Intent i = new Intent(Intent.ACTION_MAIN);
        		i.addCategory(Intent.CATEGORY_HOME);
        		startActivity(i);
        		outstr.setLength(0);
				return;
			}
    		store=0;
    		Intent i = new Intent(Intent.ACTION_MAIN);
    		i.addCategory(Intent.CATEGORY_HOME);
    		startActivity(i);
    		outstr.setLength(0);
    		return;
    	}
    	case HTTPOUT_MODE:
     		Thread thread = new Thread(new Runnable(){
    		    //@Override
    		    public void run() {
    		        try {
    		      		String req_string=""; 
    		    		if(output==OUTPUT_NUMBER)req_string = myURL.replaceAll("###", Long.toString(store)); 
    		    		if(output==OUTPUT_TEXT)req_string = myURL.replaceAll("###", outstr.toString().replaceAll(" ", "%20")); 
    		    		HttpParams params = new BasicHttpParams();
    		    		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
    		    		HttpProtocolParams.setContentCharset(params, "utf-8");
    		    		params.setBooleanParameter("http.protocol.expect-continue", false);

    		    		//registers schemes for both http and https
    		    		SchemeRegistry registry = new SchemeRegistry();
    		    		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
    		    		final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
    		    		sslSocketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    		    		registry.register(new Scheme("https", sslSocketFactory, 443));
    		    		ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
    		    		DefaultHttpClient client = new DefaultHttpClient(manager, params);
    		    		HttpGet request = new HttpGet();
    		    		StatusLine status;  
    	   				request.setURI(new URI(req_string));
        				HttpResponse response = client.execute(request);
        				status = response.getStatusLine();
        				if(status!=null) // confirmation!
        					{
        					if(vibOnHTTP) v.vibrate(100);
        					}
        	    		store=0;
        	     		outstr.setLength(0);
     		        } catch (Exception e) {
    		        	store=0; outstr.setLength(0); return;
    		            //e.printStackTrace();
    		        }
    		    }
    		});

    		thread.start();   		
    		return;
    		
    	case BROADCAST_MODE: // almost identical to TEST_MODE really
        		// This is for sending to Tasker which will take over with the number or string
    			// note Tasker will also be where u put the decision to minimise or otherwise steer out of app screen
    		   // i.e. the PWing screen sticks around, but when Tasker picks up on this intent it can do a Home key simulated press 
    		   // itself if required
    			Intent i = new Intent("com.ged.PWandroid.PWEngine.app.USER_ACTION");
        		 Bundle b = new Bundle();
        		 b.putInt("output", output); // 1 for text, 0 for number
        		 b.putLong("number", store); 
        		 b.putString("outstr", outstr.toString());
        		 i.putExtras(b);
        		 sendBroadcast(i);  
        		 outstr.setLength(0);  		
        		 store=0;
    			if(vibOnBroadcast) v.vibrate(150);
    		     return;
    		
    	  default:
    		store=0;
    		outstr.setLength(0);
    		return;
    	}
	}
	
    
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);    
        SharedPreferences settings = getPreferences(MODE_PRIVATE); // 
        orient=settings.getInt("orient", 0);
        output=settings.getInt("output", 0);
        mode=settings.getInt("mode", 0);
        no_screens = settings.getInt("no_screens", 5);
        autothump=settings.getBoolean("autothump", false);
        vibrateOnPanic=settings.getBoolean("vibrateOnPanic", false);
        vibOnHTTP=settings.getBoolean("vibOnHTTP", false);  
        vibOnWPChange=settings.getBoolean("vibOnWPChange", false);
        vibOnBroadcast=settings.getBoolean("vibOnBroadcast", false);        
       // spellcheck=settings.getBoolean("spellcheck", false);
        customwallpaper=settings.getBoolean("customwallpaper", false);
        speed=settings.getFloat("speed", 0.5f);  
        myURL=settings.getString("myURL", getResources().getString(R.string.HTTPOutURL)); 
        //udpath=settings.getString("udpath", "UNDEFINED");  
        dirtouse=settings.getInt("dirtouse", 0);  
        store=0;
        outstr=new StringBuilder();
        setRequestedOrientation (orient);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
       	String path = Environment.getExternalStorageDirectory() + "/PWEngine-images";
    		File file= new File(path);
    				if(file.exists()) {
    				       ppath=path + "/";
    				}
    				else {
    					buildDirs();
    				}

	       mLibrary = GestureLibraries.fromRawResource(this, R.raw.numbergestures2);   
        if (!mLibrary.load()) {
        	finish();
        }
        nLibrary = GestureLibraries.fromRawResource(this, R.raw.lettergestures);
        if (!nLibrary.load()) {
        	finish();
        }
        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        gestures.addOnGesturePerformedListener(this);
	  View mtext =  findViewById(R.id.mtext);
	 
	    gestureDetector = new GestureDetector(new SimpleOnGestureListener() {
	        @Override
	        public void onLongPress(MotionEvent e) {}
	        @Override
	        public boolean onSingleTapConfirmed(MotionEvent e) {
	        	if(output==OUTPUT_TEXT) {outstr.append(" ");}
	        	return true;
	        	}
	        @Override
	        public boolean onDoubleTap(MotionEvent e) {act(); return true;}

	});


		  mtext.setOnTouchListener(new View.OnTouchListener()
		  {
		      public boolean onTouch(View v, MotionEvent event) {
		          gestureDetector.onTouchEvent(event);
		          return false;
		  }}	  ); 
    }
	
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		if(output==OUTPUT_NUMBER)
		{
			ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
			if (predictions.size() > 0) {
				Prediction prediction = predictions.get(0);
				if (prediction.score > 1.0) {
					if ("panic".equals(prediction.name)) 
					{
						store=0;	
						if(vibrateOnPanic){v.vibrate(60);}
						return;
					}
					long digit = Long.parseLong(prediction.name);
					store *=10;
					store +=digit;
				}
			}
		}
		if(output==OUTPUT_TEXT)
		{
			ArrayList<Prediction> predictions = nLibrary.recognize(gesture);
			Prediction prediction = predictions.get(0);
			if (prediction.score > 1.0) {
				if ("panic".equals(prediction.name)) 
				{
				//store=0;	
				outstr.setLength(0);
				if(vibrateOnPanic){v.vibrate(60);}
				return;
				}
				if ("backspace".equals(prediction.name)) 
				{
				//store=0;	
				if(outstr.length() > 0) {outstr.setLength(outstr.length()-1);}
				if(vibrateOnPanic){v.vibrate(60);}
				return;
				}
			outstr.append(prediction.name);	
			}
		}
	}
  
	public void onStart()
	{
		super.onStart();
 
	}
 
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
        if(orient==0)
        menu.findItem(R.id.Landscape).setChecked(true);
    	if(orient==1)
    	menu.findItem(R.id.Portrait).setChecked(true);
        if(output==OUTPUT_NUMBER)
        menu.findItem(R.id.OutputNumber).setChecked(true);
    	if(output==OUTPUT_TEXT)
    	menu.findItem(R.id.OutputText).setChecked(true);
    	if(mode==TEST_MODE)
    	menu.findItem(R.id.Test).setChecked(true);
    	if(mode==WP_MODE)
    	menu.findItem(R.id.Wallpaper).setChecked(true);
    	if(mode==HTTPOUT_MODE)
    	menu.findItem(R.id.HTTPOut).setChecked(true);
    	if(mode==BROADCAST_MODE)
    	menu.findItem(R.id.SendBroadcast).setChecked(true);   	
    	if(autothump)
    	menu.findItem(R.id.AutoThump).setChecked(true);
    	if(vibrateOnPanic)
    	menu.findItem(R.id.VibrateOnPanic).setChecked(true);
       	if(vibOnHTTP)
       	menu.findItem(R.id.VibOnHTTP).setChecked(true);
       	if(vibOnWPChange)
        menu.findItem(R.id.VibOnWPChange).setChecked(true);
       	if(vibOnBroadcast)
        menu.findItem(R.id.VibOnBroadcast).setChecked(true);      	
 //      	if(spellcheck)
 //       menu.findItem(R.id.Spellcheck).setChecked(true);  
       	if(customwallpaper==true)
       	menu.findItem(R.id.UseUserDir).setChecked(true); 	
       	if(customwallpaper==false)
       	menu.findItem(R.id.UsePreset).setChecked(true);       	
       	return super.onCreateOptionsMenu(menu);  
	}


	public boolean onOptionsItemSelected(MenuItem item) {
	      SharedPreferences settings = getPreferences(MODE_PRIVATE);
	      final SharedPreferences.Editor editor = settings.edit();
	      
		  switch (item.getItemId()) {
		  case R.id.Portrait:
		  item.setChecked(true);
		  orient=1;
	      editor.putInt("orient", orient);
	      editor.commit();
		  setRequestedOrientation(1);
		  return true;

		  case R.id.Landscape:
		  item.setChecked(true);
		  orient=0;
	      editor.putInt("orient", orient);
	      editor.commit();
		  setRequestedOrientation(0);      
		  return true;

		  case R.id.OutputNumber:
		  item.setChecked(true);
		  outstr.setLength(0);
		  store=0;
		  output=OUTPUT_NUMBER;
	      editor.putInt("orient", OUTPUT_NUMBER);
	      editor.commit();
		  return true;

		  case R.id.OutputText:
		  item.setChecked(true);
		  outstr.setLength(0);
		  store=0;
		  output=OUTPUT_TEXT;
	      editor.putInt("output", OUTPUT_TEXT);
	      editor.commit();
    		  return true;
		  
		  case R.id.Test:
		  item.setChecked(true);
		  mode=TEST_MODE;
		  return true;

		  case R.id.Wallpaper:
		  item.setChecked(true);
		  mode=WP_MODE;
		  return true;

		  case R.id.HTTPOut:
		  item.setChecked(true);
		  mode=HTTPOUT_MODE;
		  return true;
		  
		  case R.id.SendBroadcast:
		  item.setChecked(true);
		  mode=BROADCAST_MODE;
		  return true;		  

		  case R.id.UsePreset:
		  item.setChecked(true);
		  customwallpaper=false;
		  return true;
		  
		  case R.id.UseUserDir:
		  item.setChecked(true);
		  customwallpaper=true;
		  return true;
		  
		  case R.id.VibrateOnPanic:
		  if (item.isChecked()) {item.setChecked(false); vibrateOnPanic=false;}
		  else {item.setChecked(true); vibrateOnPanic=true;}
		  return true;
		  
		  case R.id.AutoThump:
		  if (item.isChecked()) {item.setChecked(false); autothump=false;}
		  else {item.setChecked(true); autothump=true;}
		  return true; 

		 // case R.id.Spellcheck:
		 // if (item.isChecked()) {item.setChecked(false); spellcheck=false;}
		 // else {item.setChecked(true); spellcheck=true;}
		 // return true;	
		  
		  case R.id.VibOnHTTP:
		  if (item.isChecked()) {item.setChecked(false); vibOnHTTP=false;}
		  else {item.setChecked(true); vibOnHTTP=true;}
		  return true;	
		  
		  case R.id.VibOnWPChange:
		  if (item.isChecked()) {item.setChecked(false); vibOnWPChange=false;}
		  else {item.setChecked(true); vibOnWPChange=true;}
		  return true;	 

		  case R.id.VibOnBroadcast:
		  if (item.isChecked()) {item.setChecked(false); vibOnBroadcast=false;}
		  else {item.setChecked(true); vibOnBroadcast=true;}
		  return true;			  
		  

		  case R.id.SetAutoThumpSpeed:
				AlertDialog.Builder acz = new AlertDialog.Builder(this);
				final SeekBar seek = new SeekBar(this);
				seek.setMax(100);
				int x= (int) (speed * 100.f);
				seek.setProgress(x);
				acz.setView(seek);
				acz.setMessage (getResources().getString(R.string.AutoThumpTip)); 
				acz.setTitle (getResources().getString(R.string.AutoThumpTitle));
					acz.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
							int value = seek.getProgress();	
							speed = ((float)value)/100.f;
							    editor.putFloat("speed", speed); 
							    editor.commit();
							}
						});

				acz.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.cancel();
							}
						});
				acz.show();
		  return true;	  
		  
		  case R.id.Help:
		  Intent mIntent = new Intent(this, ViewWeb.class);
		  startActivity(mIntent);
		  return true;

		  case R.id.SetHTTPOutURL:
				AlertDialog.Builder al = new AlertDialog.Builder(this);
				final EditText in = new EditText(this);
				in.setText(myURL,TextView.BufferType.EDITABLE);
				al.setView(in);
				al.setTitle ((getResources().getString(R.string.HTTPOutTitle)));
				al.setMessage ((getResources().getString(R.string.HTTPOutTip)));
				al.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						myURL = in.getText().toString().trim();
					    editor.putString("myURL", myURL); 
					    editor.commit();
					}
				});
				al.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.cancel();
							}
						});
				al.show();
			  return true;
		  
		  case R.id.SetHomescreen:
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				final EditText input = new EditText(this);
				input.setText(Integer.toString(no_screens),TextView.BufferType.EDITABLE);
				alert.setView(input);
				alert.setMessage ((getResources().getString(R.string.SetHomescreenTip)));
				alert.setTitle ((getResources().getString(R.string.SetHomescreenTitle)));
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = input.getText().toString().trim();
						try{
							no_screens= Integer.parseInt(value);
						}catch(NumberFormatException e){
							no_screens=5;
						}		
						if(no_screens<1) no_screens=1;
						if(no_screens>11) no_screens=11;	
					    editor.putInt("no_screens", no_screens); 
					    editor.commit();
					}
				});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.cancel();
							}
						});
				alert.show();
			  return true;

 
		   default:
		    return super.onOptionsItemSelected(item);
		  }
		}

    protected void onStop(){
        super.onStop();
        store=0;
       SharedPreferences settings = getPreferences(MODE_PRIVATE);
       SharedPreferences.Editor editor = settings.edit();
       editor.putInt("orient", orient);
       editor.putInt("mode", mode);
       editor.putInt("no_screens", no_screens);     
       editor.putBoolean("autothump", autothump);
       editor.putBoolean("vibrateOnPanic", vibrateOnPanic);
       editor.putBoolean("vibOnHTTP", vibOnHTTP);
       editor.putBoolean("vibOnWPChange", vibOnWPChange); 
       editor.putBoolean("vibOnBroadcast", vibOnBroadcast);        
     // editor.putBoolean("spellcheck", spellcheck);
       editor.putBoolean("customwallpaper", customwallpaper);
       editor.putString("myURL", myURL); 
       editor.putFloat("speed", speed);
       editor.putInt("output", output);
       editor.putString("ppath", ppath);
       //editor.putString("udpath", udpath);
       editor.putInt("dirtouse", dirtouse);
       // and store an url
       // Commit the edits!
       editor.commit();
 }
	
    
    
}