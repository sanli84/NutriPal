package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class NutriPalDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "nutripal.db";
    private static final int DB_VERSION = 1;
    private static NutriPalDBHelper mHelper = null;
    private static final String TABLE_NAME_USER_INFO = "user_info";

    private SQLiteDatabase mRDB = null;
    private SQLiteDatabase mWDB = null;

    private NutriPalDBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static NutriPalDBHelper getInstance(Context context){
        if(mHelper == null){
            mHelper = new NutriPalDBHelper(context);
        }
        return mHelper;
    }

    public SQLiteDatabase openReadLink(){
        if (mRDB == null || !mRDB.isOpen()){
            mRDB = mHelper.getReadableDatabase();

        }
        return mRDB;
    }

    public SQLiteDatabase openWriteLink(){
        if (mWDB == null || !mWDB.isOpen()){
            mWDB = mHelper.getWritableDatabase();
        }
        return mRDB;
    }

    public void closeLink(){
        if(mRDB != null && mRDB.isOpen()){
            mRDB.close();
            mRDB = null;
        }

        if(mWDB != null && mWDB.isOpen()){
            mWDB.close();
            mWDB = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_USER_INFO + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " name VARCHAR NOT NULL," +
                " age INTEGER NOT NULL," +
                " real_weight INTEGER NOT NULL," +
                " target_weight INTEGER NOT NULL);" ;


        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
