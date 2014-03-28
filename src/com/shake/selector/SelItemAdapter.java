package com.shake.selector;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//public class SelItemAdapter extends BaseAdapter{
public class SelItemAdapter extends ArrayAdapter<HashMap<String, Object>>{

	private Context mContext;
	private  List<HashMap<String, Object>> mData;

	public SelItemAdapter(Context context, int resource, List<HashMap<String, Object>> objects) {
		super(context, resource, objects);
		mContext = context;
		mData = objects;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(mContext);
		convertView = inflater.inflate(R.layout.selector_item,  null );
		ImageView img = (ImageView) convertView.findViewById(R.id.img);
		TextView title = (TextView) convertView.findViewById(R.id.title);

		HashMap<String, Object> map = mData.get(position);
		if (map != null) {
			//img.setBackgroundResource((Integer) map.get( "img" ));
			title.setText(map.get( "title" ).toString());
			img.setImageBitmap((Bitmap)map.get("bitmap"));
		}
		
		return convertView;
	}
}
