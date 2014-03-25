package com.shake.selector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>, SensorEventListener {

	private static final int ACTIVITY_ITEM_ADD = 1000;
	private SensorManager sensorManager;
	private int speed = 3000;
	private int interval = 50;
	private long lastTime = 0;
	private float lastX = 0, lastY = 0, lastZ = 0;
	protected boolean alerted = false;
	private DB mDbHelper;
	private static final int DB_LOADER = 0;

	private  List<HashMap<String, Object>> arrayList;
	private SelItemAdapter itemAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbHelper = new DB(this).open();
		getLoaderManager().initLoader(DB_LOADER, null, this);
		
		arrayList = new ArrayList<HashMap<String, Object>>();
		itemAdapter =  new SelItemAdapter(this, R.layout.selector_item, arrayList);
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mDbHelper.close();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
     
		sensorManager.registerListener(
				this, 
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
				SensorManager.SENSOR_DELAY_GAME);
    }

	@Override
	protected void onPause() {
		super.onPause();

		sensorManager.unregisterListener(this);
	}

	@Override  
	public void onAccuracyChanged(Sensor arg0, int arg1) {  
	}  
	  
	@Override  
	public void onSensorChanged(SensorEvent Event)   
	{  
		long nowTime = System.currentTimeMillis();  
		if ((nowTime - lastTime) < interval)  
			return;  

		lastTime = nowTime;

		float nowX = Event.values[0];  
		float nowY = Event.values[1];  
		float nowZ = Event.values[2];  

		float deltaX = nowX - lastX;  
		float deltaY = nowY - lastY;  
		float deltaZ = nowZ - lastZ;  

		lastX = nowX;  
		lastY = nowY;  
		lastZ = nowZ;  

		double NowSpeed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)/interval * 10000;  
		if (NowSpeed >= speed && !alerted)  
		{
			alerted = true;
			Vibrator myVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
			myVibrator.vibrate(1000);
			openOptionDialog();
		}  
	}
	
	void openOptionDialog()
	{
		Random ran = new Random();
		int idx = ran.nextInt(arrayList.size());
		new AlertDialog.Builder(MainActivity.this)
			.setTitle(R.string.result_title)
			//.setMessage(arrayList.get(idx))
			.setMessage(arrayList.get(idx).get( "title" ).toString())
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					alerted = false;
				}
			})
			.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId())
		{
		case R.id.menu_add:
			Intent itent = new Intent(MainActivity.this, ItemAddActivity.class);
			startActivityForResult(itent, ACTIVITY_ITEM_ADD);
			break;
		case R.id.menu_close:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ACTIVITY_ITEM_ADD:
				Bundle bundle = data.getExtras();
				String item_name = bundle.getString("ITEM_NAME");
				//arrayList.add(item_name);
				HashMap<String, Object> map = new  HashMap<String, Object>();
				map.put( "title" ,  item_name);
				arrayList.add(map);
				//setListAdapter(arrayAdapter);
				setListAdapter(itemAdapter);
				mDbHelper.add(item_name);
				break;
			}
		}
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		switch (id) {
			case DB_LOADER:
				return new DBCursorLoader(getApplicationContext(), mDbHelper);
		}
		
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub
		if (cursor != null && cursor.getCount() > 0) {
			arrayList.clear();
			while (cursor.moveToNext()) {
				//arrayList.add(cursor.getString( 1 ));
				HashMap<String, Object> map = new  HashMap<String, Object>();
				map.put( "title" ,  cursor.getString( 1 ));
				arrayList.add(map);
			}
			
			//setListAdapter(arrayAdapter);
			setListAdapter(itemAdapter);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
}
