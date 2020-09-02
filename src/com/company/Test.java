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

    private static volatile Integer taskLeft = TestConsts.N;
    final static Logger LOGGER = Logger.getLogger(Test.class.getName());

    public static void main(String[] args) {

        Set<Double> result = new HashSet<>();
        List<Thread> threads = new ArrayList<>();

        final Runnable executor = () -> {

            while (taskLeft > 0) {

                if (Thread.interrupted()) {
                    break;
                }

                int currentNumber = TestConsts.N - taskLeft;

                try {
                    Set<Double> currentResult = TestCalc.calculate(currentNumber);

                    synchronized (result) {
                        result.addAll(currentResult);
                    }
                    synchronized (taskLeft) {
                        taskLeft--;
                    }

                } catch (TestException e) {
                    LOGGER.warning("Ошибка в вычислениях. Обрабатываемое число: " + currentNumber);
                    for (Thread currentThread : threads) {
                        currentThread.interrupt();
                    }
                }
            }
        };

        for (int i = 0; i < TestConsts.MAX_THREADS; i++) {
            Thread currentThread = new Thread(executor);
            threads.add(currentThread);
            currentThread.start();
        }

        for (Thread currentThread : threads) {
            try {
                currentThread.join();
            } catch (InterruptedException e) {
                LOGGER.warning("Ошибка в одном при исполнении потока " + currentThread);
                System.exit(-1);
            }
        }
        LOGGER.info(String.valueOf(result));
    }
}
