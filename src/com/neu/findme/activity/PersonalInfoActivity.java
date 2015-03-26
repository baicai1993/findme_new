package com.neu.findme.activity;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.neu.findme.R;
import com.neu.findme.domain.UserBean;
import com.neu.findme.server_db.UserDBServer;
import com.neu.findme.utils.FileUtil;
import com.neu.findme.utils.MyCookie;
import com.neu.findme.utils.PhotoCompressionUtil;

/**
 * @author cxm 个人信息页面从数据库中读取个人信息，显示 2015-03-09 20:12:42
 */
public class PersonalInfoActivity extends Activity {
	private UserDBServer userDBServer;
	@ViewInject(R.id.personal_head_imageview)
	private ImageView headImageView;
	@ViewInject(R.id.textViewNameShow)
	private TextView nameTextView;
	@ViewInject(R.id.textViewIDShow)
	private TextView iDTextView;
	@ViewInject(R.id.textViewAccountShow)
	private TextView accoutTextView;
	@ViewInject(R.id.backButton)
	private Button backButton;
	@ViewInject(R.id.textViewEmailShow)
	private TextView emailTV;

	@OnClick(R.id.backButton)
	public void backBtnListener(View view) {
		PersonalInfoActivity.this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_personal_info);
		ViewUtils.inject(PersonalInfoActivity.this);
		userDBServer = new UserDBServer(this);
		UserBean myself = userDBServer.queryMe();
		iDTextView.setText(myself.getId());
		accoutTextView.setText(myself.getId());
		nameTextView.setText(myself.getName());
		emailTV.setText(myself.getEmail());
		// 设置头像
		String uriStr = FileUtil.HEADIMAGE_PATH
				+ MyCookie.getString("headimage", null);
		if (new File(FileUtil.HEADIMAGE_PATH
				+ MyCookie.getString("headimage", null)).exists()) {
			Bitmap bitmap = PhotoCompressionUtil.ListViewBitmap(uriStr);
			headImageView.setImageBitmap(bitmap);

		}
	}

}
