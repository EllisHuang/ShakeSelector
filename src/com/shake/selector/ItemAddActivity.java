package com.shake.selector;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ItemAddActivity extends Activity {

	private static final int CAMERA_WITH_DATA = 0;
	private static final int PHOTO_PICKED_WITH_DATA = 1;
	ImageView mImg;
	File mFile = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_add);

		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		try {
			mFile = File.createTempFile(
					"tmpImg",	// prefix
					".jpg",		// suffix
					storageDir);	// directory
		    //String currentPhotoPath = "file:" + mFile.getAbsolutePath();
		}
		catch (IOException ex) {
			
		}

		mImg = (ImageView)findViewById(R.id.image_item);
		mImg.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(ItemAddActivity.this) 
				.setTitle("選擇相片") 
				.setItems(new String[] { "拍照", "從相冊中選擇" }, 
					new DialogInterface.OnClickListener() { 
						public void onClick(DialogInterface dialog, int which) { 
							switch (which) { 
								case CAMERA_WITH_DATA: 
									getPicFromCapture();
									break; 
								case PHOTO_PICKED_WITH_DATA:                                  
									getPicFromContent();
									break;
								default:
									break; 
							} 
						} 
				})
				.setNegativeButton("取消", null)
				.show();             
            }
        });
		
		Button button = (Button)findViewById(R.id.item_add);
		button.setOnClickListener(itemAdd);
	}

	private OnClickListener itemAdd = new OnClickListener() {
		public void onClick(View v) {
			EditText fieldItem = (EditText)findViewById(R.id.edit_item_name);
			
			Bundle bundle = new Bundle();
			Bitmap bitmap = ((BitmapDrawable)mImg.getDrawable()).getBitmap();
			if (bitmap != null) {
				bundle.putParcelable("ITEM_BMP", bitmap);
			}
			bundle.putString("ITEM_NAME", fieldItem.getText().toString());
			Intent intent = new Intent();
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			ItemAddActivity.this.finish();
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case CAMERA_WITH_DATA:
					Intent intent = new Intent("com.android.camera.action.CROP");
					intent.setDataAndType(Uri.fromFile(mFile), "image/*");
					intent.putExtra("crop", "true");
					intent.putExtra("aspectX", 1);
					intent.putExtra("aspectY", 1);
					intent.putExtra("outputX", Dp2Px(this,150));
					intent.putExtra("outputY", Dp2Px(this,150));
					intent.putExtra("return-data", true);
					startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
					break;
				case PHOTO_PICKED_WITH_DATA:
					Bitmap bitmap;
					bitmap = data.getParcelableExtra("data");;
					if (bitmap != null) {
						mImg.setImageBitmap(bitmap);
					}
					break;
			}
			
		}
	}

	public int Dp2Px(Context context, float dp) {  
		final float scale = context.getResources().getDisplayMetrics().density;  
		return (int) (dp * scale + 0.5f);  
	} 
	
	private void getPicFromCapture() { 
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
        if (takePicIntent.resolveActivity(getPackageManager()) != null) {
        	if (mFile != null) {
        		takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(mFile));
            	startActivityForResult(takePicIntent, CAMERA_WITH_DATA);            
            }
        }
	}
	
	private void getPicFromContent(){
		try {  
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);  
			intent.setType("image/*");  
			intent.putExtra("crop", "true");  
			intent.putExtra("aspectX", 1);  
			intent.putExtra("aspectY", 1);  
			intent.putExtra("outputX", Dp2Px(this,150));  
			intent.putExtra("outputY", Dp2Px(this,150));  
			intent.putExtra("return-data", true);  
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);  
		} catch (Exception e) {  
			Toast.makeText(this, "錯誤",Toast.LENGTH_LONG).show();  
		}  
	}
}
