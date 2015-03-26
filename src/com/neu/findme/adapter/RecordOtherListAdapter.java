package com.neu.findme.adapter;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neu.findme.R;
import com.neu.findme.domain.OtherRecordBean;
import com.neu.findme.utils.ChangeDateFormatter;
import com.neu.findme.utils.PhotoCompressionUtil;

/**
 * @author cxm
 *监控界面列表适配器
 *2015-03-09 20:56:42
 */
public class RecordOtherListAdapter extends BaseAdapter {
	public static List<OtherRecordBean> recordBeans = new ArrayList<OtherRecordBean>();
	protected Context context;
	protected List<ViewHolder> holders = new ArrayList<ViewHolder>();
	protected ViewHolder holder;
	public RecordOtherListAdapter(Context context,List<OtherRecordBean> recordBeans){
		this.context = context;
		RecordOtherListAdapter.recordBeans = recordBeans;
	}
	public RecordOtherListAdapter(){}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		holder = null;
		OtherRecordBean data = recordBeans.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.listitem_other, new LinearLayout(context));
			holder.showPic = (ImageView) convertView.findViewById(R.id.showPic);
			holder.tTakeTime = (TextView) convertView.findViewById(R.id.tTakeTime);
			holder.tLocation = (TextView) convertView.findViewById(R.id.tLocation);
			holder.photoTaker = (TextView) convertView.findViewById(R.id.tPhotographer);
			holder.tTitle = (TextView) convertView.findViewById(R.id.tTitle);
			holders.add(holder);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//展示文本数据
		try {
			holder.tTakeTime.setText(ChangeDateFormatter.changeDateFormat1(data.getTakeTime()));
			holder.photoTaker.setText(data.getUserName());
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
	protected static class ViewHolder {
		ImageView showPic;
		TextView tTakeTime;
		TextView tLocation;
		TextView photoTaker;
		TextView tTitle;
	}
}
