package com.yun.spider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final int CMD_STOP_SERVICE = 0;
	public static final int cmd_run_wifi = 1;
	public static final int cmd_run_all = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Switch switch1 = (Switch) findViewById(R.id.switch1);
		switch1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean state) {
				Log.d("switchButton", state ? "开" : "关");
				Toast.makeText(MainActivity.this, state ? "开" : "关",
						Toast.LENGTH_SHORT).show();
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
			}
		});

		Switch switch2 = (Switch) findViewById(R.id.switch2);
		switch2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean state) {
				Log.d("switchButton2", state ? "开" : "关");
				Intent intent = new Intent();
				intent.setAction("AAAAA");
				if (state) {
					intent.putExtra("cmd", cmd_run_wifi);
				} else {
					intent.putExtra("cmd", cmd_run_all);
				}
				sendBroadcast(intent);
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
