package com.pasc.lib.picture.pictureSelect.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.pasc.lib.picture.R;
import com.pasc.lib.picture.pictureSelect.bean.PhotoUpImageBucket;
import com.pasc.lib.picture.pictureSelect.adapter.AlbumsAdapter;
import com.pasc.lib.picture.takephoto.uitl.TConstant;
import com.pasc.lib.picture.util.PhotoUpAlbumHelper;
import com.pasc.lib.picture.util.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

public class AlbumsActivity extends AppCompatActivity {
	public static final String IS_HEAD = "ishead";
	private int canSelect = 0;
	private static final int REQUEST_CODE_PICTURE_SELECT = 100;
	private GridView gridView;
	private TextView tvTitleRight;
	private AlbumsAdapter adapter;
	private PhotoUpAlbumHelper photoUpAlbumHelper;
	private List<PhotoUpImageBucket> list;
	private boolean isHead;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StatusBarUtils.setStatusBarLightMode(AlbumsActivity.this, true, true);
		setContentView(R.layout.picture_activity_albums_gridview);
		Intent in = getIntent();
		if (in != null) {
			canSelect = in.getIntExtra(TConstant.INTENT_EXTRA_LIMIT, 0);
			isHead = in.getBooleanExtra(IS_HEAD, false);
		}
		init();
		loadData();
		onItemClick();
		setListener();

	}

	public static void actionStart(Activity context, int requestCode, int canSelect,boolean ishead) {
		Intent intent = new Intent (context, AlbumsActivity.class);
		intent.putExtra(TConstant.INTENT_EXTRA_LIMIT, canSelect);
		intent.putExtra(IS_HEAD, ishead);
		context.startActivityForResult(intent, requestCode);
	}



	private void setListener() {
		tvTitleRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void init(){
		tvTitleRight = findViewById(R.id.tv_title_right);
		gridView = (GridView) findViewById(R.id.album_gridv);
		adapter = new AlbumsAdapter(AlbumsActivity.this);
		gridView.setAdapter(adapter);
	}
	
	private void loadData(){
		photoUpAlbumHelper = PhotoUpAlbumHelper.getHelper();
		photoUpAlbumHelper.init(AlbumsActivity.this);
		photoUpAlbumHelper.setGetAlbumList(new PhotoUpAlbumHelper.GetAlbumList() {
			@Override
			public void getAlbumList(List<PhotoUpImageBucket> list) {
				adapter.setArrayList(list);
				adapter.notifyDataSetChanged();
				AlbumsActivity.this.list = list;
			}
		});
		photoUpAlbumHelper.execute(false);
	}
	
	private void onItemClick(){
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				AlbumsSelectActivity.actionStart(AlbumsActivity.this,REQUEST_CODE_PICTURE_SELECT,canSelect,list.get(position),isHead);
			}
		});
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_PICTURE_SELECT && data != null) {
			ArrayList<String> pictures = data.getStringArrayListExtra("images");
			Intent intent = new Intent ();
			intent.putStringArrayListExtra (TConstant.INTENT_EXTRA_IMAGES, pictures);
			setResult(RESULT_OK, intent);
			finish();
		}
	}
}
