package com.shake.selector;

import java.io.ByteArrayOutputStream;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>, SensorEventListener {

	//private static final String LOG_TAG = "ShakeSel";
	private static final int DB_LOADER = 0;
	private static final int ACTIVITY_ITEM_ADD = 1000;
	private SensorManager sensorManager;
	private int speed = 3000;
	private int interval = 50;
	private long lastTime = 0;
	private float lastX = 0, lastY = 0, lastZ = 0;
	protected boolean alerted = false;
	private DB mDbHelper;

	protected List<HashMap<String, Object>> arrayList;
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
		LayoutInflater inflater = LayoutInflater.from(this);
		View dlg_view = inflater.inflate(R.layout.dialog_item, null);
		ImageView img = (ImageView) dlg_view.findViewById(R.id.dlg_img);
		Bitmap bitmap = (Bitmap)arrayList.get(idx).get("bitmap");
		if (bitmap != null) {
			img.setImageBitmap(bitmap);
		}
		
		new AlertDialog.Builder(MainActivity.this)
			.setTitle(arrayList.get(idx).get("title").toString())
			//.setMessage(arrayList.get(idx).get("title").toString())
			.setView(dlg_view)
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

	private class DbAddAsyncTask extends AsyncTask<Bundle, Void, Bundle> {

		@Override
		protected Bundle doInBackground(Bundle... params) {
			// TODO Auto-generated method stub
			Bundle bundle = params[0];
			Bitmap bitmap = bundle.getParcelable("ITEM_BMP");
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();    
			bitmap.compress(Bitmap.CompressFormat.PNG,  100 , baos);
			
			Bundle postBundle = new Bundle();
			postBundle.putByteArray("ITEM_PHOTO", baos.toByteArray());
			postBundle.putString("ITEM_NAME", bundle.getString("ITEM_NAME"));
			
			return postBundle;
		}
		
		@Override
		protected void onPostExecute(Bundle bundle) {
			// TODO Auto-generated method stub
			mDbHelper.add(bundle);
		}
	}

	private class ItemloadAsyncTask extends AsyncTask<Cursor, Void, Integer> {

		@Override
		protected Integer doInBackground(Cursor... params) {
			// TODO Auto-generated method stub
			Cursor cursor = params[0];
			if (cursor != null && cursor.getCount() > 0) {
				arrayList.clear();
				while (cursor.moveToNext()) {
					byte [] photo = cursor.getBlob(cursor.getColumnIndex("photo"));
					String name = cursor.getString(cursor.getColumnIndex("item"));

					HashMap<String, Object> map = new  HashMap<String, Object>();
					map.put("title",  name);
					if (photo != null) {
						Bitmap bitmap = BitmapFactory.decodeByteArray(photo,  0 , photo.length);
						map.put("bitmap", bitmap);
					}

					arrayList.add(map);
				}
			}
			
			return 0;
		}
	
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			setListAdapter(itemAdapter);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ACTIVITY_ITEM_ADD:
				Bundle bundle = data.getExtras();
				Bitmap bitmap = bundle.getParcelable("ITEM_BMP");
				String item_name = bundle.getString("ITEM_NAME");
				HashMap<String, Object> map = new  HashMap<String, Object>();
				map.put("title",  item_name);
				map.put("bitmap", bitmap);
				arrayList.add(map);
				setListAdapter(itemAdapter);
				new DbAddAsyncTask().execute(bundle);
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
		new ItemloadAsyncTask().execute(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
}
