package com.company;

import java.util.*;
import java.util.logging.Logger;

/**
 * Необходимо уменьшить время выполнения вычислений.
 * <p>
 * Можно изменять этот класс или добавить новый. Решение должно быть максимально простым, без использования экзекуторов.
 * (Runnable, Threads). Аккуратно обработайте потенциальные ошибки (разумеется вычисления если появились ошибки нужно
 * остановить). Количество потоков должно быть ограничено значением константы com.company.TestConsts#MAX_THREADS.
 */
public class Test {
    private static volatile int tasksLeft = TestConsts.N;
    private static Set<Double> result = new HashSet<>();
    final static Logger LOGGER = Logger.getLogger(Test.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = createThreads();
        startThreads(threads);
        waitExecutorsThreads(threads);
        LOGGER.info(String.valueOf(result));
    }

    static synchronized int getNumberOfTasks() {
        return tasksLeft--;
    }

    static synchronized void setResult(Set<Double> threadsResult) {
        result.addAll(threadsResult);
    }

    private static Thread[] createThreads() {
        Thread[] threads = new Thread[TestConsts.MAX_THREADS];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Executor());
        }
        return threads;
    }

    private static void startThreads(Thread[] threads) {
        for (Thread currentThread : threads) {
            currentThread.start();
        }
    }

    private static void waitExecutorsThreads(Thread[] threads) throws InterruptedException {
        for (Thread currentThread : threads) {
            currentThread.join();
        }
    }
}
