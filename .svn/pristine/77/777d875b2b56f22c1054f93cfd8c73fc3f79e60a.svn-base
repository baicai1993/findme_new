package com.neu.findme.activity;

import java.io.File;
import java.text.ParseException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.neu.findme.R;
import com.neu.findme.R.id;
import com.neu.findme.R.layout;
import com.neu.findme.R.string;
import com.neu.findme.domain.UserBean;
import com.neu.findme.utils.ChangeDateFormatter;
import com.neu.findme.utils.FileUtil;
import com.neu.findme.utils.PhotoCompressionUtil;

public class SomeoneInfoActivity extends Activity {

	@ViewInject(R.id.personal_head_imageview)
	private ImageView headImageView;
	@ViewInject(R.id.textViewNameShow)
	private TextView nameTextView;
	@ViewInject(R.id.textViewSexShow)
	private TextView sexTextView;
	@ViewInject(R.id.textViewBirthdayShow)
	private TextView birthdayTextView;
	@ViewInject(R.id.backButton)
	private Button backButton;
	@ViewInject(R.id.textViewEmailShow)
	private TextView emailTV;
	@ViewInject(R.id.textViewPhoneShow)
	private TextView phoneTextView;
	@ViewInject(R.id.layout_someoneinfo_phoneview)
	private RelativeLayout phoneViewLayout;
	@ViewInject(R.id.layout_someoneinfo_headview)
	private RelativeLayout headviewLayout;

	@OnClick(R.id.layout_someoneinfo_phoneview)
	public void phoneListener(View view) {

		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
				+ userBean.getTelephone()));

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
//	@OnClick(R.id.layout_someoneinfo_headview)
//	public void imageListener(View view){
//		if(!(userBean.getHeadimage()).equals("")){
//			Intent intent = new Intent(SomeoneInfoActivity.this,ShowBigPhotoActivity.class);
//			intent.putExtra("recordBeanId", userBean);
//			intent.putExtra("Flag", flag);
//			startActivity(intent);
//		}
//	}

	@OnClick(R.id.backButton)
	public void backBtnListener(View view) {
		SomeoneInfoActivity.this.finish();
	}

	private UserBean userBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_someone_info);
		ViewUtils.inject(SomeoneInfoActivity.this);
		UserBean someoneBean = (UserBean) getIntent().getSerializableExtra(
				"userBean");
		userBean = someoneBean;
		sexTextView.setText(someoneBean.getGender() == "1" ? "女" : "男");
		try {
			birthdayTextView.setText(ChangeDateFormatter
					.changeDateFormat4(someoneBean.getBirthday()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		phoneTextView.setText(someoneBean.getTelephone());
		nameTextView.setText(someoneBean.getName());
		emailTV.setText(someoneBean.getEmail());
		// 设置头像
		String uriStr = FileUtil.HEADIMAGE_PATH + someoneBean.getHeadimage();
		if (new File(FileUtil.HEADIMAGE_PATH + someoneBean.getHeadimage())
				.exists()) {
			Bitmap bitmap = PhotoCompressionUtil.ListViewBitmap(uriStr);
			headImageView.setImageBitmap(bitmap);

		}
	}
}
