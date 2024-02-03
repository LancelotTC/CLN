package com.example.cln;

import android.app.Dialog;

public class CurrentDialog {
    public static Dialog dialog;

    public static Dialog getDialog() {
        return dialog;
    }

    public static void setDialog(Dialog dialog) {
        CurrentDialog.dialog = dialog;
    }
}
