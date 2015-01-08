package com.yun.spider;

import java.util.List;

import com.yun.spider.bean.Caipiao;
import com.yun.spider.db.CaiPiaoDB;
import com.yun.spider.utils.CaiPiaoUtil;

import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class CaipiaoService extends BaseService {

	String TAG = "CaipiaoService";
	CaipiaoReceiver cmdReceiver;
	boolean isRun = false;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(getTAG(), "onCreate");
		cmdReceiver = new CaipiaoReceiver();
		flag = true;
		SharedPrefsUtil.putValue(getApplicationContext(), MainActivity.isOpen,
				true);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(getTAG(), "onStartCommand=" + flags);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(TAG);
		registerReceiver(cmdReceiver, intentFilter);
		cmdReceiver.doJob();// 启动线程
		return START_STICKY;// START_STICKY是service被kill掉后自动
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		flag = false;
		this.unregisterReceiver(cmdReceiver);// 取消BroadcastReceiver
		Log.i(TAG, "onDestroy");
	}

	@Override
	public int getFrequency() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public String getTAG() {
		// TODO Auto-generated method stub
		return TAG;
	}

	/**
	 * 接收消息关闭当前的service
	 * 
	 * @author Administrator
	 * 
	 */

	private void sendBroadcastActivity() {
		Intent intent = new Intent();
		intent.setAction(CaipiaoActivity.UpdateUiReceiver);
		intent.putExtra("cmd", 1);
		sendBroadcast(intent);
	}

	class CaipiaoReceiver extends CommandReceiver {

		@Override
		public void excuteJob() {
			Log.i(TAG, "执行彩票");
			CaiPiaoDB caiPiaoDB = new CaiPiaoDB(getApplicationContext());
			Integer period = caiPiaoDB.maxPeriodGet();
			Integer newPeriod = CaiPiaoUtil.newPeriod();
			if (!newPeriod.equals(period)) {
				Integer beginPeriod = null;
				Integer endPeriod = null;
				if (period != null) {
					beginPeriod = period + 1;
					endPeriod = period + 30;
					if (endPeriod > newPeriod) {
						endPeriod = newPeriod;
					}
				}
				Log.i(TAG, "beginPeriod:" + beginPeriod + "endPeriod:"
						+ endPeriod);
				List<Caipiao> list = CaiPiaoUtil.caipiaoList(beginPeriod,
						endPeriod);
				if (!list.isEmpty()) {
					for (Caipiao cai : list) {
						caiPiaoDB.insert(cai);
						sendBroadcastActivity();
					}
				}
			}

		}

		@Override
		public void stopServer() {
			flag = false;// 停止线程
			stopSelf();// 停止服务
			Log.i(getTAG(), "收到消息停止服务,关闭服务");
		}
	}

}
