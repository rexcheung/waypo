package zxb.zweibo.Utils;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by rex on 15-8-28.
 */
public class Snack {
    public static void show(View view, String content){
        Snackbar.make(view, content, Snackbar.LENGTH_SHORT).show();
    }
}
