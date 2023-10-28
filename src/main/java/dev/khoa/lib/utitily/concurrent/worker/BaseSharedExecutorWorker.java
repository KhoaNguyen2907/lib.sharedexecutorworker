package dev.khoa.lib.utitily.concurrent.worker;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

abstract class BaseSharedExecutorWorker<D, V, T extends Task<D, V> > {
    protected final Queue<T> taskQueue;
    protected final ExecutorService executor;
    protected Processor<D, V, T> proccesor;
    protected final Object processorLock = new Object();
    protected volatile Function<D, V> defaultHandler;

    BaseSharedExecutorWorker(ExecutorService executor, Queue<T> taskQueue, Function<D, V> defaultHandler) {
        this.taskQueue = taskQueue;
        this.executor = executor;
        this.defaultHandler = defaultHandler;
    }

    public int clear() {
        int removeCount;
        for(removeCount = 0; !this.taskQueue.isEmpty(); ++removeCount) {
            ((Task)this.taskQueue.remove()).getTaskFuture().cancel(false);
        }

        return removeCount;
    }

    public void setDefaultHandler(Function<D, V> defaultHandler) {
        if (defaultHandler == null) {
            throw new IllegalArgumentException("defaultHandler cannot be null. Use removeDefaultHandler if you really mean it.");
        } else {
            this.defaultHandler = defaultHandler;
            if (!this.taskQueue.isEmpty()) {
                this.scheduleProcessor((Processor)null);
            }

        }
    }

    public Function<D, V> getDefaultHandler() {
        return this.defaultHandler;
    }

    protected void scheduleProcessor(Processor currentProcessor) {
        if (this.proccesor == null) {
            synchronized(this.processorLock) {
                if (this.proccesor == null) {
                    this.proccesor = currentProcessor != null ? currentProcessor : new Processor(this);
                    this.executor.execute(this.proccesor);
                }
            }
        }

    }

    void next(Processor callingProcessor) {
        this.proccesor = null;
        if (!this.taskQueue.isEmpty()) {
            this.scheduleProcessor(callingProcessor);
        }

    }

    Task<D, V> peek() {
        return (Task)this.taskQueue.peek();
    }

    void remove(Task<D, V> task) {
        this.taskQueue.remove(task);
    }

    void processorDone() {
        this.proccesor = null;
    }
}
