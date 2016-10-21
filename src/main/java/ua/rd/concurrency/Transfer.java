package ua.rd.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Transfer implements Callable<Boolean> {
    private static final int WAIT_SEC = 1;

    private Account accountFrom;
    private Account accountTo;
    private int amount;

    public Transfer(Account accountFrom, Account accountTo, int amount) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    @Override
    public Boolean call() throws Exception {

        try {
            if (accountFrom.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                try {
                    checkFundsAmount();
                    if (accountTo.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                        try {

                            accountFrom.withdraw(amount);
                            accountTo.deposit(amount);
                            Thread.sleep(100);
                            return true;

                        } finally {
                            accountTo.getLock().unlock();
                        }
                    } else {
                        accountTo.incFailedTransferCount();
                        return false;
//                        System.out.println("failed(2) to lock " + accountTo);
                    }
                } finally {
                    accountFrom.getLock().unlock();
                }
            } else {
                accountFrom.incFailedTransferCount();
                return false;
//                System.out.println("failed(1) to lock " + accountFrom);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void checkFundsAmount() {
        if (accountFrom.getBalance() < amount)
            throw new InsufficientFundsException();
    }
}
