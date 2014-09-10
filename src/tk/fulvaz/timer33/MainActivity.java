package tk.fulvaz.timer33;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private SaveTimeData std;
	private Handler handler;
	private Button IOButton;
	public TextView time;
	private TextView text;
	Timer  timer = new Timer();
	TimerTask timerTask = new TTask();
	SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd");
	TimeSQLHelper helper = new TimeSQLHelper(MainActivity.this, "time_data_db", null, 1);

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//initial
		IOButton = (Button)findViewById(R.id.IOButton);
		IOButton.setOnClickListener(new IOButtonListener());
		
		time = (TextView)findViewById(R.id.totalTime);
		handler = new UIRefreshHandler();
		std = new SaveTimeData(this, helper);

		time.setText(timeFormat.format(std.processSecond()));
	}
	
	public class UIRefreshHandler extends Handler{
		//刷新UI显示,将秒换算成分 时
		@Override
		public void handleMessage(Message msg) {
			System.out.println(Thread.currentThread().getName()  + " " + handler.hashCode() + " " + std.totalSecond);
			std.totalSecond++;

			time.setText(timeFormat.format(std.processSecond()));
		}
	}
	
	public class IOButtonListener implements OnClickListener {
		private boolean isOut = true;

		@Override
		public void onClick(View v) {		
			if (isOut == true) {
				//in
				timer = new Timer();
				timerTask = new TTask();
				System.out.println(timer.hashCode());
				System.out.println(timerTask.hashCode());	
				IOButton.setText(R.string.when_start);
				isOut = false;
				Toast.makeText(getApplicationContext(), "You have been start.", Toast.LENGTH_SHORT).show();
				std.saveData(true);
				timer.schedule( timerTask, 1000, 1000);
			}
			else if(isOut == false) {
				//out
				isOut = true;
				IOButton.setText(R.string.before_start);		
				Toast.makeText(getApplicationContext(), "You have been stop.", Toast.LENGTH_SHORT).show();				
				std.saveData(false);
				//停止计时
				timer.cancel(); 
				timer = null;
				timerTask = null;
			}
		}
	}
			
	class TTask extends TimerTask {
		@Override
		public void run() {
			//totalSecond++;
			//不直接在这里处理数据的原因是为了线程安全
			Message msg = handler.obtainMessage();
			handler.sendMessage(msg);
		}
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id) {
		case R.id.action_history: {
			Intent intent1 = new Intent();
			intent1.setClass(MainActivity.this, DayRecordActivity.class);
			startActivity(intent1);
		}break;
		case R.id.action_done: {
			Intent intent2 = new Intent();
			intent2.putExtra("package com.example.timer.Today", dateFormat.format(new Date(0, std.timeNow.month, std.timeNow.monthDay)));			
			intent2.setClass(MainActivity.this, TodayRecordActivity.class);
			startActivity(intent2);			
		}break;
		case R.id.action_about: {
			Intent intent3 = new Intent();	
			intent3.setClass(MainActivity.this, AboutActivity.class);
			startActivity(intent3);				
		}
		}
		return super.onOptionsItemSelected(item);
	}
}
