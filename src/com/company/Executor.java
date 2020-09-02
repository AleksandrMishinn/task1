package com.company;

import java.util.Set;

public class Executor implements Runnable {

    private static volatile boolean calculationError = false;

    @Override
    public void run() {
        int currentNumber;
        while ((currentNumber = Test.getNumberOfTasks()) > 1 && !getCalculationError()) {
            try {
                Set<Double> currentResult = TestCalc.calculate(currentNumber);
                Test.setResult(currentResult);
            } catch (TestException e) {
                setCalculationError(true);
                break;
            }
        }
    }

    private synchronized boolean getCalculationError() {
        return calculationError;
    }

    private synchronized void setCalculationError(boolean value) {
        calculationError = value;
    }
}
