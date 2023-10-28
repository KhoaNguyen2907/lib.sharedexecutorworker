package dev.khoa.lib.utitily.concurrent.worker;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

class FutureTask<D, V> extends Task<D, CompletableFuture<V>> {
    private final boolean waitOnFuture;

    public FutureTask(D data, Function<D, CompletableFuture<V>> func, CompletableFuture taskFuture, boolean waitOnFuture) {
        super(data, func, taskFuture);
        this.waitOnFuture = waitOnFuture;
    }

    void execute(Processor processor) {
        try {
            CompletableFuture<V> result = (CompletableFuture)this.func.apply(this.data);
            result.whenComplete((u, t) -> {
                try {
                    if (t != null) {
                        this.taskFuture.completeExceptionally(t);
                    } else {
                        this.taskFuture.complete(u);
                    }
                } finally {
                    if (this.waitOnFuture) {
                        processor.next();
                    }

                }

            });
        } catch (Exception var6) {
            this.taskFuture.completeExceptionally(var6);
        } finally {
            if (!this.waitOnFuture) {
                processor.next();
            }

        }

    }
}
