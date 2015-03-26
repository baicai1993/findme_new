package com.neu.findme.service;

import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.neu.findme.R;
import com.neu.findme.activity.NoticeCenterActivity;
import com.neu.findme.domain.NoticeBean;
import com.neu.findme.server_db.NoticeRecDBserver;
import com.neu.findme.server_web.HttpWebServer;
import com.neu.findme.utils.JsonParseUtils;
import com.neu.findme.utils.MyCookie;

/**
 * @author cxm
 *伪推送服务，每隔固定时间向服务器更新未查看的评论记录，并修改侧拉抽屉的显示，震动提示并弹出通知栏提示用户
 *2015-03-09 21:12:00
 */
public class GetNoticeService extends Service {
	static final String noticeAction1 = "notice_action1"; // 其他组件过滤的依据
	private static NotificationManager mNotificationManager;
	private HttpWebServer webServer;
	private NoticeRecDBserver noticeRecDBserver;
	private String userId;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		webServer = new HttpWebServer();
		noticeRecDBserver = new NoticeRecDBserver(this);
		userId = MyCookie.getString("userId", "");//这个userId不能从内存中获取，因为service的生命周期是独立的
		//每5分钟后发送下一次请求
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					webServer.hasNotReadNotice(userId, hasNotReadNoticeRC);
					try {
						Thread.sleep(300000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	//取消通知栏通知
	public static void cancelNotify(){
		if(mNotificationManager!=null){
			mNotificationManager.cancelAll();
		}
	} 
	//消息通知栏相关处理
	@SuppressWarnings("deprecation")
	private void showNotify(){
        //定义NotificationManager
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) getSystemService(ns);
        //定义通知栏展现的内容信息
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = "FindMe";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        //定义下拉通知栏时要展现的内容信息
        Context context = getApplicationContext();
        CharSequence contentTitle = "FindMe";
        CharSequence contentText = "您有未查看的回复记录";
        Intent notificationIntent = new Intent(GetNoticeService.this, NoticeCenterActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//设置为刷新任务栈
        PendingIntent contentIntent = PendingIntent.getActivity(GetNoticeService.this, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText,
                contentIntent);
        notification.defaults |= Notification.DEFAULT_VIBRATE;  
        long[] vibrate = {0,50,40,50};  
        notification.vibrate = vibrate;
        notification.flags =Notification.FLAG_AUTO_CANCEL;
        //用mNotificationManager的notify方法通知用户生成标题栏消息通知
        mNotificationManager.notify(1, notification);
	}
	//清理任务栈，并开启新任务栈
	private RequestCallBack<String> hasNotReadNoticeRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			if(JsonParseUtils.jsonToBoolean(arg0.result)){
				showNotify();
			}
			webServer.getNoticeBeans(userId, getNotSeeNoticeRC);

		}
	};
	private RequestCallBack<String> getNotSeeNoticeRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			List<NoticeBean> list = (List<NoticeBean>) JsonParseUtils.jsonToList(arg0.result, NoticeBean.class);
			for(NoticeBean bean:list){
				bean.setIsRead(false);
			}
			if(list.size()>0){//发送广播，告诉mainActivity收到了多少条记录
				noticeRecDBserver.addListOrUpdate(list);//更新数据库
				Intent intent = new Intent(noticeAction1);
				// 根据服务器返回结果设置bundle
				intent.putExtra("noticeNum", list.size());
				sendBroadcast(intent);
			}
		}
	};
}
