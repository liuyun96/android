package com.yun.spider.db;

import java.util.HashMap;
import java.util.Map;

import android.net.Uri;

/**
 * news
 * 
 * @author wangxin
 * 
 */
public class CaiPiaoColumn extends DatabaseColumn {

	public static final String TABLE_NAME = "caipiao";
	public static final String QISHU = "qi_shu";
	public static final String GE = "ge";
	public static final String SHI = "shi";
	public static final String BAI = "bai";
	public static final String QIAN = "qian";
	public static final String WAN = "wan";
	public static final String UPDATE_TIME = "update_time";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);
	private static final Map<String, String> mColumnMap = new HashMap<String, String>();
	static {
		//mColumnMap.put(_ID, "integer primary key");
		mColumnMap.put(QISHU, "integer primary key");
		mColumnMap.put(GE, "integer");
		mColumnMap.put(SHI, "integer");
		mColumnMap.put(BAI, "integer");
		mColumnMap.put(QIAN, "integer");
		mColumnMap.put(WAN, "integer");
		mColumnMap.put(BAI, "integer");
		mColumnMap.put(UPDATE_TIME, "timestamp");
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return TABLE_NAME;
	}

	@Override
	public Uri getTableContent() {
		// TODO Auto-generated method stub
		return CONTENT_URI;
	}

	@Override
	protected Map<String, String> getTableMap() {
		// TODO Auto-generated method stub
		return mColumnMap;
	}

}
