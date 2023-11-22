package com.sarkartech.mysql;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DataListAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DataListAdapter(this, fetchDataFromDatabase());
        recyclerView.setAdapter(adapter);
    }

    private List<String> fetchDataFromDatabase() {
        dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> dataList = new ArrayList<>();

        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.COLUMN_TEXT}, null, null, null, null, null);

        while (cursor.moveToNext()) {
            @SuppressLint("Range") String data = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEXT));
            dataList.add(data);
        }

        cursor.close();
        db.close();

        return dataList;
    }

    public void deleteDataFromDatabase(int position) {
        dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Delete the data at the specified position.
        db.delete(DatabaseHelper.TABLE_NAME, "ID = ?", new String[]{String.valueOf(position + 1)});
        db.close();

        // Refresh the RecyclerView to reflect the changes.
        adapter.notifyDataSetChanged();
    }
}
