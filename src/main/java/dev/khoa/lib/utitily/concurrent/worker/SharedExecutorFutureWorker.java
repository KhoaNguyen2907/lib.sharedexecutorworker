package dev.khoa.lib.utitily.concurrent.worker;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

public class SharedExecutorFutureWorker<T, U> extends BaseSharedExecutorWorker<T, CompletableFuture<U>, FutureTask<T, U>> {
    private final boolean waitOnFuture;

    public SharedExecutorFutureWorker(ExecutorService executor) {
        this(executor, new ConcurrentLinkedQueue(), (Function)null, true);
    }

    public SharedExecutorFutureWorker(ExecutorService executor, Function<T, CompletableFuture<U>> defaultHandler, boolean waitOnFuture) {
        this(executor, new ConcurrentLinkedQueue(), defaultHandler, waitOnFuture);
    }

    SharedExecutorFutureWorker(ExecutorService executor, Queue<FutureTask<T, U>> taskQueue, Function<T, CompletableFuture<U>> defaultHandler, boolean waitOnFuture) {
        super(executor, taskQueue, defaultHandler);
        this.defaultHandler = defaultHandler;
        this.waitOnFuture = waitOnFuture;
    }

    public CompletableFuture<U> submit(T message, Function<T, CompletableFuture<U>> handler) {
        return this.submit(message, handler, false);
    }

    public CompletableFuture<U> submit(T message) {
        return this.submit(message, (Function)null, true);
    }

    private CompletableFuture<U> submit(T message, Function<T, CompletableFuture<U>> handler, boolean allowNullHandler) {
        if (handler == null && !allowNullHandler) {
            throw new IllegalArgumentException("handler cannot be null.");
        } else if (message == null) {
            throw new IllegalArgumentException("message cannot be null.");
        } else {
            CompletableFuture<U> taskResult = new CompletableFuture();
            FutureTask<T, U> newTask = new FutureTask(message, handler, taskResult, this.waitOnFuture);
            this.taskQueue.offer(newTask);
            this.scheduleProcessor((Processor)null);
            return taskResult;
        }
    }
}
