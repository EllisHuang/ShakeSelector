package com.shake.selector;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

public class DBCursorLoader extends CursorLoader{

	private DB mDbHelper;
    public DBCursorLoader(Context context, DB dbHelper) {
        super(context);
        mDbHelper = dbHelper;
    }

	@Override
	public Cursor loadInBackground() {
		// TODO Auto-generated method stub
		return mDbHelper.getAll();
	}
}


