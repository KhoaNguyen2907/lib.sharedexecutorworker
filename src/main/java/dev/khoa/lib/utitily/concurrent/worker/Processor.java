package dev.khoa.lib.utitily.concurrent.worker;

import java.util.function.Function;
import java.util.function.Supplier;

class Processor<D, V, T extends Task<D, V>> implements Runnable {
    private final BaseSharedExecutorWorker<D, V, T> worker;

    public Processor(BaseSharedExecutorWorker<D, V, T> worker) {
        this.worker = worker;
    }

    public void run() {
        Function<D, V> beforeDefaultHandler = this.worker.getDefaultHandler();
        Task<D, V> task = this.worker.peek();
        Function<D, V> func = null;

        if (task != null) {
            func = task.getFunc();
            if (func != null) {
                this.worker.remove(task);
                task.execute(this);
            } else {
                this.worker.processorDone();
            }
        } else {
            this.worker.processorDone();
        }
    }

    public void next() {
        this.worker.next(this);
    }
}
