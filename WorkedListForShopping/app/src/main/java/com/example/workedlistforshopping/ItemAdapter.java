package com.example.workedlistforshopping;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private ArrayList<ItemModel> items;
    private Context context;
    private Runnable saveCallback; // Колбэк для сохранения данных
    private Runnable updateCounterCallback; // Колбэк для обновления счётчика

    public ItemAdapter(ArrayList<ItemModel> items, Context context, Runnable saveCallback, Runnable updateCounterCallback) {
        this.items = items;
        this.context = context;
        this.saveCallback = saveCallback;
        this.updateCounterCallback = updateCounterCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemModel item = items.get(position);
        holder.itemName.setText(item.getName());
        holder.checkBox.setChecked(item.isPurchased());
        updateTextStyle(holder.itemName, item.isPurchased());

        // Обработка изменения состояния CheckBox
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setPurchased(isChecked);
            updateTextStyle(holder.itemName, isChecked);
            saveCallback.run(); // Сохраняем изменения
            updateCounterCallback.run(); // Обновляем счётчик
        });

        // Обработка нажатия кнопки удаления
        holder.deleteButton.setOnClickListener(v -> {
            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size());
            saveCallback.run(); // Сохраняем изменения
            updateCounterCallback.run(); // Обновляем счётчик
        });

        // Обработка нажатия кнопки редактирования
        holder.editButton.setOnClickListener(v -> {
            showEditDialog(item, position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateList(ArrayList<ItemModel> newList) {
        this.items = newList;
        notifyDataSetChanged();
        updateCounterCallback.run(); // Обновляем счётчик при изменении списка
    }

    private void updateTextStyle(TextView textView, boolean isPurchased) {
        if (isPurchased) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setTextColor(context.getResources().getColor(android.R.color.black));
        }
    }

    private void showEditDialog(ItemModel item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Редактировать элемент");

        final EditText input = new EditText(context);
        input.setText(item.getName());
        builder.setView(input);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                item.setName(newName);
                notifyItemChanged(position);
                saveCallback.run(); // Сохраняем изменения
            }
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        CheckBox checkBox;
        ImageButton deleteButton;
        ImageButton editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            checkBox = itemView.findViewById(R.id.checkbox);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}
