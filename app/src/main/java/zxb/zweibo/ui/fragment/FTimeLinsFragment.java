package zxb.zweibo.ui.fragment;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.utils.LogUtil;

import java.util.ArrayList;

import zxb.zweibo.R;
import zxb.zweibo.adapter.FTimeLinsAdapter;
import zxb.zweibo.bean.FTimeLine;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.common.AccessTokenKeeper;
import zxb.zweibo.common.Constants;
import zxb.zweibo.common.JsonCacheUtil;
import zxb.zweibo.listener.OnBottomListener;


/**
 * 显示FriendsTimeLine最新关注用户的微博
 * Created by rex on 15-7-31.
 */
public class FTimeLinsFragment extends Fragment {

    /**
     * 初始化时传入的父类Activity, LayoutInflater需要使用
     */
    private static Activity mContext;
    private static int mPosition;

    /**
     * 新浪SDK.
     */
    private Oauth2AccessToken mAccessToken;
    private StatusesAPI mStatusesAPI;

    /**
     * 接收最近10条微博的实体类.
     */
    private FTimeLine mFTimeLine;

    /**
     * 实体类中的10条微博.
     */
    private ArrayList<StatusContent> mStatusesList;

    private String TAG;

    private LinearLayoutManager llm;
    private RecyclerView mRecyclerView;

    private int page;

    private FTimeLinsAdapter mAdapter;

    /**
     * 是否在初始化.
     */
    private boolean isInit = true;

    /**
     * 判断是否正在刷新列表.
     */
    private boolean isRefresing = false;


    /**
     * 初始化.
     * @param position 位置.
     * @param content  Content.
     * @return
     */
    public static FTimeLinsFragment newInstance(int position, Activity content) {
        FTimeLinsFragment fragment = new FTimeLinsFragment();
        mPosition = position;
        mContext = content;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, null);

        init(view);

        return view;
    }

    private void init(View view) {
        llm = new LinearLayoutManager(mContext);

        TAG = getClass().getSimpleName();

        mAccessToken = AccessTokenKeeper.readAccessToken(mContext);
        mStatusesAPI = new StatusesAPI(mContext, Constants.APP_KEY, mAccessToken);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvContent);
        mRecyclerView.setLayoutManager(llm);

        if(isInit == true){
            sendRequest();
        }

//        readJsonCache();
    }

    private void readJsonCache(){
        JsonCacheUtil jsonCacheUtil = new JsonCacheUtil(mContext);
        mStatusesList = jsonCacheUtil.read(mAccessToken.getUid());
        initDatas();
    };


    private void initDatas() {
        mStatusesList = mFTimeLine.getStatuses();

        mAdapter = FTimeLinsAdapter.newInstance(mContext, mStatusesList);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setOnScrollListener(new OnBottomListener(llm) {
            @Override
            public void onBottom() {
                if (isRefresing == false) {
                    sendRequest();
                    isRefresing = true;
                    Log.i(TAG, "Now refreshing...");
                }
            }
        });
    }

    private void sendRequest(){
        mStatusesAPI.friendsTimeline(0L, 0L, 20, ++page, false, 0, false, mListener);
    }

    private RequestListener mListener = new RequestListener() {

        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                refresh(response);

//                sqliteDemo(response);
//                jsonobject
            }
        }

        private void sqliteDemo(String response) {
            SQLiteOpenHelper sqliteHelper = new DBHelper(mContext, "test", null, 1);
            SQLiteDatabase db = sqliteHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("json", response);
            db.insert("jsonobject",null,values);
        }

        @Override
        public void onWeiboException(WeiboException e) {
            isRefresing = false;
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(mContext, info.toString(), Toast.LENGTH_LONG).show();
        }

        class DBHelper extends SQLiteOpenHelper {

            public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
                super(context, name, factory, version);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                String sql = "create table jsonobject(json varchar(65535) not null);";
                db.execSQL(sql);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        }
    };

    Gson mGson = new Gson();
    /**
     * 如果是第一次初始化，则运行initDatas()
     * 否则刷新RecycleView
     * @param response
     */
    private void refresh(String response) {
        mFTimeLine = mGson.fromJson(response, FTimeLine.class);

//        JsonCacheUtil jsonCacheUtil = new JsonCacheUtil(mContext);
//        mStatusesList = jsonCacheUtil.read(mAccessToken.getUid());
        if (isInit == true) {
            initDatas();
            isInit = false;
//            jsonCacheUtil.write(mAccessToken.getUid(), mStatusesList);
            return;
        }

        for (StatusContent sc : mFTimeLine.getStatuses()){
            mStatusesList.add(sc);
        }
        mAdapter.notifyDataSetChanged();


//        jsonCacheUtil.write(mAccessToken.getUid(), mStatusesList);
        isRefresing = false;
        Log.i(TAG, "Refresh finish");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.cleanCache();
        }
        Log.i(getClass().getSimpleName(), "onPause()");
    }
}
