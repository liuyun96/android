package com.yun.spider.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class CaiPiaoService {
	DBHelper dbHelper;

	public CaiPiaoService(Context context) {
		this.dbHelper = DBHelper.getInstance(context);
	}

	public void insert(String qishu, Integer ge, Integer shi, Integer bai,
			Integer qian, Integer wan) {
		boolean check = dbHelper.checkisExistData(" caipiao where qi_shu = '"
				+ qishu + "' ");
		if (!check) {
			ContentValues values = new ContentValues();
			values.put(CaiPiaoColumn.QISHU, qishu);
			values.put(CaiPiaoColumn.GE, ge);
			values.put(CaiPiaoColumn.SHI, shi);
			values.put(CaiPiaoColumn.BAI, bai);
			values.put(CaiPiaoColumn.QIAN, qian);
			values.put(CaiPiaoColumn.WAN, wan);
			dbHelper.insert("caipiao", values);
		}
	}

	public Integer maxPeriodGet() {
		Cursor cursor = dbHelper.rawQuery(
				" select qi_shu from  caipiao limit 0,1 order by qi_shu desc ",
				null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

}
