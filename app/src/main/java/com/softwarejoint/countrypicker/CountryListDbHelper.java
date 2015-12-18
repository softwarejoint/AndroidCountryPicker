package com.softwarejoint.countrypicker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

public class CountryListDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "country_list.sqlite";
    private static CountryListDbHelper sCountryListDbHelper;
    private Context mycontext;

    /**
     * {@link Constructor}
     *
     * @param context
     */
    private CountryListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mycontext = context;
        boolean dbexist = checkdatabase();

        if (!dbexist) {
            Log.e("db not ", "exists");
            System.out.println("Database doesn't exist");
            try {
                this.getReadableDatabase();
                this.close();
                copydatabase();
            } catch (IOException e) {
                Log.e("db not ", "IO exceptionm" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public static synchronized CountryListDbHelper getInstance(Context context) {
        if (sCountryListDbHelper == null) {
            sCountryListDbHelper = new CountryListDbHelper(context);
        }

        return sCountryListDbHelper;
    }

    private boolean checkdatabase() {
        boolean checkdb = false;
        try {
            String myPath = mycontext.getDatabasePath(DATABASE_NAME).toString();
            File dbfile = new File(myPath);
            checkdb = dbfile.exists();
            Log.e("In check db" + myPath, "not exist" + checkdb);
        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e("In check db", "not exist exception");
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    private void copydatabase() throws IOException {
        // Open your local db as the input stream
        InputStream myinput = null;
        OutputStream myoutput = null;
        byte[] buffer = new byte[1024];
        int length;
        try {
            myinput = mycontext.getResources().openRawResource(R.raw.country_list);
            myoutput = new FileOutputStream(mycontext.getDatabasePath(DATABASE_NAME).toString());
            while ((length = myinput.read(buffer)) > 0) {
                myoutput.write(buffer, 0, length);
            }
            myoutput.flush();
            myoutput.close();
            myinput.close();
            //myinput = mycontext.getAssets().open(DATABASE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor fetchQuery(String query) {
        final SQLiteDatabase readableDatabase = getReadableDatabase();
        final Cursor cursor = readableDatabase.rawQuery(query, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }
}
