package tk.fulvaz.timer33;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class TimeSQLHelper extends SQLiteOpenHelper{

	public TimeSQLHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table io_records(_id INTEGER PRIMARY KEY AUTOINCREMENT, date varchar(8), time varchar(10), action varchar(3))");
		db.execSQL("create table day_records(_id INTEGER PRIMARY KEY AUTOINCREMENT, date varchar(8), length varchar(10))");
		System.out.println("create a Database");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		System.out.println("update a Database");
	}
}
