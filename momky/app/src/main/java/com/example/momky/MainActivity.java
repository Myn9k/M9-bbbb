package com.example.momky;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.*;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    public static LinearLayout taskContainer;
    private SharedPreferences sharedPreferences;
    private static final String TASKS_KEY = "tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        taskContainer = findViewById(R.id.task_container);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadTasks();  // Загрузка сохраненных задач
    }

    public void showDialog(View v) {
        CustomDialogFragment dialog = new CustomDialogFragment();
        dialog.show(getSupportFragmentManager(), "custom");
    }

    @SuppressLint("NonConstantResourceId")
    public void addNewTask(String taskName) {
        CheckedTextView newTaskView = createTaskView(taskName, false);
        taskContainer.addView(newTaskView);
        saveTasks();  // Сохраняем задачи после добавления новой
    }

    private CheckedTextView createTaskView(String taskName, boolean isChecked) {
        CheckedTextView taskView = new CheckedTextView(this);
        taskView.setText(taskName);
        taskView.setChecked(isChecked);
        taskView.setCheckMarkDrawable(isChecked ? android.R.drawable.checkbox_on_background : android.R.drawable.checkbox_off_background);
        taskView.setPadding(16, 16, 16, 16);
        taskView.setTextSize(20);
        taskView.setClickable(true);

        taskView.setOnClickListener(v -> {
            if (taskView.isChecked()) {
                taskView.setChecked(false);
                taskView.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
            } else {
                taskView.setChecked(true);
                taskView.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
            }
            saveTasks();  // Сохраняем состояние после изменения
        });

        taskView.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, taskView);
            popupMenu.getMenuInflater().inflate(R.menu.task_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.edit_task) {
                    showEditDialog(taskView);
                    return true;
                } else if (itemId == R.id.delete_task) {
                    taskContainer.removeView(taskView);  // Удаляем задачу
                    saveTasks();  // Сохраняем изменения после удаления
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show();
            return true;
        });

        return taskView;
    }

    public void showEditDialog(CheckedTextView taskView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_listik, null);

        EditText taskInput = dialogView.findViewById(R.id.task_input);
        taskInput.setText(taskView.getText());  // Устанавливаем текст текущей задачи

        builder.setView(dialogView)
                .setTitle("Редактирование задачи")
                .setPositiveButton("Сохранить", (dialog, id) -> {
                    String newTaskName = taskInput.getText().toString();
                    if (!newTaskName.isEmpty()) {
                        taskView.setText(newTaskName);  // Обновляем текст задачи
                        saveTasks();  // Сохраняем задачи после редактирования
                    }
                })
                .setNegativeButton("Отмена", (dialog, id) -> dialog.dismiss());

        builder.create().show();
    }

    private void saveTasks() {
        JSONArray tasksArray = new JSONArray();
        for (int i = 0; i < taskContainer.getChildCount(); i++) {
            CheckedTextView taskView = (CheckedTextView) taskContainer.getChildAt(i);
            JSONObject taskObject = new JSONObject();
            try {
                taskObject.put("text", taskView.getText().toString());
                taskObject.put("checked", taskView.isChecked());
                tasksArray.put(taskObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        sharedPreferences.edit().putString(TASKS_KEY, tasksArray.toString()).apply();
    }

    private void loadTasks() {
        String tasksJson = sharedPreferences.getString(TASKS_KEY, null);
        if (tasksJson != null) {
            try {
                JSONArray tasksArray = new JSONArray(tasksJson);
                for (int i = 0; i < tasksArray.length(); i++) {
                    JSONObject taskObject = tasksArray.getJSONObject(i);
                    String text = taskObject.getString("text");
                    boolean checked = taskObject.getBoolean("checked");
                    CheckedTextView taskView = createTaskView(text, checked);
                    taskContainer.addView(taskView);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
