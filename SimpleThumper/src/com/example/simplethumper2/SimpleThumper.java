package com.example.simplethumper2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class SimpleThumper extends Activity {

	private Timer myTimer;
	//private int number=0;
	private TextView s;
	private Button playButton;
	//private Button stopButton;
	private String myURL;
	private String myURL2;
	private Vibrator v;
	private float speed=0.5f;
	/** Called when the activity is first created. */


protected void buzz(long s)
	{
	float m = 1.0f - speed;
	long l_duration = 100 + (long)(600 * m);
	long	s_duration = 33 + (long)(200 * m);
		long pause_duration = 33 + (long)(200 * m);
		long [] long_bit = {l_duration, pause_duration};
		long [] short_bit = {s_duration, pause_duration};
		long x = s; 
		int r = 63 - Long.numberOfLeadingZeros(x); 
		int vib_length=((r+1) * 2) + 1;
		long [] pattern = new long[vib_length];
		pattern[0] = 0;
		int vptr=1;
		long testbit=1;
		while (testbit <= s){
		if ((s & testbit) == testbit)
			{
			pattern[vptr] = long_bit[0];
			pattern[vptr+1] = long_bit[1];
			}
			else
			{
			pattern[vptr] = short_bit[0];
			pattern[vptr+1] = short_bit[1];
			}
			testbit = testbit * 2;
			vptr+=2;
		}
		  v.vibrate(pattern, -1);
	}

protected void buzz(String s)
{
	Map<String, String> map = new HashMap<String, String>();

	map.put("a", ".-"); map.put("b", "-..."); map.put("c", "-.-."); map.put("d", "-.."); 
	map.put("e", "."); map.put("f", "..-."); map.put("g", "--."); map.put("h", "...."); 
	map.put("i", ".."); map.put("j", ".---"); map.put("k", "-.-"); map.put("l", ".-.."); 
	map.put("m", "--"); map.put("n", "-."); map.put("o", "---"); map.put("p", ".--."); 
	map.put("q", "--.-"); map.put("r", ".-."); map.put("s", "..."); map.put("t", "-"); 
	map.put("u", "..-"); map.put("v", "...-"); map.put("w", ".--"); map.put("x", "-..-"); 
	map.put("y", "-.--"); map.put("z", "--.."); 
	float m = 1.0f - speed;
	long l_duration = 100 + (long)(600 * m);
	long s_duration = 33 + (long)(200 * m);
	//long pause_duration = 33 + (long)(200 * m);

	StringBuilder ms= new StringBuilder();
	for (int x=0; x<s.length(); x++)
		{
		if(s.charAt(x)==' ')
			{
			ms.append(" "); // space; - 2 longs
			}
		
			else
			{
			String p = Character.toString(s.charAt(x));
			ms.append(map.get(p));
			ms.append("|"); // interletter - 2 shorts 
			}
		}

	String k = ms.toString();
	k.trim();
	long[] pattern = new long[(k.length()*2)+1];
	int p=1;
	pattern[0]=0;

	for (int x=0; x<k.length(); x++)
		{
		if(k.charAt(x)==' ') 
			{
			pattern[p] = 0;
			//p +=1;
			pattern[p+1]=l_duration*2;
			}
		if(k.charAt(x)=='|') 
			{
			pattern[p] = 0;
			//p += 1;
			pattern[p+1]=s_duration*2;
			}
		if(k.charAt(x)=='.') 
			{
			pattern[p]=s_duration;
			pattern[p+1]=s_duration;
			}
		if(k.charAt(x)=='-') 
			{
			pattern[p]=l_duration;
			pattern[p+1]=s_duration;
			}
		p += 2;
		}
	//vibrating=true;
	v.vibrate(pattern, -1);	
}
	
protected void thump()
{
    if(myTimer != null) {
        myTimer.cancel();
        myTimer.purge();
        myTimer = null;
	playButton.setText("thumping...");
    }
	InputStream is = null;
	String toThump=null;
   	try{
    		HttpClient httpclient = new DefaultHttpClient();
    		HttpGet httppost = new HttpGet(myURL2);
        	 HttpResponse response = httpclient.execute(httppost);
    		HttpEntity entity = response.getEntity();
    		is = entity.getContent();
    		InputStreamReader reader = new InputStreamReader(is,"iso-8859-1");
		BufferedReader r=new BufferedReader(reader);
		toThump  = r.readLine();
		long z=0;		
		try 
			{
			z=Long.parseLong(toThump);
			buzz(z);
			}
		catch(NumberFormatException e){
			buzz(toThump);
			}
		s.setText(toThump);
  
	   }

catch(Exception e){		}



//start timer again
		        myTimer = new Timer();
     	  		playButton.setText("listening...");
    			myTimer.schedule(new TimerTask() {
    			@Override
    			public void run() {
    				TimerMethod();
    			}

    		}, 0, 2000);
 } // check number of brackets!

	


	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
	       SharedPreferences settings = getPreferences(MODE_PRIVATE);
	       speed=settings.getFloat("speed", 0.5f);
		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		 myURL=getResources().getString(R.string.HTTPOutURL); 
	 myURL2=getResources().getString(R.string.HTTPOutURL2); 
		s=new TextView(this); 
		s=(TextView)findViewById(R.id.output);
		myTimer = new Timer();
		//s.setText(myURL);
	this.playButton = (Button)this.findViewById(R.id.play);
	this.playButton.setOnClickListener(new OnClickListener() {
    	//@Override
     	  	public void onClick(View v) { 
   		     if(myTimer != null) {
		         myTimer.cancel();
		         myTimer.purge();
		         myTimer = null;
		     }
   		        myTimer = new Timer();
     	  		playButton.setText("listening...");
    			myTimer.schedule(new TimerTask() {
    			@Override
    			public void run() {
    				TimerMethod();
    			}

    		}, 0, 2000);
      		}});		
 

	/*this.stopButton = (Button)this.findViewById(R.id.stop);
	this.stopButton.setOnClickListener(new OnClickListener() {
    	//@Override
    	public void onClick(View v) {   		
			//@Override
    		playButton.setText("listen");
		     if(myTimer != null) {
		         myTimer.cancel();
		         myTimer.purge();
		         myTimer = null;
		     }

    		}
  		}); */

	}

	private void TimerMethod()
	{
		//This method is called directly by the timer
		//and runs in the same thread as the timer.

		//We call the method that will work with the UI
		//through the runOnUiThread method.
		this.runOnUiThread(Timer_Tick);
	}

	private Runnable Timer_Tick = new Runnable() {
		public void run() {
		//number++;
		//This method runs in the same thread as the UI.    	       
		//Do something to the UI thread here
		InputStream is = null;
	   	try{
    		HttpClient httpclient = new DefaultHttpClient();
    		HttpGet httppost = new HttpGet(myURL);
        	 HttpResponse response = httpclient.execute(httppost);
    		HttpEntity entity = response.getEntity();
    		is = entity.getContent();
    		InputStreamReader reader = new InputStreamReader(is,"iso-8859-1");
    		int n = reader.read();
    		//s.setText(Integer.toString(n));
    		if(n==49) thump();
	   		}catch(Exception e){
     			//Context context = getApplicationContext();
     			//int duration = Toast.LENGTH_LONG;
     			//Toast toast = Toast.makeText(context, "Error in http connection "+e.toString(), duration);
     			//toast.show();  
	   			s.setText("Error in http connection");
    		}
		}
	};

	protected void onStop()
	{
	super.onStop();
    if(myTimer != null) {
        myTimer.cancel();
        myTimer.purge();
        myTimer = null;
    }
    v.cancel();
    playButton.setText("listen");
    SharedPreferences settings = getPreferences(MODE_PRIVATE);
    final SharedPreferences.Editor editor = settings.edit();
    editor.putFloat("speed", speed); 
    editor.commit();
	}

	protected void onDestroy()
	{
	super.onDestroy();
    if(myTimer != null) {
        myTimer.cancel();
        myTimer.purge();
        myTimer = null;
    }
    v.cancel();
    playButton.setText("listen");
    SharedPreferences settings = getPreferences(MODE_PRIVATE);
    final SharedPreferences.Editor editor = settings.edit();
    editor.putFloat("speed", speed); 
    editor.commit();
	}

	public boolean onCreateOptionsMenu(Menu menu){
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return super.onCreateOptionsMenu(menu);  
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	      SharedPreferences settings = getPreferences(MODE_PRIVATE);
	      final SharedPreferences.Editor editor = settings.edit();
	      switch (item.getItemId()) {
		  case R.id.SetThumpSpeed:
				AlertDialog.Builder acz = new AlertDialog.Builder(this);
				final SeekBar seek = new SeekBar(this);
				seek.setMax(100);
				int x= (int) (speed * 100.f);
				seek.setProgress(x);
				acz.setView(seek);
				acz.setMessage (getResources().getString(R.string.ThumpTip)); 
				acz.setTitle (getResources().getString(R.string.ThumpTitle));
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
	      }
	      return true;
	}
	
	
	
}