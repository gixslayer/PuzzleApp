package rnd.puzzleapp.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Threading {

    public static void async(Action task, Action onCompletion) {
        new VoidTaskWrapper(task, onCompletion).execute();
    }

    public static void asyncProgressDialog(Context context, String message, Action task, Action onCompletion) {
        new VoidTaskWrapper(context, message, task, onCompletion).execute();
    }

    public static <T> void async(Supplier<T> task, Consumer<T> onCompletion) {
        new TaskWrapper<>(task, onCompletion).execute();
    }

    public static <T> void asyncProgressDialog(Context context, String message, Supplier<T> task, Consumer<T> onCompletion) {
        new TaskWrapper<>(context, message, task, onCompletion).execute();
    }
}

class VoidTaskWrapper extends AsyncTask<Void, Void, Void> {
    private final Action task;
    private final Action onCompletion;
    private final ProgressDialog dialog;

    VoidTaskWrapper(Action task, Action onCompletion) {
        this.task = task;
        this.onCompletion = onCompletion;
        this.dialog = null;
    }

    VoidTaskWrapper(Context context, String message, Action task, Action onCompletion) {
        this.task = task;
        this.onCompletion = onCompletion;
        this.dialog = Dialog.showProgressDialog(context, message);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        task.perform();

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        onCompletion.perform();

        if(dialog != null) {
            dialog.dismiss();
        }
    }
}

class TaskWrapper<T> extends AsyncTask<Void, Void, T> {
    private final Supplier<T> task;
    private final Consumer<T> onCompletion;
    private final ProgressDialog dialog;

    TaskWrapper(Supplier<T> task, Consumer<T> onCompletion) {
        this.task = task;
        this.onCompletion = onCompletion;
        this.dialog = null;
    }

    TaskWrapper(Context context, String message, Supplier<T> task, Consumer<T> onCompletion) {
        this.task = task;
        this.onCompletion = onCompletion;
        this.dialog = Dialog.showProgressDialog(context, message);
    }

    @Override
    protected T doInBackground(Void... voids) {
        return task.get();
    }

    @Override
    protected void onPostExecute(T result) {
        onCompletion.accept(result);

        if(dialog != null) {
            dialog.dismiss();
        }
    }
}
