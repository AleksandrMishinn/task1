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

    private static volatile boolean allTasksCompleted = false;
    private static volatile Queue<Integer> taskQueue = new LinkedList<>();
    final static Logger LOGGER = Logger.getLogger(Test.class.getName());

    public static void main(String[] args) {

        Set<Double> result = new HashSet<>();
        List<Thread> threads = new ArrayList<>();

        final Runnable executor = () -> {
            while (!(Test.allTasksCompleted && taskQueue.isEmpty())) {
                Integer currentNumber = taskQueue.poll();
                if (currentNumber != null) {
                    try {
                        Set<Double> currentResult = TestCalc.calculate(currentNumber);
                        result.addAll(currentResult);
                    } catch (TestException e) {
                        LOGGER.warning("Ошибка в вычислениях. Обрабатываемое число: " + currentNumber);
                        e.printStackTrace();
                        break;
                    }
                }
            }
        };

        for (int i = 0; i < TestConsts.MAX_THREADS; i++) {
            Thread currentThread = new Thread(executor);
            threads.add(currentThread);
            currentThread.start();
        }

        for (int i = 0; i < TestConsts.N; i++) {
            taskQueue.add(i);
        }

        allTasksCompleted = true;

        for (Thread currentThread : threads) {
            try {
                currentThread.join();
            } catch (InterruptedException e) {
                LOGGER.warning("Ошибка в одном при исполнении потока " + currentThread);
                e.printStackTrace();
            }
        }
        LOGGER.info(String.valueOf(result));
    }
}
