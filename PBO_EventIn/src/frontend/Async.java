package frontend;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

/** Menjalankan query database di luar EDT supaya UI tidak membeku. */
public final class Async {

    private Async() {
    }

    public static <T> void run(Callable<T> work, Consumer<T> onSuccess, Consumer<Exception> onError) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return work.call();
            }

            @Override
            protected void done() {
                try {
                    onSuccess.accept(get());
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() == null ? ex : ex.getCause();
                    onError.accept(cause instanceof Exception ? (Exception) cause : new Exception(cause));
                }
            }
        }.execute();
    }
}