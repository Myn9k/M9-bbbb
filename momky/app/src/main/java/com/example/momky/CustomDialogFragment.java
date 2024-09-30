package com.example.momky;


import android.app.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.DialogFragment;
import androidx.annotation.NonNull;

public class CustomDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Создаем AlertDialog через Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Получаем LayoutInflater для создания вида диалога
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_listik, null);

        // Находим EditText для ввода задачи
        EditText taskInput = dialogView.findViewById(R.id.task_input);
        // Находим кнопку "Добавить задачу"

        // Устанавливаем кастомный вид для диалога и задаем заголовок
        builder.setView(dialogView)
                .setTitle("Окно добавления")
                // Обработчик для кнопки "OK"
                .setPositiveButton("OK", (dialog, id) -> {
                    // Получаем введенную задачу
                    String taskName = taskInput.getText().toString();
                    // Если поле ввода не пустое, добавляем задачу в основной список
                    if (!taskName.isEmpty()) {
                        // Вызываем метод addNewTask у MainActivity
                        ((MainActivity) getActivity()).addNewTask(taskName);
                    }
                })
                // Обработчик для кнопки "Отмена" (просто закрывает диалог)
                .setNegativeButton("Отмена", (dialog, id) -> dialog.dismiss());

        // Возвращаем созданный диалог
        return builder.create();
    }
}
