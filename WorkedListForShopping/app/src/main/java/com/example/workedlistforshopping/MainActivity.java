package com.example.workedlistforshopping;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "shopping_list_prefs";
    private static final String KEY_ITEMS = "items";

    private ArrayList<ItemModel> itemList;
    private ItemAdapter adapter;
    private EditText itemInput;
    private TextView itemCounter; // TextView для отображения счетчика

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemInput = findViewById(R.id.item_input);
        EditText searchInput = findViewById(R.id.search_input);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        ImageButton searchButton = findViewById(R.id.search_button);
        itemCounter = findViewById(R.id.item_counter); // Инициализация счетчика

        itemList = loadItems();
        if (itemList == null) {
            itemList = new ArrayList<>();
        }

        adapter = new ItemAdapter(itemList, this, this::saveItems, this::updateItemCounter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.add_button).setOnClickListener(view -> addItem());

        // Обновляем счетчик при запуске
        updateItemCounter();

        // Обработчик кнопки поиска
        searchButton.setOnClickListener(view -> {
            if (searchInput.getVisibility() == View.GONE) {
                searchInput.setVisibility(View.VISIBLE);
            } else {
                searchInput.setVisibility(View.GONE);
                searchInput.setText(""); // Сброс поиска
                adapter.updateList(itemList); // Сброс фильтра
            }
        });

        // Реализация поиска "на лету"
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Не нужно ничего делать здесь
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString()); // Обновляем список
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Не нужно ничего делать здесь
            }
        });
    }

    private void filterItems(String query) {
        ArrayList<ItemModel> filteredList = new ArrayList<>();
        for (ItemModel item : itemList) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList); // Обновление адаптера с отфильтрованным списком
    }

    private void addItem() {
        String itemName = itemInput.getText().toString();
        if (!TextUtils.isEmpty(itemName)) {
            itemList.add(new ItemModel(itemName, false));
            adapter.notifyItemInserted(itemList.size() - 1);
            saveItems(); // Сохраняем изменения
            updateItemCounter(); // Обновляем счетчик
            itemInput.setText("");
        } else {
            Toast.makeText(this, "Введите название товара", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateItemCounter() {
        int purchasedCount = 0;
        for (ItemModel item : itemList) {
            if (item.isPurchased()) {
                purchasedCount++;
            }
        }
        String counterText = purchasedCount + "/" + itemList.size() + " Товаров куплено";
        itemCounter.setText(counterText);
    }

    private void saveItems() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(itemList);
        editor.putString(KEY_ITEMS, json);
        editor.apply();
    }

    private ArrayList<ItemModel> loadItems() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(KEY_ITEMS, null);
        Type type = new TypeToken<ArrayList<ItemModel>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
