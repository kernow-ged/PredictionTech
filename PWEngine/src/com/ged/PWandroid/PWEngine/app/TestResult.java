package com.ged.PWandroid.PWEngine.app;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TestResult extends Activity {

private long number;
private int orient;

private long[] pattern;
private float speed;  
private int vib_length;
private long l_duration;
private long s_duration;
private long pause_duration;
private boolean do_vibrate;
private boolean vibrating;
private static final int OUTPUT_NUMBER = 0;
private static final int OUTPUT_TEXT = 1;
private int output=0;
private String outstr;
private Vibrator v;
private GestureDetector mgestureDetector;


public void go_back()
{
if(vibrating){vibrating=false; v.cancel();}
finish();
}

	
@SuppressWarnings("deprecation")
public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.test);

      Bundle extras = getIntent().getExtras();
      if (extras != null) 
		{
		number = extras.getLong("number");
		orient = extras.getInt("orient");
		do_vibrate = extras.getBoolean("thump");
		speed = extras.getFloat("speed");
		output = extras.getInt("output");
		outstr = extras.getString("outstr");
		}
	else {finish();}
        setRequestedOrientation (orient);
vibrating=false;       
//speed=1; // for now
v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
if(output==OUTPUT_TEXT)
{
TextView s=new TextView(this); 
s=(TextView)findViewById(R.id.ithink); 
s.setText(getResources().getString(R.string.Ithink2));

TextView t=new TextView(this); 
t=(TextView)findViewById(R.id.testview); 
t.setText(outstr);
}

else
{
TextView t=new TextView(this); 
t=(TextView)findViewById(R.id.testview); 
t.setText(Long.toString(number));
}

LinearLayout ll =(LinearLayout)findViewById(R.id.ll);

mgestureDetector = new GestureDetector(new SimpleOnGestureListener() {
    @Override
    public void onLongPress(MotionEvent e) {}
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {return true;}
    @Override
    public boolean onDoubleTap(MotionEvent e) {go_back(); return true;}

});


ll.setOnTouchListener(new View.OnTouchListener()
  {
      public boolean onTouch(View v, MotionEvent event) {
          mgestureDetector.onTouchEvent(event);
          // We do not use the return value of
          // mGestureDetector.onTouchEvent because we will not receive
          // the "up" event if we return false for the "down" event.
      // false is returned so children views can receive touch event too.
          return false;
  }}	  );

}

protected void onResume(){
	super.onResume();
	if(do_vibrate && output==OUTPUT_NUMBER) 
		{
			float m = 1.0f - speed;
			l_duration = 100 + (long)(600 * m);
			s_duration = 33 + (long)(200 * m);
			pause_duration = 33 + (long)(200 * m);
			long [] long_bit = {l_duration, pause_duration};
			long [] short_bit = {s_duration, pause_duration};
			long x = number; 
			int r = 63 - Long.numberOfLeadingZeros(x); 
			vib_length=((r+1) * 2) + 1;
			pattern = new long[vib_length];
			pattern[0] = 0;
			int vptr=1;
			long testbit=1;
			while (testbit <= number){
				if ((number & testbit) == testbit)
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
		  vibrating=true;
		  v.vibrate(pattern, -1);
		} 
	if(do_vibrate && output==OUTPUT_TEXT) 
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
		l_duration = 100 + (long)(600 * m);
		s_duration = 33 + (long)(200 * m);
		pause_duration = 33 + (long)(200 * m);

		StringBuilder ms= new StringBuilder();
		for (int x=0; x<outstr.length(); x++)
			{
			if(outstr.charAt(x)==' ')
				{
				ms.append(" "); 
				}
			
				else
				{
				String s = Character.toString(outstr.charAt(x));
				ms.append(map.get(s));
				ms.append("|"); // interletter - 2 shorts 
				}
			}

		String k = ms.toString();
		k.trim();
		pattern = new long[(k.length()*2)+1];
		int p=1;
		pattern[0]=0;

		for (int x=0; x<k.length(); x++)
			{
			if(k.charAt(x)==' ') 
				{
				pattern[p] = 0;
				pattern[p+1]=l_duration*2;
				}
			if(k.charAt(x)=='|') 
				{
				pattern[p] = 0;
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
		vibrating=true;
		v.vibrate(pattern, -1);	
	}
}

protected void onPause (){
	super.onPause();
	if(vibrating){vibrating=false; v.cancel();}
}

protected void onStop (){
	super.onStop();
	if(vibrating){vibrating=false; v.cancel();}
}
}