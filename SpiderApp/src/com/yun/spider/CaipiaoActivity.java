package com.yun.spider;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.yun.spider.db.CaiPiaoDB;
import com.yun.spider.utils.CaiPiaoUtil;

public class CaipiaoActivity extends Activity {
	private AppContext appContext;
	public static final String isOpenCaipiao = "is_open_caipiao";
	static final String tag = "CaipiaoActivity";
	static final String UpdateUiReceiver = "UpdateUiReceiver";
	TextView period;

	private static final String[] m = { "个位", "十位", "百位", "千位", "万位" };
	private TextView view;
	private Spinner spinner;
	private ArrayAdapter<String> adapter;
	
	private EditText editNums;
	private EditText editQishu;
	private EditText editWeishu;
	private WebView webView;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.caipiao);
		period = (TextView) findViewById(R.id.period);
		CaiPiaoDB caiPiaoDB = new CaiPiaoDB(getApplicationContext());
		period.setText("当前期" + caiPiaoDB.maxPeriodGet());
		addSwitch();
		addspinner();
		
		Button button = (Button) findViewById(R.id.total);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				total();
			}
		});
		
		registerUpdateUiReceiver();

	}

	private void addspinner() {
		view = (TextView) findViewById(R.id.spinnerText);
		spinner = (Spinner) findViewById(R.id.Spinner01);
		// 将可选内容与ArrayAdapter连接起来
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, m);
		// 设置下拉列表的风格
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 将adapter 添加到spinner中
		spinner.setAdapter(adapter);
		// 添加事件Spinner事件监听
		spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
		// 设置默认值
		spinner.setVisibility(View.VISIBLE);
	}

	// 使用数组形式操作
	class SpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			view.setText("位数是：" + m[arg2]);
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
	}

	private void addSwitch() {
		final Switch switch3 = (Switch) findViewById(R.id.switch3);
		boolean opencai = SharedPrefsUtil.getValue(getApplicationContext(),
				isOpenCaipiao, false);
		switch3.setChecked(opencai);
		switch3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean state) {
				appContext = (AppContext) getApplication();// 全局Context
				if (appContext.isNetworkConnected()) {
					Log.d("switchButton", state ? "开" : "关");
					Toast.makeText(CaipiaoActivity.this, state ? "开" : "关",
							Toast.LENGTH_SHORT).show();
					SharedPrefsUtil.putValue(getApplicationContext(),
							isOpenCaipiao, state);
					if (state) {
						Intent intent = new Intent(getApplicationContext(),
								CaipiaoService.class);
						startService(intent);
					} else {
						Intent intent = new Intent();
						intent.setAction("CaipiaoService");
						intent.putExtra("cmd", 1);
						sendBroadcast(intent);
					}
					if (state) {
						switch3.setText("彩票打开了");
					} else {
						switch3.setText("彩票关闭状态");
					}
				} else {
					Toast.makeText(CaipiaoActivity.this, "无法更新状态，网络链接不正常",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		if (opencai) {
			switch3.setText("彩票正在运行");
		} else {
			switch3.setText("彩票关闭状态");
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	public void total(){
		view = (TextView) findViewById(R.id.spinnerText);
		editQishu = (EditText) findViewById(R.id.qishu);
		editNums = (EditText) findViewById(R.id.nums);
		editWeishu = (EditText) findViewById(R.id.weishu);
		webView = (WebView) findViewById(R.id.webview);
		String data = CaiPiaoUtil.total(editWeishu.getText().toString(), editNums.getText().toString());
		webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
		Log.i(tag, "data"+data);
	}

	/**
	 * 注册receiver
	 */
	private void registerUpdateUiReceiver() {
		UpdateUiReceiver updateUiReceiver = new UpdateUiReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(UpdateUiReceiver);
		registerReceiver(updateUiReceiver, intentFilter);
	}

	/**
	 * 更新当前activity的数据
	 * 
	 * @author Administrator
	 * 
	 */
	class UpdateUiReceiver extends BroadcastReceiver {
		// 接收消息
		@Override
		public void onReceive(Context arg0, Intent intent) {
			int cmd = intent.getIntExtra("cmd", -1);
			if (cmd == 1) {// 如果等于0
				Log.i(tag, "收到消息更新当前期");
				period = (TextView) findViewById(R.id.period);
				if (period != null) {
					CaiPiaoDB caiPiaoDB = new CaiPiaoDB(getApplicationContext());
					period.setText("当前期" + caiPiaoDB.maxPeriodGet());
				}
			}
		}
	}

}
