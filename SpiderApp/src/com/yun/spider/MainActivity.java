package com.yun.spider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final int CMD_STOP_SERVICE = 0;
	public static final int cmd_run_only_wifi = 1;
	public static final int cmd_run_all = 2;
	public static final String isOpen = "is_open";
	public static final String isWifi = "is_wifi";
	public static final String frequency = "frequency";

	private AppContext appContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Switch switch1 = (Switch) findViewById(R.id.switch1);
		int times = SharedPrefsUtil.getValue(getApplicationContext(),
				Constant.SHARE_MAIO_TIMES, 0);
		TextView textView = (TextView) findViewById(R.id.miao_times);
		textView.setText("" + times);
		switch1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean state) {
				appContext = (AppContext) getApplication();// 全局Context
				if (appContext.isNetworkConnected()) {
					Log.d("switchButton", state ? "开" : "关");
					Toast.makeText(MainActivity.this, state ? "开" : "关",
							Toast.LENGTH_SHORT).show();
					SharedPrefsUtil.putValue(getApplicationContext(), isOpen,
							state);
					if (state) {
						Intent intent = new Intent(getApplicationContext(),
								ItbtService.class);
						startService(intent);
					} else {
						Intent intent = new Intent();
						intent.setAction("AAAAA");
						intent.putExtra("cmd", CMD_STOP_SERVICE);
						sendBroadcast(intent);
					}
					if (state) {
						switch1.setText("秒杀正在运行");
					} else {
						switch1.setText("秒杀关闭状态");
					}
				} else {
					Toast.makeText(MainActivity.this, "无法更新状态，网络链接不正常",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		final Switch switch2 = (Switch) findViewById(R.id.switch2);
		switch2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean state) {
				SharedPrefsUtil
						.putValue(getApplicationContext(), isWifi, state);
				if (state) {
					switch2.setText("仅wifi下可运行");
				} else {
					switch2.setText("任何网络下都可以运行");
				}
			}
		});

		boolean open = SharedPrefsUtil.getValue(getApplicationContext(),
				isOpen, false);
		boolean wifi = SharedPrefsUtil.getValue(getApplicationContext(),
				isWifi, false);
		switch1.setChecked(open);
		switch2.setChecked(wifi);
		if (open) {
			switch1.setText("秒杀正在运行");
		} else {
			switch1.setText("秒杀关闭状态");
		}
		if (wifi) {
			switch2.setText("仅wifi下可运行");
		} else {
			switch2.setText("任何网络下都可以运行");
		}
		int fre = SharedPrefsUtil.getValue(getApplicationContext(), frequency,
				2);
		final EditText editText = (EditText) findViewById(R.id.frequency);
		editText.setText("" + fre);
		Button button = (Button) findViewById(R.id.updateBtn);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				CharSequence text = editText.getText();
				SharedPrefsUtil.putValue(getApplicationContext(), frequency,
						Integer.valueOf(text.toString()));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
