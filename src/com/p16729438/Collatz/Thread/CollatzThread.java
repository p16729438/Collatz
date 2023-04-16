package com.p16729438.Collatz.Thread;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CollatzThread extends Thread {
    private long StartTime;
    private long EndTime;

    private int ID;

    private int ReturnCode = 0;

    private final BigInteger ZERO = BigInteger.ZERO;
    private final BigInteger ONE = BigInteger.ONE;
    private final BigInteger TWO = BigInteger.TWO;
    private final BigInteger THREE = new BigInteger("3");

    private int a;
    private int b;

    private BigInteger p;
    private BigInteger q;

    private ArrayList<BigInteger> PowNumbers;

    private ArrayList<String> Logs = new ArrayList<>();

    public CollatzThread(int id, int a, int b, BigInteger p, BigInteger q, ArrayList<BigInteger> powNumbers) {
        this.ID = id;
        this.a = a;
        this.b = b;
        this.p = p;
        this.q = q;
        this.PowNumbers = powNumbers;
        ReturnCode = 1;
        StartTime = System.nanoTime();
        System.out.println(getTimeStamp() + "[Thread-" + String.format("%05d", ID) + "] a: " + a + ", b: " + b + " / Start");
    }

    public void run() {
        if (isNExist(a, b, p, q, "", a, b - 1)) {
            EndTime = System.nanoTime();
            System.out.println(getTimeStamp() + "[Thread-" + String.format("%05d", ID) + "] a: " + a + ", b: " + b + " / Loop Found (time: " + ((EndTime - StartTime) / 1000000) + ")");
            init(2);
            return;
        }
        createFile(a, b);
        writeData(a, b);
        EndTime = System.nanoTime();
        System.out.println(getTimeStamp() + "[Thread-" + String.format("%05d", ID) + "] a: " + a + ", b: " + b + " / End (time: " + ((EndTime - StartTime) / 1000000) + ")");
        init(0);
    }

    private void init(int returnCode) {
        PowNumbers = null;
        Logs.clear();
        ReturnCode = returnCode;
    }

    public int getReturnCode() {
        return ReturnCode;
    }

    private boolean isNExist(int a, int b, BigInteger p, BigInteger q, String str, int n, int r) {
        if (r == 1) {
            String zero = "0";
            StringBuilder builder = new StringBuilder(zero.repeat(n));
            for (int i = 0; i < n; i++) {
                builder.setCharAt(i, '1');
                String newStr = String.valueOf(str) + builder.toString();
                builder.setCharAt(i, '0');
                BigInteger k = getK(newStr);
                if (check(a, b, newStr, k, p, q)) {
                    return true;
                }
            }
            return false;
        }
        if (n == r) {
            String zero = "1";
            String newStr = String.valueOf(str) + zero.repeat(n);
            BigInteger k = getK(newStr);
            return check(a, b, newStr, k, p, q);
        }
        if (isNExist(a, b, p, q, "1" + str, n - 1, r - 1)) {
            return true;
        }
        if (isNExist(a, b, p, q, "0" + str, n - 1, r)) {
            return true;
        }
        return false;
    }

    private BigInteger getK(String str) {
        BigInteger k = ONE;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '1') {
                k = k.multiply(THREE).add(PowNumbers.get(i + 1));
            }
        }
        return k;
    }

    private boolean check(int a, int b, String str, BigInteger k, BigInteger p, BigInteger q) {
        if (k.remainder(p.subtract(q)).compareTo(BigInteger.ZERO) == 0) {
            BigInteger n = k.divide(p.subtract(q));
            if (n.compareTo(BigInteger.ONE) != 0) {
                BigInteger N = n;
                int i = 0;
                Logs.add("+========================================+");
                Logs.add("a:" + a + ",   b:" + b);
                Logs.add("str:" + str);
                Logs.add("k:" + k.toString());
                Logs.add("Condition Satisfaction");
                Logs.add("n:" + n.toString());
                while (N.compareTo(BigInteger.ONE) != 0) {
                    N = col(N);
                    i++;
                    if (N.compareTo(n) == 0) {
                        Logs.add("Loop found in step " + i);
                        Logs.add("+========================================+");
                        return true;
                    }
                }
                Logs.add("No loop found");
                Logs.add("+========================================+");
                return false;
            }
        }
        return false;
    }

    private BigInteger col(BigInteger N) {
        if (N.remainder(TWO).compareTo(ZERO) == 0) {
            return N.divide(TWO);
        }
        return N.multiply(THREE).add(ONE);
    }

    private String getTimeStamp() {
        return new SimpleDateFormat("[yyyy-MM-dd (EEE)  a hh:mm:ss]     ").format(new Date());
    }

    private boolean isFileExist(int a, int b) {
        createFolder();
        File file = new File("data/" + a + "." + b + ".txt");
        return (file.exists() && file.isFile());
    }

    private void createFile(int a, int b) {
        createFolder();
        if (!isFileExist(a, b)) {
            File file = new File("data/" + a + "." + b + ".txt");
            try {
                file.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void writeData(int a, int b) {
        try {
            File file = new File("data/" + a + "." + b + ".txt");
            FileWriter w = new FileWriter(file);
            for (String log : Logs) {
                w.append(String.valueOf(log) + "\n");
            }
            w.flush();
            w.close();
            Logs.clear();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void createFolder() {
        File folder = new File("data");
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdir();
        }
    }
}