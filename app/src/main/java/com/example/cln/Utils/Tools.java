package com.example.cln.Utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.EditText;

/**
 * Utility class. No usages
 */
public class Tools {
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static void setTextEmpty(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setText("");
        }
    }
}
