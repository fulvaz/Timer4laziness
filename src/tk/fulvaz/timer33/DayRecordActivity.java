package tk.fulvaz.timer33;



import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class DayRecordActivity extends ListActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_history);
		super.onCreate(savedInstanceState);
		TimeSQLHelper dbHelper = new TimeSQLHelper(DayRecordActivity.this, "time_data_db", null, 1);		
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor cursor = database.query("day_records", new String[]{"_id","date", "length"}, null, null,  null, null, null);//查询
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item_records, cursor, new String[]{"date", "length"}, new int[] {R.id.column1, R.id.column2});
		ListView mView = (ListView)findViewById(android.R.id.list);
		TextView empty = (TextView)findViewById(R.id.emptyView);
		mView.setAdapter(adapter);
		mView.setEmptyView(empty);
	}



}
