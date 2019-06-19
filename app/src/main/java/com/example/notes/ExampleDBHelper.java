package com.example.notes;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ExampleDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SQLiteExample.db";
    private static final int DATABASE_VERSION = 1;
    public static final String INPUT_TABLE_NAME = "input";
    public static String INPUT_COLUMN_ID = "_id";
    public static final String INPUT_COLUMN_Title = "title";
    public static final String INPUT_COLUMN_Text = "text";
    public static final String INPUT_COLUMN_DATE = "_text";


    public static final String INPUT_TABLE_NAME_FAB = "input_";
    public static String INPUT_COLUMN_ID_FAB = "_id";
    public static final String INPUT_COLUMN_Title_FAB = "title";
    public static final String INPUT_COLUMN_Text_FAB = "text";
    public static final String INPUT_COLUMN_DATE_FAB = "_text";



    public ExampleDBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + INPUT_TABLE_NAME + "(" +
                INPUT_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                INPUT_COLUMN_Title + " TEXT, " +
                INPUT_COLUMN_Text + " TEXT,"+
                INPUT_COLUMN_DATE + "TEXT )"
        );
        db.execSQL("CREATE TABLE " + INPUT_TABLE_NAME_FAB + "(" +
                INPUT_COLUMN_ID_FAB + " INTEGER PRIMARY KEY, " +
                INPUT_COLUMN_Title_FAB + " TEXT, " +
                INPUT_COLUMN_Text_FAB + " TEXT,"+
                INPUT_COLUMN_DATE_FAB + "TEXT )"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + INPUT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + INPUT_TABLE_NAME_FAB);
        onCreate(db);
    }

    public boolean insertFab(String title, String text,String text_date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(INPUT_COLUMN_Title_FAB, title);
        contentValues.put(INPUT_COLUMN_Text_FAB, text);
        Log.i("Insert Text",text);
        contentValues.put("_textTEXT", text_date);
        db.insert(INPUT_TABLE_NAME_FAB, null, contentValues);
        return true;
    }

    public void deleteSingleFab(String id){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(INPUT_TABLE_NAME_FAB, INPUT_COLUMN_ID_FAB + "=?", new String[]{id});
    }

    public Cursor getFabList() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT DISTINCT * FROM " + INPUT_TABLE_NAME_FAB, null );
        return res;
    }




    public boolean insertPerson(String title, String text,String text_date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(INPUT_COLUMN_Title, title);
        contentValues.put(INPUT_COLUMN_Text, text);
        Log.i("Insert Text",text);
        contentValues.put("_textTEXT", text_date);
        db.insert(INPUT_TABLE_NAME, null, contentValues);
        return true;
    }


    public Cursor getAllPersons() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + INPUT_TABLE_NAME, null );
        return res;
    }


    public Cursor getPerson(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + INPUT_TABLE_NAME + " WHERE " +
                INPUT_COLUMN_ID + "=?", new String[] { id } );
        return res;
    }


    public void deleteSingleContact(String id){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(INPUT_TABLE_NAME, INPUT_COLUMN_ID + "=?", new String[]{id});
    }


}
