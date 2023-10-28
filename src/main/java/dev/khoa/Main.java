package dev.khoa;

import dev.khoa.lib.utitily.concurrent.worker.SharedExecutorWorker;

import java.util.concurrent.*;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(20);

        // Test SharedExecutorWorker
        SharedExecutorWorker<String, Integer> worker1 = new SharedExecutorWorker<>(executor);
        SharedExecutorWorker<String, Integer> worker2 = new SharedExecutorWorker<>(executor);

        Function<String, Integer> stringLengthFunction = s -> {
            int length = s.length();
            System.out.println("Message: " + s);
            System.out.println("From thread: " + Thread.currentThread().getName());
            System.out.println();
            return length;
        };

        CompletableFuture<Integer> cf1 = worker1.submit("Task 1 - Worker 1", stringLengthFunction);
        CompletableFuture<Integer> cf2 = worker1.submit("Task 2 - Worker 1", stringLengthFunction);
        CompletableFuture<Integer> cf3 = worker2.submit("Task 11 - Worker 2", stringLengthFunction);
        CompletableFuture<Integer> cf4 = worker2.submit("Task 22 - Worker 2", stringLengthFunction);


        try {
            int value1 = cf1.get();
            int value2 = cf2.get();
            int value3 = cf3.get();
            int value4 = cf4.get();

            System.out.println("Result 1: " + value1);
            System.out.println("Result 2: " + value2);
            System.out.println("Result 3: " + value3);
            System.out.println("Result 4: " + value4);
        } catch (Exception e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}