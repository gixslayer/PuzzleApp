package rnd.puzzleapp.utils;

import android.app.AlertDialog;
import android.content.Context;

public class Dialog {

    public static void showConfirmationDialog(Context context, String title, String message, Action onYes) {
        showConfirmationDialog(context, title, message, onYes, () -> {});
    }

    public static void showConfirmationDialog(Context context, String title, String message, Action onYes, Action onNo) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (d, i) -> onYes.perform())
                .setNegativeButton(android.R.string.no, (d, i) -> onNo.perform())
                .show();
    }
}
