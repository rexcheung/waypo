package zxb.zweibo.listener;

import android.content.Context;
import android.widget.Toast;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * Created by rex on 15-8-9.
 */
public abstract class WeiboRequestListener implements RequestListener {
    private Context mContext;

    private final String TAG = getClass().getSimpleName();

    public WeiboRequestListener(Context context){
        this.mContext = context;
    }

    @Override
    public void onComplete(String s) {
        onSuccess(s);
    }

    @Override
    public void onWeiboException(WeiboException e) {
        LogUtil.e(TAG, e.getMessage());

        ErrorInfo info = ErrorInfo.parse(e.getMessage());
        Context context = this.mContext;
//        Toast.makeText(mContext, info != null ? info.toString() : "微博请求失败，请检查网络", Toast.LENGTH_LONG).show();
        LogUtil.e(TAG, "微博请求失败，请检查网络");
    }

    protected abstract void onSuccess(String response);
}
