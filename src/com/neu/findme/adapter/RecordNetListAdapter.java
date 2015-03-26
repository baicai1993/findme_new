package com.neu.findme.adapter;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.neu.findme.R;
import com.neu.findme.domain.NetRecordBean;
import com.neu.findme.utils.ChangeDateFormatter;
import com.neu.findme.utils.PhotoCompressionUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author cxm
 *网络记录列表适配器
 *2015-03-09 20:56:30
 */
public class RecordNetListAdapter extends BaseAdapter {
	public static List<NetRecordBean> recordBeans = new ArrayList<NetRecordBean>();
	private Context context;
	private List<ViewHolder> holders = new ArrayList<ViewHolder>();
	private ViewHolder holder;
	public RecordNetListAdapter(Context context,List<NetRecordBean> recordBeans){
		this.context = context;
		RecordNetListAdapter.recordBeans = recordBeans;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		holder = null;
		NetRecordBean data = recordBeans.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.listitem_net, new LinearLayout(context));
			holder.showPic = (ImageView) convertView.findViewById(R.id.showPic);
			holder.tTakeTime = (TextView) convertView.findViewById(R.id.tTakeTime);
			holder.tLocation = (TextView) convertView.findViewById(R.id.tLocation);
			holder.uploadTime = (TextView) convertView.findViewById(R.id.tUploadTime);
			holder.tTitle = (TextView) convertView.findViewById(R.id.tTitle);
			holders.add(holder);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//展示文本数据
		try {
			holder.tTakeTime.setText(ChangeDateFormatter.changeDateFormat1(data.getTakeTime()));
			holder.uploadTime.setText(ChangeDateFormatter.changeDateFormat1(data.getUpLoadedTime()));
			holder.tLocation.setText(data.getLocation());
			holder.tTitle.setText(data.getTitle());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 展示图片
		if (data.getSdUri() != null) {
			if((new File(data.getSdUri().toString().substring(data.getSdUri().toString().indexOf(":")+1)).exists())){
				String uriStr = data.getSdUri().toString();
				Bitmap bitmap =PhotoCompressionUtil.ListViewBitmap(uriStr.substring(uriStr.indexOf(":")+1));
				holder.showPic.setImageBitmap(bitmap);
			}else {
				holder.showPic.setImageResource(R.drawable.ic_loading);
			}
		}else {
			holder.showPic.setImageResource(R.drawable.ic_loading);
		}
		return convertView;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return recordBeans.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return recordBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	static class ViewHolder {
		ImageView showPic;
		TextView tTakeTime;
		TextView tLocation;
		TextView uploadTime;
		TextView tTitle;
	}

}
