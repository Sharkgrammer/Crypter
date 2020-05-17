package ie.sharkie.cryptr.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ie.sharkie.cryptr.utility.ReadFile;

public class DbControl extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database.db";
    SQLiteDatabase db;
    Context context;

    public DbControl(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean databaseExists() {
        boolean result = false;

        try {
            ReadFile readFile = new ReadFile(context);

            String[] sqlFileAll = readFile.returnAssetAsString("checkTables.sql").split(";");

            Cursor cursor;
            for (String sqlFile : sqlFileAll) {
                cursor = db.rawQuery(sqlFile, null);
                if (cursor.moveToFirst()) {
                    int count = cursor.getInt(0);

                    result = count != 0;
                }
                cursor.close();

                if (!result) break;
            }

        } catch (Exception e) {
            Log.wtf("Error in databaseExists", e.toString());
        }

        return result;
    }

    public boolean createTables() {
        boolean result = false;

        if (!databaseExists()) {
            try {
                ReadFile readFile = new ReadFile(context);

                String[] sqlFileAll = readFile.returnAssetAsString("createTables.sql").split(";");

                for (String sqlFile : sqlFileAll) {
                    db.execSQL(sqlFile);
                }

                result = true;
            } catch (Exception e) {
                Log.wtf("Error in createTables", e.toString());
            }
        }

        return result;
    }

    public boolean deleteTables() {
        boolean result = false;

        try {
            ReadFile readFile = new ReadFile(context);

            String[] sqlFileAll = readFile.returnAssetAsString("deleteTables.sql").split(";");

            for (String sqlFile : sqlFileAll) {
                db.execSQL(sqlFile);
            }

            result = true;
        } catch (Exception e) {
            Log.wtf("Error in deleteTables", e.toString());
        }


        return result;
    }

    public void initialise() {
        createTables();
    }

    public void destroy() {
        db.close();
    }

}
