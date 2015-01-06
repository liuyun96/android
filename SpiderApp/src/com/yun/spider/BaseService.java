package com.yun.spider;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class BaseService extends Service {

	public String TAG = "BaseService";

	/**
	 * 控制线程
	 */
	static boolean flag;
	/**
	 * 线程的状态
	 */
	boolean isRun = false;

	AppContext appContext;
	
	CommandReceiver cmdReceiver;
	
	int frequency = 1;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressLint("NewApi")
	protected void addNotification(Context context, String msg, String sender) {
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(context, MainActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("msg", msg);
		bundle.putString("sender", sender);
		intent.putExtras(bundle);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_ONE_SHOT);
		Notification noti = new Notification.Builder(context)
				.setContentTitle(sender).setContentText(msg).setTicker("MSG")
				.setContentIntent(pendingIntent)
				.setSmallIcon(R.drawable.notice).build();
		noti.defaults = Notification.DEFAULT_SOUND;
		// 让声音、振动无限循环，直到用户响应
		noti.flags = Notification.FLAG_INSISTENT;
		manager.notify(1, noti);
	}
	
	class CommandReceiver extends BroadcastReceiver {

		public void stopServer() {
			flag = false;// 停止线程
			stopSelf();// 停止服务
			Log.i(getTAG(), "收到消息停止服务,关闭服务");
			SharedPrefsUtil.putValue(getApplicationContext(),
					MainActivity.isOpen, false);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			int cmd = intent.getIntExtra("cmd", -1);
			if (cmd == MainActivity.CMD_STOP_SERVICE) {// 如果等于0
				stopServer();
			}
		}
		
		public void excuteJob() {

		}

		public void doJob() {
			if (!isRun) {
				isRun = true;
				new Thread() {
					@Override
					public void run() {
						Log.i(TAG, "启动线程");
						while (flag) {
							Calendar calendar = Calendar.getInstance();
							int hour = calendar.get(Calendar.HOUR_OF_DAY);
							if (hour >= 8 & hour <= 23) {// 在这个区间运行
								try {
									appContext = (AppContext) getApplication();// 全局Context
									if (appContext.isNetworkConnected()) {
										boolean isRun = false;
										int type = appContext.getNetworkType();
										boolean isWifi = SharedPrefsUtil
												.getValue(
														getApplicationContext(),
														MainActivity.isWifi,
														true);
										if (!isWifi) {
											isRun = true;
										} else if (isWifi && type == 1) {// 只WiFi下才可以允许
											isRun = true;
										}
										if (isRun) {// 在wifi条件下才运行
											// 增加扫描的次数
											int times = SharedPrefsUtil
													.getValue(
															getApplicationContext(),
															Constant.SHARE_MAIO_TIMES,
															0);
											SharedPrefsUtil.putValue(
													getApplicationContext(),
													Constant.SHARE_MAIO_TIMES,
													times + 1);
											// 添加处理的方法
											excuteJob();
										}
										Log.i(TAG, "正常情况下每" + getFrequency()
												+ "分钟扫描一次");
										Thread.sleep(1000 * 60 * getFrequency());// 每两分钟运行一次
									} else {
										Log.i(TAG, "没有网络停5分钟");
										Thread.sleep(1000 * 60 * 5);
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							} else {
								try {
									Log.i(getTAG(), "服务在这个区间不运行，暂停半个小时");
									Thread.sleep(1000 * 60 * 30);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}.start();
			}

		}
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public String getTAG() {
		return TAG;
	}

}
