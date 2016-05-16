package com.activity_pack;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.tools.R;

public class WordsListActivity extends AppCompatActivity {

    String _wordsString = "aa\n" +
            "ab\n" +
            "ad\n" +
            "ae\n" +
            "ag\n" +
            "ah\n" +
            "ai\n" +
            "al\n" +
            "am\n" +
            "an\n" +
            "ar\n" +
            "as\n" +
            "at\n" +
            "aw\n" +
            "ax\n" +
            "ay\n" +
            "ba\n" +
            "be\n" +
            "bi\n" +
            "bo\n" +
            "by\n" +
            "de\n" +
            "do\n" +
            "ed\n" +
            "ef\n" +
            "eh\n" +
            "el\n" +
            "em\n" +
            "en\n" +
            "er\n" +
            "es\n" +
            "et\n" +
            "ex\n" +
            "fa\n" +
            "fe\n" +
            "go\n" +
            "ha\n" +
            "he\n" +
            "hi\n" +
            "hm\n" +
            "ho\n" +
            "id\n" +
            "if\n" +
            "in\n" +
            "is\n" +
            "it\n" +
            "jo\n" +
            "ka\n" +
            "ki\n" +
            "la\n" +
            "li\n" +
            "lo\n" +
            "ma\n" +
            "me\n" +
            "mi\n" +
            "mm\n" +
            "mo\n" +
            "mu\n" +
            "my\n" +
            "na\n" +
            "ne\n" +
            "no\n" +
            "nu\n" +
            "od\n" +
            "oe\n" +
            "of\n" +
            "oh\n" +
            "oi\n" +
            "om\n" +
            "on\n" +
            "op\n" +
            "or\n" +
            "os\n" +
            "ow\n" +
            "ox\n" +
            "oy\n" +
            "pa\n" +
            "pe\n" +
            "pi\n" +
            "qi\n" +
            "re\n" +
            "sh\n" +
            "si\n" +
            "so\n" +
            "ta\n" +
            "ti\n" +
            "to\n" +
            "uh\n" +
            "um\n" +
            "un\n" +
            "up\n" +
            "us\n" +
            "ut\n" +
            "we\n" +
            "wo\n" +
            "xi\n" +
            "xu\n" +
            "ya\n" +
            "ye\n" +
            "yo\n" +
            "za";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_list);
        load();
    }

    static String[] words_array;

    private void load() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activivtywordsrelative);
        if (words_array == null) {
            words_array = _wordsString.split("\n");
        }
        int i = 0;
        int k = 0;
        int previousId = 0;
        int idFisrtTop = 0, idSecondTop = 0, idThirdTop = 0;
        for (String c : words_array) {
            Button bt = new Button(this);
            bt.setText(c);
            bt.setId(View.generateViewId());

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            if (i % 2 == 0) {

                bt.setBackgroundResource(R.color.colorLightBlueTile);
            } else {
                bt.setBackgroundResource(R.color.colorLightGreenTile);
            }

            params.setMargins(5, 5, 5, 5);

            if (i == 0) {
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.ALIGN_LEFT);
                idFisrtTop = bt.getId();
            } else if ((i == 1)) {
                idSecondTop = bt.getId();
                params.addRule(RelativeLayout.RIGHT_OF, previousId);
            } else if (i == 2) {
                idThirdTop = bt.getId();
                params.addRule(RelativeLayout.RIGHT_OF, previousId);
                k = -1;
            } else {

                switch (k) {
                    case 0:
                        params.addRule(RelativeLayout.BELOW, idFisrtTop);
                        params.addRule(RelativeLayout.ALIGN_LEFT);
                        idFisrtTop = bt.getId();
                        break;
                    case 1:
                        params.addRule(RelativeLayout.BELOW, idSecondTop);
                        params.addRule(RelativeLayout.RIGHT_OF, previousId);
                        idSecondTop = bt.getId();
                        break;
                    case 2:
                        params.addRule(RelativeLayout.BELOW, idThirdTop);
                        params.addRule(RelativeLayout.RIGHT_OF, previousId);
                        k = -1;
                        idThirdTop = bt.getId();
                        break;
                }
            }
            bt.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));
            previousId = bt.getId();
            i++;
            k++;
            bt.setLayoutParams(params);
            relativeLayout.addView(bt);
        }
    }
}
