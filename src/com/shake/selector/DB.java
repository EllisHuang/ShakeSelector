package com.shake.selector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB{

	private Context mContext = null;
	private DBHelper dbHelper ;
	private SQLiteDatabase db;

	private static final String DATABASE_NAME = "selector.db";
    private static final int DATABASE_VERSION = 1;
	
	public DB(Context context) {
	    this.mContext = context;
	}

	public DB open () throws SQLException {
		dbHelper = new DBHelper(mContext);
		db = dbHelper.getWritableDatabase();
		
	    return this;
	}

	public void close() {
	    dbHelper.close();
	}

	private static class DBHelper extends SQLiteOpenHelper {
	
		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE selector (" 
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT," 
					+ "item TEXT," 
					+ "photo BINARY)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS selector");
            onCreate(db);
		}
	}

	// add an entry
	public long add(String item) {
	    ContentValues args = new ContentValues();
	    args.put("item", item);

	    return db.insert("selector", null, args);
	}
	
	//remove an entry
	public boolean delete(long id) {
	    return db.delete("selector", "_id=" + id, null) > 0;
	}

	// get all entries
	public Cursor getAll() {
	    return db.query("selector", //Which table to Select
	         null, // Which columns to return
	         null, // WHERE clause
	         null, // WHERE arguments
	         null, // GROUP BY clause
	         null, // HAVING clause
	         null //Order-by clause
	         );
	}
}
