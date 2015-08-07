package zxb.zweibo.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rex on 15-8-6.
 */
public class DBUtil {

    Context mContext;
    SQLiteOpenHelper mdbHelper;

    public DBUtil(Context context){
        this.mContext = context;
        mdbHelper = new DBHelper(mContext, "test", null, 1);
    }
}
