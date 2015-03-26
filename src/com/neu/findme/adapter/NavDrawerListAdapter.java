package com.neu.findme.adapter;

import java.io.File;
import java.util.List;

import com.neu.findme.R;
import com.neu.findme.domain.NavDrawerItem;
import com.neu.findme.server_db.UserDBServer;
import com.neu.findme.utils.FileUtil;
import com.neu.findme.utils.MyCookie;
import com.neu.findme.utils.PhotoCompressionUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author cxm 左侧抽屉的列表适配器 2015-03-09 20:54:39
 */
public class NavDrawerListAdapter extends BaseAdapter {

	private Context context;
	private List<NavDrawerItem> navDrawerItems;
	private UserDBServer userDBServer;

	public NavDrawerListAdapter(Context context,
			List<NavDrawerItem> navDrawerItems) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;
		userDBServer = new UserDBServer(context);
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhS");
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			// 个人信息，单独布局
			if (position == 0) {
				convertView = mInflater.inflate(R.layout.item_drawerlist_self,
						new RelativeLayout(context));
			} else {
				convertView = mInflater.inflate(R.layout.item_drawerlist,
						new RelativeLayout(context));

			}
		}

		ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
		TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
		TextView txtCount = (TextView) convertView.findViewById(R.id.counter);
		// 设置个人头像,userId
		if (position != 0) {
			imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
			txtTitle.setText(navDrawerItems.get(position).getTitle());
		} else {
			txtTitle.setText(userDBServer.queryMe().getName());
			String uriStr = FileUtil.HEADIMAGE_PATH
					+ MyCookie.getString("headimage", null);
			if (new File(uriStr).exists()) {
				Bitmap bitmap = PhotoCompressionUtil.ListViewBitmap(uriStr);
				imgIcon.setImageBitmap(bitmap);
			}else{
				imgIcon.setImageResource(R.drawable.ic_user);
			}
		}

		if (navDrawerItems.get(position).getCounterVisibility()) {
			txtCount.setText(navDrawerItems.get(position).getCount());
		} else {
			txtCount.setVisibility(View.GONE);
		}

		return convertView;
	}

}
