package com.p16729438.Collatz;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;

import com.p16729438.Collatz.Thread.CollatzThread;

public class Collatz {
    private int ThreadCount;

    private CollatzThread[] threads;

    private final BigInteger TWO = new BigInteger("2");
    private final BigInteger THREE = new BigInteger("3");

    private int a;
    private int b;

    private BigInteger p;
    private BigInteger q;

    private ArrayList<BigInteger> PowNumbers = new ArrayList<>();

    public Collatz() {
        init();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        start();
    }

    private void init() {
        ThreadCount = Runtime.getRuntime().availableProcessors();
        threads = new CollatzThread[ThreadCount];
        System.out.println("Thread Count: " + ThreadCount);

        a = 4;
        b = 2;

        PowNumbers.add(TWO.pow(0));
        PowNumbers.add(TWO.pow(1));
        PowNumbers.add(TWO.pow(2));
        PowNumbers.add(TWO.pow(3));
        PowNumbers.add(TWO.pow(4));

        p = new BigInteger("16");
        q = new BigInteger("9");
    }

    private void start() {
        int index = 0;
        while (true) {
            while (p.compareTo(q) != 1) {
                a++;
                p = p.multiply(TWO);
                PowNumbers.add(p);
                b = 2;
                q = new BigInteger("9");
            }
            if (!isFileExist(a, b)) {
                if (index == ThreadCount) {

                    index = 0;
                }
                if (threads[index] == null) {
                    threads[index] = new CollatzThread(index, a, b, p, q, PowNumbers);
                    threads[index].start();
                    b++;
                    q = q.multiply(THREE);
                    continue;
                }
                int returnCode = threads[index].getReturnCode();
                if (returnCode == 0) {
                    threads[index].interrupt();
                    threads[index] = new CollatzThread(index, a, b, p, q, PowNumbers);
                    threads[index].start();
                    b++;
                    q = q.multiply(THREE);
                    continue;
                }
                if (returnCode == 1) {
                    index++;
                    continue;
                }
                if (returnCode == 2) {
                    for (int k = 0; k < ThreadCount; k++)
                        threads[k].interrupt();
                    return;
                }
                continue;
            }
            b++;
            q = q.multiply(THREE);
        }
    }

    private boolean isFileExist(int a, int b) {
        createFolder();
        File file = new File("data/" + a + "." + b + ".txt");
        return (file.exists() && file.isFile());
    }

    private void createFolder() {
        File folder = new File("data");
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdir();
        }
    }
}