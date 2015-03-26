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
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.neu.findme.R;
import com.neu.findme.domain.NoticeBean;
import com.neu.findme.server_web.HttpWebServer;
import com.neu.findme.utils.ChangeDateFormatter;
import com.neu.findme.utils.PhotoCompressionUtil;

/**
 * @author cxm
 *通知中心界面列表适配器
 *防止消息过多，一次性加载。照片只有滑动到列表显示范围内才会下载
 *2015-03-09 20:55:40
 */
public class NoticeListAdapter extends BaseAdapter {
	public static List<NoticeBean> recordBeans = new ArrayList<NoticeBean>();
	protected Context context;
	protected List<ViewHolder> holders = new ArrayList<ViewHolder>();
	protected ViewHolder holder;
	private HttpWebServer webServer = new HttpWebServer();
	private NoticeBean data;
	public NoticeListAdapter(Context context,List<NoticeBean> recordBeans){
		this.context = context;
		NoticeListAdapter.recordBeans = recordBeans;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		holder = null;
		data = recordBeans.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.listitem_notice, new LinearLayout(context));
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
			}else if(!data.isLoading()){
				data.setIsloading(true);
				webServer.downloadPhoto(downloadPhotoRC, data.getPhotoId(), data.getSdUri());
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
	//下载图片
	private RequestCallBack<File> downloadPhotoRC = new RequestCallBack<File>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			data.setIsloading(false);
		}

		@Override
		public void onSuccess(ResponseInfo<File> arg0) {
			// TODO Auto-generated method stub
			notifyDataSetChanged();
		}
	};
}
