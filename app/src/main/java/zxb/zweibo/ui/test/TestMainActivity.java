package zxb.zweibo.ui.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import zxb.zweibo.R;

/**
 * 用于写测试类的MainActivity
 * Created by rex on 15-7-29.
 */
public class TestMainActivity extends Activity{

    String AUTH = "OAuth2";
    String TIME_LINE = "TimeLine";
    String BIT_MAP = "BitMap";
    String VOLLEY_CACHE = "Volley cache test";
    String[] datas = {AUTH, TIME_LINE, BIT_MAP, VOLLEY_CACHE};
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listview_activity);

        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(
                new MyAdapter(this, android.R.layout.simple_list_item_1, datas));
        listView.setOnItemClickListener(new ItemClick());
    }

    class ItemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Class<?> cls = null;
            String item = datas[position];

            if (AUTH.equals(item)) {
                cls = TestAuthActivity.class;
            } else if (TIME_LINE.equals(item)){
                cls = TestFTimeLinesActivity.class;
            } else if (BIT_MAP.equals(item)) {
                cls = TestBitmapActivity.class;
            } else if (VOLLEY_CACHE.equals(item)) {
                cls = TestVolleyCache.class;
            }

            Intent intent = new Intent(TestMainActivity.this, cls);
            startActivity(intent);
        }
    }

    class MyAdapter extends ArrayAdapter{

        public MyAdapter(Context context, int resource, Object[] objects) {
            super(context, resource, objects);
        }
    }
}
