package com.example.photoalbum;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AlbumDAO {

	static final String dbName = "photodetail";
	static final String detailTable = "photo";
	static final int dbVer = 1;
	static final String colID = "photoID";
	static final String colEvent = "Event";
	static final String colPlace = "Avenue";
	static final String colDate = "Date";
	static final String colPerson = "Name";
	static final String colPath = "ImagePath";

	private DbHelper dbhelper;
	private final Context c;
	private SQLiteDatabase albumDB;

	public AlbumDAO(Context context) {
		c = context;
	}

	public AlbumDAO open() {
		dbhelper = new DbHelper(c);
		albumDB = dbhelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbhelper.close();
	}

	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, dbName, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL("CREATE TABLE " + detailTable + " (" + colID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + colEvent
					+ " TEXT, " + colPlace + " TEXT, " + colDate + " TEXT, "
					+ colPerson + " TEXT, " + colPath + " TEXT)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE " + detailTable);
			onCreate(db);
		}

	}

	public long insertData(String inputPath) {
		ContentValues cv = new ContentValues();
		cv.put(colPath, inputPath);
		long id = albumDB.insert(detailTable, null, cv);
		String[] column = new String[] {colID, colEvent, colPlace, colDate, colPerson, colPath};
		Cursor c = albumDB.query(detailTable, column, null,	null, null, null, null);
		Log.d("inside method insertdata testing",""+c.getCount());
		return id;
	}

	public String getData(String path) {
		String[] column = new String[] { colEvent, colPlace, colDate, colPerson };
		Log.d("inside method getdata",path);
		Cursor c = albumDB.query(detailTable, column, colPath + "='" + path+"'",
				null, null, null, null);
		if (c != null) {
			Log.d("getdata method cursor row", ""+c.getCount());
			int ievent = c.getColumnIndex(colEvent);
			int iplace = c.getColumnIndex(colPlace);
			int idate = c.getColumnIndex(colDate);
			int iperson = c.getColumnIndex(colPerson);
			c.moveToFirst();
			//return "asd;qwe;wer;rty";
			return c.getString(ievent) + ";" + c.getString(iplace) + ";"+ c.getString(idate) + ";" + c.getString(iperson);
		}

		return null;

	}

	public ArrayList<String> getAllImgPath() {
		String[] column = new String[] { colPath, colPerson };
		Cursor c = albumDB.query(detailTable, column, null, null, null, null, null);
		if (c != null) {
			Log.d("getallimgpath method- cursor count", ""+c.getCount());
			int iPath = c.getColumnIndex(colPath);
			ArrayList<String> paths = new ArrayList<String>();
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				paths.add(c.getString(iPath));
			}
			return paths;
		}

		return null;
	}

	public void deleteData(String path) {
		Log.d("deleteData method", ""+albumDB.delete(detailTable, colPath + "='" + path + "'", null));
	}

	public void updateData(String path, String upEvent, String upPlace,
			String upDate, String upPerson) {
		ContentValues cvUpdate = new ContentValues();
		cvUpdate.put(colEvent, upEvent);
		cvUpdate.put(colPlace, upPlace);
		cvUpdate.put(colDate, upDate);
		cvUpdate.put(colPerson, upPerson);
		albumDB.update(detailTable, cvUpdate, colPath + "='" + path + "'", null);
	}

}
