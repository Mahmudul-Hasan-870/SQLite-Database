package com.sarkartech.mysql;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class EditDataActivity extends AppCompatActivity {
    private EditText editText;
    private DatabaseHelper dbHelper;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        editText = findViewById(R.id.editText);
        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);

        String currentData = fetchDataFromDatabase(position);
        editText.setText(currentData);

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataInDatabase(editText.getText().toString(), position);
                startActivity(new Intent(EditDataActivity.this,MainActivity.class));
            }
        });
    }

    private String fetchDataFromDatabase(int position) {
        dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.COLUMN_TEXT}, null, null, null, null, null);

        if (cursor.moveToPosition(position)) {
            @SuppressLint("Range") String data = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEXT));
            cursor.close();
            db.close();
            return data;
        } else {
            cursor.close();
            db.close();
            return "";
        }
    }

    private void updateDataInDatabase(String updatedData, int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TEXT, updatedData);

        db.update(DatabaseHelper.TABLE_NAME, values, "ID = ?", new String[]{String.valueOf(position + 1)});
        db.close();
    }
}
