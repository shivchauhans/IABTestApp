package incube8.iabtestapp.sqlitehelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import incube8.iabtestapp.model.MemberDetailsBean;

/**
 * Created by shiv on 29/3/17.
 */
public class SqliteMemberDetails extends SQLiteOpenHelper {

    
    public static final String DATABASE_NAME = "IABTestApp.db";
    public static final String TABLE_MEMBER = "member_details";
    public static final String COLUMN_NAME = "member_name";
    public static final String COLUMN_EMAIL = "member_email";

    public SqliteMemberDetails(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_MEMBER +
                        "(" + COLUMN_NAME + " text," + COLUMN_EMAIL + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBER);
        onCreate(db);
    }


    /**
     * insert row in database (name and email)
     *
     * @param name  A variable of type String holding name of member
     * @param email A variable of type String holding email address of member
     */
    public void insertDetails(String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_EMAIL, email);
        db.insert(TABLE_MEMBER, null, contentValues);
    }


    /**
     * return all rows from table member_details
     *
     * @return arraylist A collection of type ArrayList
     */
    public ArrayList<MemberDetailsBean> getMemberDetails() {
        Cursor res = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String[] args = new String[]{COLUMN_NAME, COLUMN_EMAIL};
            res = db.rawQuery("SELECT * FROM " + TABLE_MEMBER, null);
            if (res != null) {
                res.moveToFirst();
                ArrayList<MemberDetailsBean> arrayList = new ArrayList<>();
                while (!res.isAfterLast()) {
                    String name = res.getString(res.getColumnIndex(COLUMN_NAME));
                    String email = res.getString(res.getColumnIndex(COLUMN_EMAIL));
                    MemberDetailsBean bean = new MemberDetailsBean();
                    bean.setName(name);
                    bean.setEmail(email);
                    arrayList.add(bean);
                    res.moveToNext();
                }
                res.close();
                return arrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList();
    }

}
