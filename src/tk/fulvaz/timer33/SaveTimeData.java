package tk.fulvaz.timer33;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;
import android.widget.Toast;

public class SaveTimeData {
	private int inHour;
	private int inMinute;
	private int inSecond;
	private int inDay;
	private int inMonth;
	private int inYear;
	
	private int outHour;
	private int outMinute;
	private int outSecond;
	private int outDay;
	private int outMonth;
	private int outYear;
	
	int totalHour = 0;
	int totalMinute = 0;
	int totalSecond = 0;
	
	private Timer timer = new Timer();

	private boolean isDiffDay = false;
	
	private Context context;
	private TimeSQLHelper helper;
	private SQLiteDatabase database;
	
	public 	Time timeNow = new Time();

	/*
	 * 这个类的主要作用是记录每次点击按钮之后的时间,记录下数据,并且写到XML中
	 * 这个类需要新建一个线程处理
	 * */
	public SaveTimeData (Context context, TimeSQLHelper helper) {
		this.context = context;
		this.helper = helper;
		database = this.helper.getWritableDatabase();
		timeNow.setToNow();
		SimpleDateFormat  formater = new SimpleDateFormat("MMdd");
		//每次打开都查询一次total,然后修正total
		String tmp = formater.format(new Date(0, timeNow.month, timeNow.monthDay, 0, 0, 0));
		System.out.println(tmp);		
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.query("day_records", new String[]{"length"}, "date=?", new String[]{tmp}, null, null, null);
		if (cursor.moveToNext()) {
			tmp = cursor.getString(cursor.getColumnIndex("length"));
			System.out.println(tmp);
			//System.out.println(tmp.substring(6, 8));
			totalHour = Integer.parseInt(tmp.substring(0, 2) );
			//System.out.println(totalHour +":" + totalMinute + ":" + totalSecond);
			totalMinute = Integer.parseInt(tmp.substring(3, 5) );
			totalSecond = Integer.parseInt(tmp.substring(6, 8) );
		}
	}
	
	public void saveData(boolean ifStart) {
		timeNow.setToNow();
		if (ifStart == true) { //save the record when timer start
			if (isDiffDay == true) { 
				//每次in都检测一次是否上次记录是否日不一致,上一次不一致这次就将总时间清零
				DifferentDay();
			}
			inHour = timeNow.hour;	
			inMinute = timeNow.minute;
			inSecond = timeNow.second;
			inDay = timeNow.monthDay;
			inMonth = timeNow.month;
			inYear = timeNow.year;
			
			insertIOData(inYear, inMonth, inDay, inHour, inMinute, inSecond, "start");
		}
		else if (ifStart == false) {//save the record when timer stop
			outHour =  timeNow.hour;
			outMinute = timeNow.minute;
			outSecond = timeNow.second;
			outDay = timeNow.monthDay;
			outMonth = timeNow.month;
			outYear = timeNow.year;			
			
			isCheat();
			insertIOData(outYear, outMonth, outDay, outHour, outMinute, outSecond, "stop");

		}//判断是in 还是 out
	} //end of counting()
	
	private void cheat() {
		//提示作弊   清零工作时间
		popUpText("I know what you have done! Here is the result.");
		setToZero();
	}
	
	private void DifferentDay() {
			//日不一致则在下一次in的时候将总工作时间清零
		isDiffDay = false;
		setToZero();
	}	
	
	private void setToZero() {
		totalHour = 0;
		totalMinute = 0;
		totalSecond = 0;		
	}

	private void popUpText(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
	public Date processSecond() {
		if(totalSecond == 60) {
			totalSecond = 0;
			totalMinute++;
		}
		if(totalMinute == 60) {
			totalMinute = 0;
			totalHour++;
		}
		if(totalHour == 24) {
			totalHour = 0;
		}
		Date date = new Date(0, 0, 0, totalHour, totalMinute, totalSecond);
		return date;
	} //end of processSecond
	
	private boolean isCheat() {
		if (outDay != inDay || outMonth!=inMonth || outYear != inYear) { 
			//用年月日防止过分作弊,但是防不了直接改时分秒,下一个版本可以改用网络时间
			//假如年月日和之前有不一致就判断年月是否一致,年月不一致直接判作弊,日不一致超过一天也判作弊,只有超过一天,算正常
			//如果全部都一致,就进行普通的处理
			if(outMonth!=inMonth || outYear != outMonth) {
				cheat();
				return true;
			}
			else {
				isDiffDay = true;
				if (outDay == inDay + 1) {
					popUpText("Too hardworking.");
					return false;
				}
				else {
					cheat();
					return true;
				}
			}
		}
		else {
			return false;
		}
	}
	
	private boolean insertIOData(int y, int m, int d, int h, int min, int s, String io) {
		if (io != "start" && io != "stop") {
			return false;
		}
		SimpleDateFormat dayFormat = new SimpleDateFormat("MMdd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");	
		ContentValues dayRecords = new ContentValues();
		ContentValues ioRecords = new ContentValues();
		
		Date time = new Date(0, 0, 0, h, min, s);
		Date date = new Date(0, m, d, 0, 0, 0);
		ioRecords.put("date", dayFormat.format(date));
		ioRecords.put("time", timeFormat.format(time));
		ioRecords.put("action", io);
		database.insert("io_records", null, ioRecords);
		
		time = new Date(0, 0, 0, totalHour, totalMinute, totalSecond);
		date = new Date(0, inMonth, inDay, 0, 0, 0);
		dayRecords.put("date", dayFormat.format(date));
		dayRecords.put("length", timeFormat.format(time));
		

		
		if (database.update("day_records", dayRecords,  "date=?", new String[]{dayFormat.format(date)}) == 0) {
			database.insert("day_records", null, dayRecords);
		}

		
		return true;
	}//end if insertIODate
	
}//end of class


	

