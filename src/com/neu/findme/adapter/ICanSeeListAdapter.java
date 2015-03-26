package com.neu.findme.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.neu.findme.R;
import com.neu.findme.activity.PersonalInfoActivity;
import com.neu.findme.activity.SomeoneInfoActivity;
import com.neu.findme.domain.UserBean;
import com.neu.findme.server_web.HttpWebServer;
import com.neu.findme.utils.FileUtil;
import com.neu.findme.utils.PhotoCompressionUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author cxm “我可见的”列表适配器 2015-03-09 20:52:59
 */
public class ICanSeeListAdapter extends BaseAdapter {
	public static List<UserBean> userBeans = new ArrayList<UserBean>();
	private Context context;
	private List<ViewHolder> holders = new ArrayList<ViewHolder>();
	private ViewHolder holder;
	private HttpWebServer webServer = new HttpWebServer();
	private UserBean data;

	public ICanSeeListAdapter(Context context, List<UserBean> list) {
		super();
		this.context = context;
		ICanSeeListAdapter.userBeans = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userBeans.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return userBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		holder = null;
		data = userBeans.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listitem_icansee, new RelativeLayout(context));
			// 单个listview各属性添加值
			holder.chooseImage = (ImageView) convertView
					.findViewById(R.id.choose_image);
			holder.chooseName = (TextView) convertView
					.findViewById(R.id.choose_name);
			holders.add(holder);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 给控件设置内容
		holder.chooseName.setText(data.getName());
		// 设置头像
		if (!data.getHeadimage().equals("")) {
			String headimageSdUri = FileUtil.HEADIMAGE_PATH
					+ data.getHeadimage();

			if ((new File(headimageSdUri).exists())) {
				System.out.println("直接读的直接读的");
				Bitmap bitmap = PhotoCompressionUtil
						.ListViewBitmap(headimageSdUri);
				holder.chooseImage.setImageBitmap(bitmap);
			} else if (!data.isLoading()) {
				data.setIsloading(true);
				System.out.println("下载下载乐乐乐乐乐");
				webServer.downloadHeadimage(downloadHeadimageRC,
						data.getHeadimage(), headimageSdUri);
				holder.chooseImage.setImageResource(R.drawable.ic_user);
			}
		} else {
			holder.chooseImage.setImageResource(R.drawable.ic_user);
		}
		// 点击头像，进入个人信息界面
		holder.chooseImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, SomeoneInfoActivity.class);
				intent.putExtra("userBean", userBeans.get(position));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				
			}
		});
		return convertView;
	}

	// 下载用户头像
	private RequestCallBack<File> downloadHeadimageRC = new RequestCallBack<File>() {
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
	

	static class ViewHolder {
		ImageView chooseImage;
		TextView chooseName;
	}
}
