package com.sarkartech.mysql;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DataListAdapter extends RecyclerView.Adapter<DataListAdapter.DataViewHolder> {
    private List<String> dataList;
    private Context context;
    private DatabaseHelper dbHelper;

    public DataListAdapter(Context context, List<String> dataList) {
        this.context = context;
        this.dataList = dataList;
        dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String data = dataList.get(position);
        holder.textView.setText(data);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the editing activity.
                Intent intent = new Intent(context, EditDataActivity.class);
                intent.putExtra("position", position); // Pass the position of the item to edit.
                context.startActivity(intent);
            }
        });

        // Set a click listener on the delete button to show a confirmation dialog.
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageButton deleteButton;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete this data?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            if (deleteDataFromDatabase(position)) {
                showToast("Data deleted successfully!");
            } else {
                showToast("Failed to delete data.");
            }
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            // User canceled the deletion, so do nothing.
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean deleteDataFromDatabase(int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            // Delete the data at the specified position.
            db.delete(DatabaseHelper.TABLE_NAME, "ID = ?", new String[]{String.valueOf(position + 1)});
            db.close();

            // Remove the deleted item from the list and notify the adapter of the change.
            dataList.remove(position);
            notifyItemRemoved(position);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
            return false;
        }
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
