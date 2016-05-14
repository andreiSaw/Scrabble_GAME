package com.scrabble;

import java.util.ArrayList;
import java.util.List;

public class rusBag extends engBag {
    private String letters = "АААААААА" +
            "ББ" +
            "ВВВВ" +
            "ГГ" +
            "ДДДД" +
            "ЕЕЕЕЕЕЕЕ" +
            "Ё" +
            "Ж" +
            "ЗЗ" +
            "ИИИИИ" +
            "Й" +
            "КККК" +
            "ЛЛЛЛ" +
            "МММ" +
            "ННННН" +
            "ОООООООООО" +
            "ПППП" +
            "РРРРР" +
            "ССССС" +
            "ТТТТТ" +
            "УУУУ" +
            "Ф" +
            "Х" +
            "Ц" +
            "Ч" +
            "Ш" +
            "Щ" +
            "Ъ" +
            "ЫЫ" +
            "Ь" +
            "Э" +
            "Ю" +
            "ЯЯ";

    private int count = -1;
    private List<String> list;

    public rusBag() {
        list = new ArrayList<>();
    }

    @Override
    public void load() {
        String context = letters;
        String temp = "";
        while (context.length() > 0) {
            temp = context.substring(0, 1);
            list.add(temp.toLowerCase());
            context = context.substring(1);
        }
    }

    @Override
    protected String getLettersToString(int count) {
        return super.getLettersToString(count);
    }

    @Override
    protected int getCount() {
        makeCount();
        return count;
    }

    @Override
    protected void pushLetters(String letters) {
        while (letters.length() > 0) {
            String x = "" + letters.charAt(0);
            list.add(x);
            letters = letters.substring(1);
        }
    }

    @Override
    protected void makeCount() {
        count = list.size();
    }

}
