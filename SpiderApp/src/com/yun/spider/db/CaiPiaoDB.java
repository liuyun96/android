package com.yun.spider.db;

import com.yun.spider.bean.Caipiao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class CaiPiaoDB {
	DBHelper dbHelper;

	public CaiPiaoDB(Context context) {
		this.dbHelper = DBHelper.getInstance(context);
	}

	public void insert(Caipiao caipiao) {
		boolean check = dbHelper.checkisExistData(" caipiao where qi_shu = '"
				+ caipiao.getQishu() + "' ");
		if (!check) {
			ContentValues values = new ContentValues();
			values.put(CaiPiaoColumn.QISHU, caipiao.getQishu());
			values.put(CaiPiaoColumn.GE, caipiao.getGe());
			values.put(CaiPiaoColumn.SHI, caipiao.getShi());
			values.put(CaiPiaoColumn.BAI, caipiao.getBai());
			values.put(CaiPiaoColumn.QIAN, caipiao.getQian());
			values.put(CaiPiaoColumn.WAN, caipiao.getWan());
			dbHelper.insert("caipiao", values);
		}
	}

	public Integer maxPeriodGet() {
		Cursor cursor = dbHelper.rawQuery(
				" select qi_shu from  caipiao order by qi_shu desc limit 0,1 ",
				null);
		cursor.moveToFirst();
		int count = cursor.getCount();
		if(count!=0){
			return cursor.getInt(0);
		}
		return null;
	}

}
