package rnd.puzzleapp.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

public class Dialog {

    /**
     * Creates and shows a new confirmation dialog.
     * @param context the context to show the dialog in
     * @param title the title of the dialog
     * @param message the message of the dialog
     * @param onYes the action to perform if the user clicked the yes button
     */
    public static void showConfirmationDialog(Context context, String title, String message, Action onYes) {
        showConfirmationDialog(context, title, message, onYes, () -> {});
    }

    /**
     * Creates and shows a new confirmation dialog.
     * @param context the context to show the dialog in
     * @param title the title of the dialog
     * @param message the message of the dialog
     * @param onYes the action to perform if the user clicked the yes button
     * @param onNo the action to perform if the user clicked the no button
     */
    public static void showConfirmationDialog(Context context, String title, String message, Action onYes, Action onNo) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (d, i) -> onYes.perform())
                .setNegativeButton(android.R.string.no, (d, i) -> onNo.perform())
                .show();
    }

    /**
     * Creates and shows a new progress dialog that displays for an indeterminate time. Note that the
     * caller is responsible for calling {@code dismiss} on the returned dialog.
     * @param context the context to show the dialog in
     * @param message the message of the dialog
     * @return the created dialog, which must be dismissed by the caller.
     */
    public static ProgressDialog showProgressDialog(Context context, String message) {
        ProgressDialog dialog = new ProgressDialog(context);

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        return dialog;
    }
}
