package dev.khoa.lib.utitily.concurrent.worker;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

class Task<D, V> {
    protected final D data;
    protected final CompletableFuture taskFuture;
    protected Function<D, V> func;

    public Task(D data, Function<D, V> func, CompletableFuture taskFuture) {
        this.data = data;
        this.func = func;
        this.taskFuture = taskFuture;
    }

    public CompletableFuture getTaskFuture() {
        return this.taskFuture;
    }

    public Function<D, V> getFunc() {
        return this.func;
    }

    public void setFunc(Function<D, V> func) {
        this.func = func;
    }

    void execute(Processor processor) {
        try {
            V result = this.func.apply(this.data);
            this.taskFuture.complete(result);
        } catch (Exception var6) {
            this.taskFuture.completeExceptionally(var6);
        } finally {
            processor.next();
        }

    }

    public D getData() {
        return this.data;
    }
}
