
package com.yun.spider.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "spider";
	private static final int DB_VERSION = 1;
	private SQLiteDatabase db;
	private static DBHelper mdbHelper;

	/**
	 * 单例模式
	 * 
	 * @param context
	 * @return
	 */
	public static DBHelper getInstance(Context context) {
		if (mdbHelper == null) {
			mdbHelper = new DBHelper(context);
		}
		return mdbHelper;
	}

	private DBHelper(Context context) {
		// 创建数据库
		super(context, DB_NAME, null, DB_VERSION);
	}

	private DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 当数据库首次创建时执行该方法，一般将创建表等初始化操作放在该方法中执行. 重写onCreate方法，调用execSQL方法创建表
	 * */
	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		operateTable(db, "");
		if (!checkisExistData("caipiao"))
			initData();
	}
	
	// 当打开数据库时传入的版本号与当前的版本号不同时会调用该方法
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (oldVersion == newVersion) {
			return;
		}
		operateTable(db, "DROP TABLE IF EXISTS ");
		onCreate(db);
	}

	public void initData() {
		
	}

	public boolean checkisExistData(String tableName) {
		Cursor cursor = this.rawQuery(" select count(*) from  " + tableName,
				null);
		cursor.moveToFirst();
		if (cursor.getLong(0) != 0) {
			return true;
		}
		cursor.close();
		return false;
	}

	// 查询记录的总数
	public long getCount(String tableName) {
		if (db == null)
			db = getWritableDatabase();
		String sql = "select count(*) from " + tableName;
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		long length = c.getLong(0);
		c.close();
		return length;
	}

	/**
	 * 拿到所有的记录条数
	 * 
	 * @param firstResult
	 *            从第几条数据开始查询。
	 * @param maxResult
	 *            每页显示多少条记录。
	 * @return 当前页的记录
	 */
	public Cursor getAllItems(String tableName, int firstResult, int maxResult) {
		if (db == null) {
			db = getWritableDatabase();
		}
		String sql = "select * from " + tableName + " limit ?,?";
		Cursor mCursor = db.rawQuery(
				sql,
				new String[] { String.valueOf(firstResult),
						String.valueOf(maxResult) });
		return mCursor;
	}


	public void operateTable(SQLiteDatabase db, String actionString) {
		Class<DatabaseColumn>[] columnsClasses = DatabaseColumn.getSubClasses();
		DatabaseColumn columns = null;
		for (int i = 0; i < columnsClasses.length; i++) {
			try {
				columns = columnsClasses[i].newInstance();
				if ("".equals(actionString) || actionString == null) {
					db.execSQL(columns.getTableCreateor());
				} else {
					db.execSQL(actionString + columns.getTableName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public long insert(String Table_Name, ContentValues values) {
		if (db == null)
			db = getWritableDatabase();
		return db.insert(Table_Name, null, values);
	}

	/**
	 * 
	 * @param Table_Name
	 * @param id
	 * @return 褰卞搷琛屾暟
	 */
	public int delete(String Table_Name, int id) {
		if (db == null)
			db = getWritableDatabase();
		return db.delete(Table_Name, BaseColumns._ID + "=?",
				new String[] { String.valueOf(id) });
	}

	/**
	 * @param Table_Name
	 * @param values
	 * @param WhereClause
	 * @param whereArgs
	 * @return 褰卞搷琛屾暟
	 */
	public int update(String Table_Name, ContentValues values,
			String WhereClause, String[] whereArgs) {
		if (db == null) {
			db = getWritableDatabase();
		}
		return db.update(Table_Name, values, WhereClause, whereArgs);
	}

	public Cursor query(String Table_Name, String[] columns, String whereStr,
			String[] whereArgs) {
		if (db == null) {
			db = getReadableDatabase();
		}
		return db.query(Table_Name, columns, whereStr, whereArgs, null, null,
				null);
	}

	public Cursor rawQuery(String sql, String[] args) {
		if (db == null) {
			db = getReadableDatabase();
		}
		return db.rawQuery(sql, args);
	}

	public void ExecSQL(String sql) {
		if (db == null) {
			db = getWritableDatabase();
		}
		db.execSQL(sql);
	}

	public void closeDb() {
		if (db != null && db.isOpen()) {
			db.close();
			db = null;
		}
	}

}
