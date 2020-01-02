package com.example.android_itpmapp_homework.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.CaseMap;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.android_itpmapp_homework.R;
import com.example.android_itpmapp_homework.database.ITPMDataOpenHelper;
import com.example.android_itpmapp_homework.pojo.TitleDataItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private MainListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        mListView = findViewById(R.id.mainList);
        mAdapter = new MainListAdapter(this, R.layout.layout_title_item, new ArrayList<TitleDataItem>());
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                TitleDataItem item = (TitleDataItem)adapterView.getItemAtPosition(position);
                Intent intent = EditActivity.createIntent(MainActivity.this, item.getId(), item.getTitle());
                startActivity(intent);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                TitleDataItem item = (TitleDataItem)adapterView.getItemAtPosition(position);
                SQLiteDatabase itpmDb = new ITPMDataOpenHelper(MainActivity.this).getWritableDatabase();
                itpmDb.delete(
                        ITPMDataOpenHelper.TABLE_NAME,
                        ITPMDataOpenHelper._ID + "=" + item.getId(),
                        null
                );
                itpmDb.close();
                new AllDataLoadTask().execute();
                Toast.makeText(MainActivity.this, item.getTitle() + "を削除しました", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        FloatingActionButton insertBtn = findViewById(R.id.insertBtn);
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AllDataLoadTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayDataList(List<TitleDataItem> titleDataItems) {
        mAdapter.clear();
        mAdapter.addAll(titleDataItems);
        mAdapter.notifyDataSetChanged();
    }

    private class MainListAdapter extends ArrayAdapter<TitleDataItem> {
        private LayoutInflater layoutInflater;
        private int resource;
        private List<TitleDataItem> titleDataItems;

        public MainListAdapter(@NonNull Context context, int resource, @NonNull List<TitleDataItem> objects) {
            super(context, resource, objects);
            this.layoutInflater = LayoutInflater.from(context);
            this.resource = resource;
            this.titleDataItems = objects;
        }

        @Override
        public int getCount() {
            return titleDataItems.size();
        }

        @Nullable
        @Override
        public TitleDataItem getItem(int position) {
            return titleDataItems.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TitleDataItem item = titleDataItems.get(position);
            TextView titleTextView;

            if (convertView == null) {
                convertView = layoutInflater.inflate(resource, null);
                titleTextView = convertView.findViewById(R.id.titleTextView);
                convertView.setTag(titleTextView);
            } else {
                titleTextView = (TextView)convertView.getTag();
            }
            titleTextView.setText(item.getTitle());

            return convertView;
        }
    }

    private class AllDataLoadTask extends AsyncTask<Void, Void, List<TitleDataItem>> {

        @Override
        protected List<TitleDataItem> doInBackground(Void... voids) {
            List<TitleDataItem> titleDataItems = new ArrayList<>();
            SQLiteDatabase itpmDb = new ITPMDataOpenHelper(MainActivity.this).getWritableDatabase();
            Cursor itpmDbCursor = itpmDb.query(
                    ITPMDataOpenHelper.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            while (itpmDbCursor.moveToNext()) {
                int id = itpmDbCursor.getInt(itpmDbCursor.getColumnIndex(ITPMDataOpenHelper._ID));
                String title = itpmDbCursor.getString(itpmDbCursor.getColumnIndex(ITPMDataOpenHelper.COLUMN_TITLE));
                titleDataItems.add(new TitleDataItem(id, title));
            }
            itpmDbCursor.close();
            itpmDb.close();
            return titleDataItems;
        }

        @Override
        protected void onPostExecute(List<TitleDataItem> titleDataItems) {
            displayDataList(titleDataItems);
        }
    }
}
