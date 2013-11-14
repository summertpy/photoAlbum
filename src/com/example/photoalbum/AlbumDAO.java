package com.example.photoalbum;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AlbumDAO {
	
	static final String dbName = "photodetails";
	static final String detailTable = "details";
	static final String colID = "photoID";
	static final String colEvent = "Event";
	static final String colPlace = "Avenue";
	static final String colDate = "Date";
	static final String colPerson = "Name";
	static final String colPath = "ImagePath";
	
	private DbHelper dbhelper;
	private final Context c;
	private SQLiteDatabase albumDB;
	
	public AlbumDAO(Context context){
		c = context;
	}
	
	public AlbumDAO open(){
		dbhelper = new DbHelper(c);
		albumDB = dbhelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		dbhelper.close();
	}
	
	private static class DbHelper extends SQLiteOpenHelper{
		
		public DbHelper(Context context){
			super(context, dbName, null, 1);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {

			System.out.println("create db");
			db.execSQL("CREATE TABLE "+detailTable+" ("
						+colID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
						+colEvent+" TEXT, "
						+colPlace+" TEXT, "
						+colDate+" TEXT, "
						+colPerson+" TEXT, "
						+colPath+" TEXT)");
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXIST "+ detailTable);
			onCreate(db);
		}
		
	}
	
	public long insertData(String inputEvent, String inputPlace,String inputPerson,String inputPath){
		ContentValues cv = new ContentValues(); 
		cv.put(colEvent, inputEvent);
		cv.put(colPlace, inputPlace);
		cv.put(colPerson, inputPerson);
		cv.put(colPath, inputPath);
		return albumDB.insert(detailTable, null, cv);
		
	}
	
	public String getData(String path){
		String[] column = new String[] {colEvent, colPlace, colDate, colPerson};
		System.out.println("asd2");
		Cursor c = albumDB.query(detailTable, column, colPath+"="+path, null, null, null, null);
		if (c!=null){
			int ievent = c.getColumnIndex(colEvent);
			int iplace = c.getColumnIndex(colPlace);
			int idate = c.getColumnIndex(colDate);
			int iperson = c.getColumnIndex(colPerson);
			c.moveToFirst();
			return c.getString(ievent)+";"+c.getString(iplace)+";"+c.getString(idate)+";"+c.getString(iperson);
		}
		
		return null;
		
	}
	
	public ArrayList<String> getAllImgPath(){
		String[] column = new String[] {colPath, colPerson};
		Cursor c = albumDB.query(detailTable, column, "", null, "", "", "");
		return null;
		
	}
	
	public void deleteData(long id){
		albumDB.delete(detailTable,colID+"="+id,null);
	}
	
	public void updateData(long id, String upEvent, String upPlace,String upPerson,String upPath){
		ContentValues cvUpdate = new ContentValues();
		cvUpdate.put(colEvent, upEvent);
		cvUpdate.put(colPlace, upPlace);
		cvUpdate.put(colPerson, upPerson);
		cvUpdate.put(colPath, upPath);
		albumDB.update(detailTable,cvUpdate,colID+"="+id,null);
	}
	
	
	
}
