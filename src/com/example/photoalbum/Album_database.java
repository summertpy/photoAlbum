package com.example.photoalbum;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Album_database extends SQLiteOpenHelper {
	
	static final String dbName = "photodetails";
	static final String detailTable = "details";
	static final String colID = "photoID";
	static final String colEvent = "Event";
	static final String colPlace = "Avenue";
	static final String colDate = "Date";
	static final String colPerson = "Name";
	static final String colPath = "ImagePath";
		
	public Album_database(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE"+detailTable+"("+colID+"INTEGER PRIMARY KEY, "+colEvent+"TEXT,"+colPlace+" TEXT, "+colDate+" DATE, "+colPerson+" TEXT, "+colPath+" TEXT)");
		
	}
	
	public long AcceptInput(String inputEvent, String inputPlace,String inputPerson,String inputPath){
		SQLiteDatabase db1 = Album_database.this.getWritableDatabase();
		ContentValues cv = new ContentValues(); 
		cv.put(colEvent, inputEvent);
		cv.put(colPlace, inputPlace);
		cv.put(colPerson, inputPerson);
		cv.put(colPath, inputPath);
		return db1.insert(detailTable, null, cv);
		
	}
	
	public String getData(String path){
		String[] column = new String[] {colEvent, colPlace, colDate, colPerson};
		Cursor c = Album_database.this.getReadableDatabase().query(detailTable, column, colPath+"="+path, null, null, null, null);
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
		String[] column = new String[] {colPath};
		Cursor c = Album_database.this.getReadableDatabase().query(detailTable, column, null, null, null, null, null);
		if (c!=null){
			int iPath = c.getColumnIndex(colPath);
			ArrayList<String> paths = new ArrayList<String>();
			for(c.moveToFirst(); !c.isAfterLast();c.moveToNext()){
				paths.add(c.getString(iPath));
			}
			return paths;
		}
		
		
		return null;
		
	}
	
	public void deleteData(long id){
		SQLiteDatabase db2 = Album_database.this.getWritableDatabase();
		db2.delete(detailTable,colID+"="+id,null);
	}
	
	public void updateData(long id, String upEvent, String upPlace,String upPerson,String upPath){
		ContentValues cvUpdate = new ContentValues();
		cvUpdate.put(colEvent, upEvent);
		cvUpdate.put(colPlace, upPlace);
		cvUpdate.put(colPerson, upPerson);
		cvUpdate.put(colPath, upPath);
		SQLiteDatabase db3 = Album_database.this.getWritableDatabase();
		db3.update(detailTable,cvUpdate,colID+"="+id,null);
	}
	
	
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	
}
