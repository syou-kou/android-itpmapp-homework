package com.example.android_itpmapp_homework.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.android_itpmapp_homework.database.ITPMDataOpenHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.telephony.SubscriptionManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android_itpmapp_homework.R;

public class EditActivity extends AppCompatActivity {

    private static final String KEY_ID = "key_id";
    private static final String KEY_TITLE = "key_title";
    private EditText mTitleEditText;
    private int selectedId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.editToolbar);
        setSupportActionBar(toolbar);

        mTitleEditText = findViewById(R.id.titleEditText);
        String title = getIntent().getStringExtra(KEY_TITLE);
        if (title != null) {
            mTitleEditText.setText(title);
        } else {
            mTitleEditText.setText("");
        }

        selectedId = getIntent().getIntExtra(KEY_ID, -1);
        ActionBar actionBar = getSupportActionBar();
        if (selectedId == -1 && actionBar != null) {
            actionBar.setTitle(R.string.a_new);
        } else {
            actionBar.setTitle(R.string.edit);
        }

        FloatingActionButton saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mTitleEditText.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(EditActivity.this, getString(R.string.error_no_title), Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ITPMDataOpenHelper.COLUMN_TITLE, mTitleEditText.getText().toString());
                    SQLiteDatabase itpmDb = new ITPMDataOpenHelper(EditActivity.this).getWritableDatabase();
                    if (selectedId == -1) {
                        itpmDb.insert(
                                ITPMDataOpenHelper.TABLE_NAME,
                                null,
                                contentValues
                        );
                    } else {
                        contentValues.put(ITPMDataOpenHelper._ID, selectedId);
                        itpmDb.update(
                                ITPMDataOpenHelper.TABLE_NAME,
                                contentValues,
                                ITPMDataOpenHelper._ID + "=" + selectedId,
                                null
                        );
                    }
                    itpmDb.close();
                    finish();
                }
            }
        });
    }

    public static Intent createIntent(Context context, int id, String title) {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(KEY_ID, id);
        intent.putExtra(KEY_TITLE, title);
        return intent;
    }

}
