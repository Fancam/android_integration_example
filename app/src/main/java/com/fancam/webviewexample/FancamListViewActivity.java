package com.fancam.webviewexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;


public class FancamListViewActivity extends AppCompatActivity {

    private ArrayList<Fancam> mFancamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        ListView mListView = (ListView) findViewById(R.id.listview_fancams);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mFancamList != null && (position < mFancamList.size())) {
                    Intent i = new Intent(FancamListViewActivity.this,FancamWebViewActvity.class);
                    i.putExtra("url", mFancamList.get(position).url);
                    startActivity(i);
                }
            }
        });

        mFancamList = Fancam.getFancamsFromFile("fancams.json", this);
        String[] listItems = new String[mFancamList.size()];

        for(int i = 0; i < mFancamList.size(); i++){
            Fancam fancam = mFancamList.get(i);
            listItems[i] = fancam.title;
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
        mListView.setAdapter(adapter);
    }


}
