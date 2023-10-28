package dev.khoa.lib.utitily.concurrent.worker;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

public class SharedExecutorWorker<T, U> extends BaseSharedExecutorWorker<T, U, Task<T, U>> {
    public SharedExecutorWorker(ExecutorService executor) {
        this(executor, new ConcurrentLinkedQueue(), (Function)null);
    }

    public SharedExecutorWorker(ExecutorService executor, Function<T, U> defaultHandler) {
        this(executor, new ConcurrentLinkedQueue(), defaultHandler);
    }

    SharedExecutorWorker(ExecutorService executor, Queue<Task<T, U>> taskQueue, Function<T, U> defaultHandler) {
        super(executor, taskQueue, defaultHandler);
        this.defaultHandler = defaultHandler;
    }

    public CompletableFuture<U> submit(T message, Function<T, U> handler) {
        return this.submit(message, handler, false);
    }

    public CompletableFuture<U> submit(T message) {
        return this.submit(message, (Function)null, true);
    }

    private CompletableFuture<U> submit(T message, Function<T, U> handler, boolean allowNullHandler) {
        if (handler == null && !allowNullHandler) {
            throw new IllegalArgumentException("handler cannot be null.");
        } else if (message == null) {
            throw new IllegalArgumentException("message cannot be null.");
        } else {
            CompletableFuture<U> taskResult = new CompletableFuture();
            Task<T, U> newTask = new Task(message, handler, taskResult);
            this.taskQueue.offer(newTask);
            this.scheduleProcessor(null);
            return taskResult;
        }
    }

}
