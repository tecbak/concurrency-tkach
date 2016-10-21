package ua.rd.concurrency;

import java.util.concurrent.TimeUnit;

public class Operations {

    public static final int WAIT_SEC = 1;

    public static void main(String[] args) {
        final Account a = new Account(1000);
        final Account b = new Account(2000);

        System.out.println(a.getBalance());
        System.out.println(b.getBalance());

        new Thread(new Runnable() {
            @Override
            public void run() {
                transfer(a, b, 500);
            }
        }).start();
        transfer(b, a, 300);


        System.out.println(a.getBalance());
        System.out.println(b.getBalance());
    }

    public static void transfer(Account acc1, Account acc2, int amount) {
        if (acc1.getBalance() < amount)
            throw new InsufficientFundsException();

        try {
            if (acc1.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                try {
                    if (acc2.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                        try {

                            acc1.withdraw(amount);
                            acc2.deposit(amount);

                        } finally {
                            acc2.getLock().unlock();
                        }
                    } else {
                        acc2.incFailedTransferCount();
                        System.out.println("failed(2) to lock " + acc2);
                    }
                } finally {
                    acc1.getLock().unlock();
                }
            } else {
                acc1.incFailedTransferCount();
                System.out.println("failed(1) to lock " + acc1);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//            synchronized (acc1) {
//                System.out.println("Taken lock on " + acc1);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                synchronized (acc2) {
//                    System.out.println("Taken lock on " + acc2  );
//                    acc1.withdraw(amount);
//                    acc2.deposit(amount);
//                }
//            }
    }
}
