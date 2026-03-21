/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.campusdocs.client.util;
 
import com.campusdocs.client.api.ApiException;
import javafx.application.Platform;
import javafx.concurrent.Task;
 
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
 
public class TaskRunner {
 
    // Single shared thread pool — reuse threads instead of creating new ones
    private static final ExecutorService POOL = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true); // don't block app shutdown
        return t;
    });
 
    /**
     * Run a background task safely.
     *
     * @param work      The blocking operation (DB/HTTP call) — runs off JavaFX thread
     * @param onSuccess Called on JavaFX thread with the result
     * @param onError   Called on JavaFX thread with the exception
     */
    
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
    
    public static <T> void run(
        ThrowingSupplier<T> work,
        Consumer<T> onSuccess,
        Consumer<Exception> onError
    ) {
        Task<T> task = new Task<T>() {
            @Override
            protected T call() throws Exception {
                return work.get();
            }
        };
        task.setOnSucceeded(e -> onSuccess.accept(task.getValue()));
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            onError.accept(ex instanceof Exception ? (Exception) ex : new Exception(ex));
        });
        POOL.submit(task);
    }
 
    /** Convenience overload — fire and forget with no result needed */
    public static void run(Runnable work, Runnable onSuccess, Consumer<Exception> onError) {
        run(() -> { work.run(); return null; }, ignored -> onSuccess.run(), onError);
    }
}