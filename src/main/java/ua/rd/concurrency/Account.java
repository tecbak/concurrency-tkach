package ua.rd.concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    public static final int INITIAL_VALUE = 0;

    private AtomicInteger failCounter = new AtomicInteger(INITIAL_VALUE);
    private volatile int balance;
    private Lock lock = new ReentrantLock();

    /*Constructor*/
    public Account(int balance) {
        this.balance = balance;
    }

    /*Getters*/
    public int getBalance() {
        return balance;
    }

    public Lock getLock() {
        return lock;
    }

    /*Methods*/
    public void withdraw(int amount) {
        balance -= amount;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public void incFailedTransferCount() {
        failCounter.getAndIncrement();
    }
}
