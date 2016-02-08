package zxb.zweibo.Utils;

import android.mtp.MtpObjectInfo;
import android.widget.Toast;

import zxb.zweibo.GlobalApp;

/**
 * Created by Rex.Zhang on 2016/2/8.
 */
public class Toastutils {
    private static Toast mToast;
    static {
        mToast = Toast.makeText(GlobalApp.getInstance(), "", Toast.LENGTH_SHORT);
    }

    public void s(String msg){
        mToast.setText(msg);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }
    public void l(String msg){
        mToast.setText(msg);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }


}
