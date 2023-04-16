package com.p16729438.Collatz;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;

import com.p16729438.Collatz.Thread.CollatzThread;

public class Main {
    private static int ThreadCount;

    private static CollatzThread[] Threads;

    private static final BigInteger TWO = BigInteger.TWO;

    private static final BigInteger THREE = new BigInteger("3");

    private static ArrayList<BigInteger> PowNumbers = new ArrayList<>();

    public static void main(String[] args) {
        ThreadCount = Runtime.getRuntime().availableProcessors();
        Threads = new CollatzThread[ThreadCount];
        System.out.println("Thread Count: " + ThreadCount);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int a = 4;
        int b = 2;
        PowNumbers.add(TWO.pow(0));
        PowNumbers.add(TWO.pow(1));
        PowNumbers.add(TWO.pow(2));
        PowNumbers.add(TWO.pow(3));
        PowNumbers.add(TWO.pow(4));
        BigInteger p = new BigInteger("16");
        BigInteger q = new BigInteger("9");
        int i = 0;
        while (true) {
            while (p.compareTo(q) != 1) {
                a++;
                p = p.multiply(TWO);
                PowNumbers.add(p);
                b = 2;
                q = new BigInteger("9");
            }
            if (!isFileExist(a, b)) {
                if (i == ThreadCount) {

                    i = 0;
                }
                if (Threads[i] == null) {
                    Threads[i] = new CollatzThread(i + 1, a, b, p, q, PowNumbers);
                    Threads[i].start();
                    b++;
                    q = q.multiply(THREE);
                    continue;
                }
                int returnCode = Threads[i].getReturnCode();
                if (returnCode == 0) {
                    Threads[i].stopWork();
                    Threads[i] = new CollatzThread(i + 1, a, b, p, q, PowNumbers);
                    (new Thread((Runnable) Threads[i])).start();
                    b++;
                    q = q.multiply(THREE);
                    continue;
                }
                if (returnCode == 1) {
                    i++;
                    continue;
                }
                if (returnCode == 2) {
                    for (int k = 0; k < ThreadCount; k++)
                        Threads[k].stopWork();
                    return;
                }
                continue;
            }
            b++;
            q = q.multiply(THREE);
        }
    }

    private static boolean isFileExist(int a, int b) {
        createFolder();
        File file = new File("data/" + a + "." + b + ".txt");
        return (file.exists() && file.isFile());
    }

    private static void createFolder() {
        File folder = new File("data");
        if (!folder.exists() || !folder.isDirectory())
            folder.mkdir();
    }
}