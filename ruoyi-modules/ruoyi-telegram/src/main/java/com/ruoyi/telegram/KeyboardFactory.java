package com.ruoyi.telegram;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.util.Arrays;


public class KeyboardFactory {
    public static ReplyKeyboard get7days() {
        KeyboardRow row = new KeyboardRow();
        LocalDate localDate = LocalDate.now().minusDays(7);
        while (true) {
            if (localDate.isAfter(LocalDate.now())) {
                break;
            }
            row.add(">>" + localDate);
        }
        return new ReplyKeyboardMarkup(Arrays.asList(row));
    }

    public static ReplyKeyboard getPizzaOrDrinkKeyboard() {
        KeyboardRow row = new KeyboardRow();
        row.add("Pizza");
        row.add("Drink");
        return new ReplyKeyboardMarkup(Arrays.asList(row));
    }


}
