package com.shake.selector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ItemAddActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_add);

		Button button = (Button)findViewById(R.id.item_add);
		button.setOnClickListener(itemAdd);
	}

	private OnClickListener itemAdd = new OnClickListener() {
		public void onClick(View v) {
			EditText fieldItem = (EditText)findViewById(R.id.edit_item_name);
			
			Bundle bundle = new Bundle();
			bundle.putString("ITEM_NAME", fieldItem.getText().toString());
			Intent intent = new Intent();
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			ItemAddActivity.this.finish();
		}
	};

}
