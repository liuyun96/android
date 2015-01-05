package com.yun.spider;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 保持service不被杀死
 * 
 * @author Administrator
 * 
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
	private String tag = "MyBroadcastReceiver";
	String ItbtService = "com.yun.spider.ItbtService";

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean isServiceRunning = false;
		if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
			// 检查Service状态
			boolean open = SharedPrefsUtil.getValue(
					context.getApplicationContext(),MainActivity.isOpen, false);
			if (open) {
				ActivityManager manager = (ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE);
				for (RunningServiceInfo service : manager
						.getRunningServices(Integer.MAX_VALUE)) {
					//Log.i(tag, "service:"+service.service.getClassName());
					if (ItbtService.equals(service.service.getClassName())) {
						isServiceRunning = true;
						break;
					}
				}
				if (!isServiceRunning) {
					Intent i = new Intent(context, ItbtService.class);
					context.startService(i);
					Log.i(tag, "重新启动 ItbtService");

				}else{
					Log.i(tag, "ItbtService 正在运行");
				}
			}else{
				Log.i(tag, "检查  ItbtService isOpen");
			}
		}
	}

}
