package com.yun.spider;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ItbtService extends BaseService {
	String TAG = "ItbtService";
	CommandReceiver cmdReceiver;
	boolean isRun = false;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(getTAG(), "onCreate");
		cmdReceiver = new ItbtReceiver();
		flag = true;
		SharedPrefsUtil.putValue(getApplicationContext(), MainActivity.isOpen,
				true);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(getTAG(), "onStartCommand="+flags);
		IntentFilter intentFilter = new IntentFilter();
		//发送广播名称aaaaa 参数名字data
		intentFilter.addAction(getTAG());
		registerReceiver(cmdReceiver, intentFilter);
		cmdReceiver.doJob();// 启动线程
		return START_STICKY;//START_STICKY是service被kill掉后自动
	}

	public int getFrequency() {
		return SharedPrefsUtil.getValue(getApplicationContext(),
				MainActivity.frequency, 2);
	}
	
	@Override
	public String getTAG() {
		// TODO Auto-generated method stub
		return TAG;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		flag = false;
		this.unregisterReceiver(cmdReceiver);// 取消BroadcastReceiver
		Log.i(TAG, "onDestroy");
	}
	
	
	class ItbtReceiver extends CommandReceiver {
		@Override
		public void stopServer() {
			flag = false;// 停止线程
			stopSelf();// 停止服务
			Log.i(getTAG(), "收到消息停止服务,关闭服务");
			SharedPrefsUtil.putValue(getApplicationContext(),
					MainActivity.isOpen, false);
		}
		
		
		@Override
		public void excuteJob() {
			Log.i(TAG, "执行秒杀");
			boolean check = HttpUtils.checkMiao();
			if (check) {
				addNotification(getApplicationContext(), "秒标提示", "亲，有秒标了");
				try {
					Thread.sleep(1000 * 60 * 60);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}// 隔一个小时后运行
				Log.i(TAG, "有秒标了停一个小时");
			}
		}
	}


}
