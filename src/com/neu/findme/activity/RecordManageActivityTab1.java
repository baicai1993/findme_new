package com.neu.findme.activity;

import com.neu.findme.R;
import com.neu.findme.fragment.RecordManageFragment;
import com.neu.findme.utils.MyApplication;
import com.neu.findme.view.MyAlertDialog;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

/**
 * @author cxm
 *记录管理activity，包含 RecordManageFragment
 *2015-03-09 20:43:31
 */
public class RecordManageActivityTab1 extends FragmentActivity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tab1_record_manage);
		getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new RecordManageFragment()).commit();
	}
	// 点击返回键时的操作
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
			myAlertDialog.showWoodDialog(true);
			myAlertDialog.getDialog_title().setText(MyApplication.getApplication().getString(R.string.quitDialog_title));
			myAlertDialog.getDialog_msg().setText(MyApplication.getApplication().getString(R.string.quitDialog_content));
			myAlertDialog.getPosButton().setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					getParent().finish();
				}
			});
			myAlertDialog.getNevButton().setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myAlertDialog.dismiss();
				}
			});
		}
		return super.onKeyDown(keyCode, event);
	}
}
