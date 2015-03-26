package com.neu.findme.server_web;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.neu.findme.domain.BaseRecordBean;
import com.neu.findme.domain.CommentBean;
import com.neu.findme.domain.LocalRecordBean;
import com.neu.findme.domain.MoneyRecordBean;
import com.neu.findme.domain.SignInBean;
import com.neu.findme.domain.UserBean;
import com.neu.findme.utils.MyApplication;
import com.neu.findme.utils.MyCookie;

/**
 * @author cxm
 *web服务类，其他界面只调用方法和处理返回的数据，将调用过程透明化
 *2015-03-09 21:10:34
 */
public class HttpWebServer {
	private String path;
	HttpUtils http = new HttpUtils();
	public HttpWebServer(){
		this.path = "http://"+MyApplication.get("ipconfig")+"";
		//如果是更换服务器之后 内存是没有数据的，所以需要从配置文件读取
		if((MyApplication.get("ipconfig")+"").equals("null")){
			this.path = "http://"+MyCookie.getString("ipconfig", "");
		}
	}
	//登录方法，返回的json需要转换为UserBean实体 stringName:user
	public void checkLogin(String userid, String password,RequestCallBack<String> requestCallBack){
		RequestParams params = new RequestParams();
		params.addBodyParameter("loginid", userid);
		params.addBodyParameter("password", password);
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/user/login.do", params,requestCallBack);
	}
	//获取服务器时间，返回的json需要转换为String对象
	public void getTime(RequestCallBack<String> requestCallBack){
		http.send(HttpRequest.HttpMethod.GET, path+"/webservice/sign/gettime.do", requestCallBack);
	}
	 // 获取工程名-类型名表的方法，返回的json需要转换为ProjectCosttypeBean实体数组
	public void getProjectCostTypes(RequestCallBack<String> requestCallBack){
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/project/listProjectCostType.do", requestCallBack);
	}
	//获取未完成的项目，返回的json转换为UnfinishProject实体数组
	public void getUnfinishProject(RequestCallBack<String> requestCallBack){
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/project/listUnfinishedProject.do", requestCallBack);
	}
	//	  获取工程名-类型名表更新时间,返回的json需要转换为String对象
	public void getProjectTypeUpdateTime(RequestCallBack<String> requestCallBack){
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/project/getTableUpdateTime/project_costtype.do", requestCallBack);
	}
	//获取预算和余额，返回的json转换为BudgetBalance实体数组
	public void getBudgetBanlance(String projectName, String costType,RequestCallBack<String> requestCallBack){
		http.send(HttpRequest.HttpMethod.GET, path+"/webservice/project/getBalanceByNameAndCostType/"+ projectName + "/" + costType + ".do", requestCallBack);
	}
	//获取财务记录某页的数据,返回的json转换为MoneyRecord实体数组
	public void getMoneyRecords(int pageCount,String userid,RequestCallBack<String> requestCallBack) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("pageNum", pageCount+"");
		http.send(HttpRequest.HttpMethod.POST, path
				+ "/webservice/project/getRecordById/" + userid + ".do",params, requestCallBack);
	}
	//获取财务记录某时间点之后的10条,返回的json转换为MoneyRecord实体数组
	public void getMoneyRecordsAfter(String time,String handlerId,RequestCallBack<String> requestCallBack){
		time = time.replace(" ", "%20");
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/project/getRecordAfterTime/"
		+ handlerId + "/"+ time + ".do", requestCallBack);
	}
	//获取财务记录某时间点之前的10条,返回的json转换为MoneyRecord实体数组
	public void getMoneyRecordsBefore(String time,String handlerId,RequestCallBack<String> requestCallBack){
		time = time.replace(" ", "%20");
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/project/getRecordBeforeTime/"
				+ handlerId + "/"+ time + ".do", requestCallBack);
	}
	
	//获取网络照片记录某页的数据,返回的json转换为NetRecordBean实体数组
	public void getNetPhotoRecords(int pageCount,String userid,RequestCallBack<String> requestCallBack) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("pageNum", pageCount+"");
		http.send(HttpRequest.HttpMethod.POST,  path + "/webservice/record/listPhoto/"
				+ userid + ".do",params, requestCallBack);
	}
	//获取网络照片记录某时间点之后的10条,返回的json转换为NetRecordBean实体数组
	public void getNetPhotoBeansAfter(String uploadTime,String userid,RequestCallBack<String> requestCallBack) {
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/record/listPhotoAfter/" + userid + "/"
				+ uploadTime + ".do", requestCallBack);
	}
	//获取网络照片记录某时间点之前的10条,返回的json转换为NetRecordBean实体数组
	public void getNetPhotoBeansBefore(String uploadTime,String userid,RequestCallBack<String> requestCallBack) {
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/record/listPhotoBefore/" + userid + "/"
				+ uploadTime + ".do", requestCallBack);
	}
	//获取监控照片记录某页的数据,返回的json转换为OtherRecordBean实体数组
	public void getOtherPhotoBeans(int pageCount,String userid,RequestCallBack<String> requestCallBack){
		RequestParams params = new RequestParams();
		params.addBodyParameter("pageNum", pageCount+"");
		http.send(HttpRequest.HttpMethod.POST, path + "/webservice/record/ICanSeeWhoPhotoList/"
				+ userid + ".do",params, requestCallBack);
	}
	//获取监控照片记录某时间点之后的10条,返回的json转换为OtherRecordBean实体数组
	public void getOtherPhotoBeansAfter(String uploadTime, String userid,RequestCallBack<String> requestCallBack) {
		http.send(HttpRequest.HttpMethod.POST, path+ "/webservice/record/listICanSeePhotosAfter/" + userid + "/"
				+ uploadTime + ".do", requestCallBack);
	}
	//获取监控照片记录某时间点之前的10条,返回的json转换为OtherRecordBean实体数组
	public void getOtherPhotoBeansBefore(String uploadTime, String userid,RequestCallBack<String> requestCallBack) {
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/record/listICanSeePhotosBefore/" + userid + "/"
				+ uploadTime + ".do", requestCallBack);
	}

	// 上传一条财务记录,返回json转换成String对象
	//时间格式为xx-xx-xx xx：xx：xx
	public void uploadMoneyRecord(MoneyRecordBean bean,RequestCallBack<String> requestCallBack){
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", bean.getId());
		params.addBodyParameter("handler", bean.getHandler());
		params.addBodyParameter("handler_id", bean.getHandler_id());
		params.addBodyParameter("recorder", bean.getRecorder());
		params.addBodyParameter("total_price", String.valueOf(bean.getTotal_price()));
		params.addBodyParameter("timeStr", bean.getTimeStr());
		params.addBodyParameter("project_name", bean.getProject_name());
		params.addBodyParameter("type_name", bean.getType_name());
		params.addBodyParameter("unit_price", String.valueOf(bean.getUnit_price()));
		params.addBodyParameter("amount", String.valueOf(bean.getNumber()));
		params.addBodyParameter("description", bean.getDesc());
		params.addBodyParameter("isdel", "0");
		params.addBodyParameter("flag", "1");
		params.addBodyParameter("state", "1");
		params.addBodyParameter("rule", "1");
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/project/addMoneyRecord.do", params,requestCallBack);
	}
	// 上传一张照片记录
	public void uploadPhotoRecord(LocalRecordBean photoBean,RequestCallBack<String> requestCallBack){
		RequestParams params = new RequestParams();
		params.addBodyParameter("takeaddress", photoBean.getLocation());
		params.addBodyParameter("ispublic", photoBean.getIsPubilc()+"");
		params.addBodyParameter("label", photoBean.getProject());
		params.addBodyParameter("title", photoBean.getTitle());
		params.addBodyParameter("description", photoBean.getDescription());
		params.addBodyParameter("imagename", photoBean.getPhotoId());
		params.addBodyParameter("userid", photoBean.getUserId() + "");
		params.addBodyParameter("uploadTimeStr", photoBean.getUpLoadedTime());
		params.addBodyParameter("takeTimeStr", photoBean.getTakeTime());
		params.addBodyParameter("latitude", photoBean.getLatitude()+ "");
		params.addBodyParameter("longitude", photoBean.getLongitude()+ "");
		params.addBodyParameter("username", photoBean.getUserName()+ "");
		if (photoBean.getPopularity() == null) {params.addBodyParameter("popularity", "");
		} else {
			params.addBodyParameter("popularity", photoBean.getPopularity());
		}
		params.addBodyParameter("photo", new File(photoBean.getSdUri().substring(photoBean.getSdUri().indexOf(":") + 1)));
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/record/uploadPhotoWithData.do", params,requestCallBack);
	}
	//修改照片信息,返回的json转换为String对象 
	public void updatePhotoInfo(BaseRecordBean photoBean,RequestCallBack<String> requestCallBack){
		RequestParams params = new RequestParams();
		params.addBodyParameter("takeaddress", photoBean.getLocation());
		params.addBodyParameter("ispublic", photoBean.getIsPubilc()+"");
		params.addBodyParameter("label", photoBean.getProject());
		params.addBodyParameter("title", photoBean.getTitle());
		params.addBodyParameter("description", photoBean.getDescription());
		params.addBodyParameter("imagename", photoBean.getPhotoId());
		params.addBodyParameter("userid", photoBean.getUserId() + "");
		params.addBodyParameter("uploadTimeStr", photoBean.getUpLoadedTime());
		params.addBodyParameter("takeTimeStr", photoBean.getTakeTime());
		params.addBodyParameter("latitude", photoBean.getLatitude()+ "");
		params.addBodyParameter("longitude", photoBean.getLongitude()+ "");
		params.addBodyParameter("username", photoBean.getUserName()+ "");
		if (photoBean.getPopularity() == null) {params.addBodyParameter("popularity", "");
		} else {
			params.addBodyParameter("popularity", photoBean.getPopularity());
		}
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/record/updatePhoto.do", params,requestCallBack);
	}
	//上传一条签到记录,返回的json转换为String对象
	public void signIn(SignInBean bean,RequestCallBack<String> requestCallBack){
	RequestParams params = new RequestParams();
	params.addBodyParameter("userid", bean.getUserid());
	params.addBodyParameter("name", bean.getName());
	params.addBodyParameter("location", bean.getLocation());
	params.addBodyParameter("type", bean.getType());
	params.addBodyParameter("remark", bean.getRemark());
	params.addBodyParameter("device_id", bean.getDeviceId());
	http.send(HttpRequest.HttpMethod.POST, path+"/webservice/sign/addRecord.do", params,requestCallBack);
}

	//获得各种用户列表,返回的json转换为UserBean实体数组
	//获取到可见我的列表
	public void getCanSeeIUsers(String userid,RequestCallBack<String> requestCallBack){
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/user/listUp/" + userid+ ".do",requestCallBack);
}
	//获取到我可见的列表 无权限标识
	public void getICanSeeUsers(String userid,RequestCallBack<String> requestCallBack){
	http.send(HttpRequest.HttpMethod.POST, path+"/webservice/user/listDown/"+ userid + ".do",requestCallBack);
}
	//获取全部的人员列表 无权限标识
	public void getAllUsers(String userid,RequestCallBack<String> requestCallBack){
	http.send(HttpRequest.HttpMethod.POST, path+"/webservice/user/listIncludeCanSeeMe/" + userid + ".do",requestCallBack);
}
	//获取全部人员的头像列表
	public void getAllHeadImageList(RequestCallBack<String> requestCallBack ){
		http.send(HttpRequest.HttpMethod.POST, path+"/headimage/getHeadImageList.do", requestCallBack);
	}
	//增加一个可见自己的用户,返回的json转换为String对象
	public void addUserCanSeeMe(String userid, List<UserBean> userList,RequestCallBack<String> requestCallBack){
		RequestParams params = new RequestParams();
		params.addBodyParameter("loginid", userid);
		// 处理userlist
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0;i<userList.size();i++){
			list.add("\"" +userList.get(i).getId()+"\"");
		}
		params.addBodyParameter("userListJson", list.toString());
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/user/addUserCanSeeMe.do",params,requestCallBack);
	}
	//减少一个可见自己的用户,返回的json转换为String对象
	public void deleteUserCanSeeMe(String userid, List<UserBean> userList,RequestCallBack<String> requestCallBack){
		RequestParams params = new RequestParams();
		params.addBodyParameter("loginid", userid);
		// 处理userlist
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0;i<userList.size();i++){
			list.add("\"" +userList.get(i).getId()+"\"");
		}
		params.addBodyParameter("userListJson", list.toString());
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/user/deleteUserCanSeeMe.do",params,requestCallBack);
	}
	//发布一条评论,返回的json转换为String对象
	public void uploadComment(CommentBean commentBean,RequestCallBack<String> requestCallBack){
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", commentBean.getId()+"");
		params.addBodyParameter("photoid", commentBean.getPhotoId());
		params.addBodyParameter("userid", commentBean.getUserId());
		params.addBodyParameter("name", commentBean.getUserName());
		params.addBodyParameter("timeStr", commentBean.getTime());
		params.addBodyParameter("content", commentBean.getContent());
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/textmessage/addRecord.do" ,params,requestCallBack);
	}
	//获得某张照片的评论列表,返回的json转换为CommentBean实体数组
	public void getCommentBeans(String photoId,RequestCallBack<String> requestCallBack){
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/textmessage/selectphotobyid/" + photoId + ".do",requestCallBack);
	}
	//查看某个用户是否有未查看的评论,返回的json转换为String对象
	public void hasNotReadNotice(String userId,RequestCallBack<String> requestCallBack){
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/textmessage/checkreadstate/" + userId + ".do",requestCallBack);
	}
	//获得某个用户未查看评论图片的列表,返回的json转换为NoticeBean实体数组
	public void getNoticeBeans(String userId,RequestCallBack<String> requestCallBack){
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/textmessage/getreadstate/" + userId + ".do" ,requestCallBack);
	}
	//告知服务器某张照片已经查看完毕
	public void hadRead(String userId, String photoId,RequestCallBack<String> requestCallBack){
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/textmessage/changereadstate/" + userId + "/"
				+ photoId + ".do",requestCallBack);
	}
	//获取服务器版本号，便于更新
	public void getVersion(RequestCallBack<String> requestCallBack){
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/version/getVersion.do",requestCallBack);
	}
	//告知服务器某个用户的全部照片已经查看完毕
	public void allHadRead(String userId,RequestCallBack<String> requestCallBack){
		http.send(HttpRequest.HttpMethod.POST, path+"/webservice/textmessage/changeallreadstate/"+userId+".do",requestCallBack);
	}
	//下载照片文件,url为照片名，target为本地存储路径
	public void downloadPhoto(RequestCallBack<File> requestCallBack,String url,String target){
		http.download(path+"/upload/FPPhoto/"+url , target,true,true, requestCallBack);
	}
	//下载头像文件,url为照片名，target为本地存储路径
	public void downloadHeadimage(RequestCallBack<File> requestCallBack,String url,String target){
		http.download(path+"/resource/headimage/"+url , target,true,true, requestCallBack);
	}
	//下载findme最新版
	public void downloadAPK(RequestCallBack<File> requestCallBack ,String target){
		//http://2.findme307a.sinaapp.com/html/down.html
//		http.download(path+"/resource/downloadFile/findme.apk", target,true,true, requestCallBack);
		http.download("http://findme307a-findme.stor.sinaapp.com/findme.apk", target,true,true, requestCallBack);
	}

}
