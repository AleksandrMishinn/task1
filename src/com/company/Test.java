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

    private static volatile Integer tasksLeft = TestConsts.N;
    final static Logger LOGGER = Logger.getLogger(Test.class.getName());

    public static void main(String[] args) {

        Set<Double> result = new HashSet<>();
        List<Thread> threads = new ArrayList<>();

        final Runnable executor = () -> {
            while (tasksLeft > 0) {
                if (Thread.interrupted()) {
                    break;
                }

                Integer currentNumber = TestConsts.N - tasksLeft;

                try {
                    Set<Double> currentResult = TestCalc.calculate(currentNumber);
                    synchronized (result) {
                        result.addAll(currentResult);
                    }
                } catch (TestException e) {
                    LOGGER.warning("Ошибка в вычислениях. Обрабатываемое число: " + currentNumber);
                    breakAllThreads(threads);
                }

                synchronized (tasksLeft) {
                    tasksLeft--;
                }
            }
        };

        createThreads(executor, threads);
        startThreads(threads);
        waitAllThreads(threads);

        LOGGER.info(String.valueOf(result));
    }

    private static void breakAllThreads(List<Thread> threads) {
        for (Thread currentThread : threads) {
            currentThread.interrupt();
        }
    }

    private static void createThreads(Runnable executor, List<Thread> threads) {
        for (int i = 0; i < TestConsts.MAX_THREADS; i++) {
            Thread currentThread = new Thread(executor);
            threads.add(currentThread);
        }
    }

    private static void waitAllThreads(List<Thread> threads) {
        for (Thread currentThread : threads) {
            try {
                currentThread.join();
            } catch (InterruptedException e) {
                LOGGER.warning("Ошибка в одном при исполнении потока " + currentThread);
                System.exit(-1);
            }
        }
    }

    private static void startThreads(List<Thread> threads) {
        for (Thread currentThread : threads) {
            currentThread.start();
        }
    }
}
