package com.pasc.lib.picture.pictureSelect.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.pasc.lib.picture.R;
import com.pasc.lib.picture.pictureSelect.bean.PhotoUpImageBucket;
import com.pasc.lib.picture.util.GlideUtil;

import java.util.ArrayList;
import java.util.List;

public class AlbumsAdapter extends BaseAdapter {

	private final Context context;
	private List<PhotoUpImageBucket> arrayList;
	private LayoutInflater layoutInflater;
	private String TAG = AlbumsAdapter.class.getSimpleName();

	public AlbumsAdapter(Context context){
		this.context = context;
		layoutInflater = LayoutInflater.from(this.context);
		arrayList = new ArrayList<PhotoUpImageBucket>();//初始化集合

	};
	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = layoutInflater.inflate(R.layout.picture_ablums_adapter_item, parent, false);
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.count = (TextView) convertView.findViewById(R.id.count);
			convertView.setTag(holder);
		}else {
			holder = (Holder) convertView.getTag();
		}
		holder.count.setText(""+arrayList.get(position).getCount());
		holder.name.setText(arrayList.get(position).getBucketName());
		GlideUtil.loadImage (context,holder.image, arrayList.get(position).getImageList().get(0).getPath(),
				R.drawable.picture_bg_default_image_color,R.drawable.picture_bg_default_image_color);
		return convertView;
	}

	class Holder{
		ImageView image;
		TextView name;
		TextView count;
	}

	public void setArrayList(List<PhotoUpImageBucket> arrayList) {
		this.arrayList = arrayList;
	}
}
