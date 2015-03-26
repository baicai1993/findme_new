package com.neu.findme.adapter;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neu.findme.R;
import com.neu.findme.domain.MoneyRecordBean;


/**
 * @author cxm
 *财务管理界面列表适配器
 *2015-03-09 20:54:03
 */
public class MoneyRecordListAdapter extends BaseAdapter {
	public static List<MoneyRecordBean> recordBeans = new ArrayList<MoneyRecordBean>();
	private Context context;
	private List<MoneyItemViewHolder> holders = new ArrayList<MoneyItemViewHolder>();
	private MoneyItemViewHolder holder;
	
	public MoneyRecordListAdapter(Context context, List<MoneyRecordBean> list) {
		super();
		this.context = context;
		MoneyRecordListAdapter.recordBeans = list;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		holder = null;
		MoneyRecordBean data = recordBeans.get(position);
			if (convertView == null) {
				holder = new MoneyItemViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.listitem_money, new LinearLayout(context));
				// 实例化组件
				// 单个listview各属性添加值
				holder.categoryText = (TextView) convertView
						.findViewById(R.id.categoryText);// 类别
				holder.projectNameText = (TextView) convertView
						.findViewById(R.id.item_projectNameText);// 项目组名称
				holder.moneyTimeText = (TextView) convertView
						.findViewById(R.id.moneyTimeText);// 项目时间
				holder.showMoneyText = (TextView) convertView
						.findViewById(R.id.showMoneyText);// 金额
				holders.add(holder);
				convertView.setTag(holder);
			} else {
				holder = (MoneyItemViewHolder) convertView.getTag();
			}
				holder.categoryText.setText(data.getType_name());
				holder.projectNameText.setText(data.getProject_name());
				holder.moneyTimeText.setText(data.getTimeStr());
				//格式化小数点
				holder.showMoneyText.setText(new DecimalFormat(".00").format(data.getTotal_price()));
			return convertView;
	}

	static class MoneyItemViewHolder {
		TextView categoryText;
		TextView projectNameText;
		TextView moneyTimeText;
		TextView showMoneyText;
	}
}
