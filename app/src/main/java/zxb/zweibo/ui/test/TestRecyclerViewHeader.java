package zxb.zweibo.ui.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import zxb.zweibo.R;

/**
 * Created by rex on 15-8-22.
 */
public class TestRecyclerViewHeader extends AppCompatActivity{
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.listComment) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);
        ButterKnife.bind(this);

        toolbar.setTitle("Comments");
        setSupportActionBar(toolbar);

        addHeader();
    }

    private void addHeader() {
//        Header
    }

    class Header{
        TextView textView;
    }
}
