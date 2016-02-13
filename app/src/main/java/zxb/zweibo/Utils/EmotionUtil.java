package zxb.zweibo.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;

import zxb.zweibo.GlobalApp;
import zxb.zweibo.bean.EmotionBean;
import zxb.zweibo.common.AccessTokenKeeper;
import zxb.zweibo.common.Constants;
import zxb.zweibo.common.WeiboAPIUtils;
import zxb.zweibo.db.SqliteHelper;

/**
 * 表情的JSON缓存，
 * 新浪微博会一次把一个系列的表情都发过来，
 * 基本上头一次连网，之后就使用缓存就可以了。
 * Created by rex on 15-8-15.
 */
public class EmotionUtil {

//    private final String TABLE = "emocache.db";

//    private Oauth2AccessToken mAccessToken;
    private Context mContext;
    private SQLiteOpenHelper mdbHelper;
    private SQLiteDatabase db;

    public static final String TABLE = "waypo_emotions";
    public static final String KEY = "KEY";
    public static final String FILE = "FILE";
    public static final String VALUE = "VALUE";

    Gson gson;

    private String TAG = getClass().getSimpleName();

    public EmotionUtil(Context context){
        this.mContext = context;
        initDB();
    }

    public EmotionUtil(Context context, Gson gson) {
        this.mContext = context;
        this.gson = gson;
//        mAccessToken = new Oauth2AccessToken();
//        mAccessToken = AccessTokenKeeper.readAccessToken(context);
    }

    public void initDB() {
        Log.i(TAG, "+++初始化EmotionCache.db");
        if (mdbHelper == null) {
            mdbHelper = SqliteHelper.getInstance();
        }
        db = mdbHelper.getWritableDatabase();
    }

    public void closeDB() {
        if (db.isOpen()) {
            db.close();
        }

        mdbHelper.close();
        Log.i(TAG, "---关闭数据库EmotionCache.db");
    }

    /**
     * 根据KEY获取表情的图片
     * @param emo 表情的名称
     * @return 表情的byte[]，需要转换
     */
    public static byte[] getEmotion(String emo) {
        byte[] result = null;

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(EmotionTable.VALUE);
        sql.append(" FROM ");
        sql.append(EmotionTable.TABLE);
        sql.append(" WHERE ");
        sql.append(EmotionTable.KEY);
        sql.append(" = ? ");

        Cursor cursor = SqliteHelper.getInstance().getReadableDatabase().rawQuery(sql.toString(), new String[]{emo});
        if (cursor.moveToNext()){
            result = cursor.getBlob(0);
        }
        cursor.close();

        return result;
    }

    public void insertEmotions(){
        InputStream in;
        try {
            Resources resources = GlobalApp.getInstance().getResources();
            in = resources.getAssets().open("emotions.properties");
            Properties properties = new Properties();
            properties.load(new InputStreamReader(in, "utf-8"));
            Set<Object> keySet = properties.keySet();

            // 开启事务
            db.beginTransaction();
            db.execSQL(String.format("delete from %s", EmotionTable.TABLE));
            for (Object key : keySet) {
                String value = properties.getProperty(key.toString());
                Log.w(TAG, String.format("emotion's KEY(%s), VALUE(%s)", key, value));

                ContentValues values = new ContentValues();
                values.put(EmotionTable.KEY, key.toString());
                byte[] emotion = FileUtils.readStreamToBytes(resources.getAssets().open(value));
                values.put(EmotionTable.VALUE, emotion);
                values.put(EmotionTable.FILE, value);

                db.insert(EmotionTable.TABLE, null, values);
            }
            // 结束事务
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class EmotionTable {
        static final String TABLE = "waypo_emotions";
        static final String KEY = "KEY";
        static final String FILE = "FILE";
        static final String VALUE = "VALUE";
    }
}
