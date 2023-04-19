package com.p16729438.Collatz.Thread;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import com.p16729438.Collatz.Util.CollatzUtil;

public class CollatzThread extends Thread {
    private int ID;

    private int ReturnCode = 0;// 0: 준비, 1: 실행, 2: 완료

    private final BigInteger ZERO = new BigInteger("0");
    private final BigInteger ONE = new BigInteger("1");
    private final BigInteger TWO = new BigInteger("2");
    private final BigInteger THREE = new BigInteger("3");

    private int a;
    private int b;

    private BigInteger p;
    private BigInteger q;

    private ArrayList<BigInteger> PowNumbers;

    private ArrayList<String> Logs = new ArrayList<>();

    public CollatzThread(int id, int a, int b, BigInteger p, BigInteger q, ArrayList<BigInteger> powNumbers) {
        ReturnCode = 1;
        this.ID = id;
        this.a = a;
        this.b = b;
        this.p = p;
        this.q = q;
        this.PowNumbers = powNumbers;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        System.out.println(CollatzUtil.getTimeStamp() + "[Thread-" + String.format("%05d", ID) + "] a: " + a + ", b: " + b + " / Start");
        if (isNExist(new boolean[a], a, b - 1, 0)) {
            long endTime = System.currentTimeMillis();
            System.out.println(CollatzUtil.getTimeStamp() + "[Thread-" + String.format("%05d", ID) + "] a: " + a + ", b: " + b + " / Loop Found (time: " + (endTime - startTime) + "ms)");
            setReturnCode(2);
            return;
        }
        writeData();
        long endTime = System.currentTimeMillis();
        System.out.println(CollatzUtil.getTimeStamp() + "[Thread-" + String.format("%05d", ID) + "] a: " + a + ", b: " + b + " / End (time: " + (endTime - startTime) + "ms)");
        setReturnCode(0);
    }

    private boolean isNExist(boolean[] recipe, int n, int r, int index) {
        if (r == 1) {
            for (; index < recipe.length; index++) {
                recipe[index] = true;
                boolean[] recipeClone = recipe.clone();
                recipe[index] = false;
                BigInteger k = getK(recipeClone);
                if (check(recipeClone, k)) {
                    return true;
                }
            }
            return false;
        }
        if (n == r) {
            for (; index < recipe.length; index++) {
                recipe[index] = true;
            }
            BigInteger k = getK(recipe.clone());
            return check(recipe.clone(), k);
        }
        recipe[index] = true;
        if (isNExist(recipe.clone(), n - 1, r - 1, index + 1)) {
            return true;
        }
        recipe[index] = false;
        if (isNExist(recipe.clone(), n - 1, r, index + 1)) {
            return true;
        }
        return false;
    }

    private BigInteger getK(boolean[] recipe) {
        BigInteger k = ONE;
        for (int i = 0; i < recipe.length; i++) {
            if (recipe[i]) {
                k = k.multiply(THREE).add(PowNumbers.get(i + 1));
            }
        }
        return k;
    }

    private boolean check(boolean[] recipe, BigInteger k) {
        if (k.remainder(p.subtract(q)).compareTo(BigInteger.ZERO) == 0) {
            BigInteger n = k.divide(p.subtract(q));
            if (n.compareTo(BigInteger.ONE) != 0) {
                BigInteger N = n;
                int i = 0;
                Logs.add("+========================================+");
                Logs.add("a: " + a + ",   b: " + b);
                Logs.add("recipe: " + Arrays.toString(recipe));
                Logs.add("k: " + k.toString());
                Logs.add("n: " + n.toString());
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

    private void setReturnCode(int returnCode) {
        ReturnCode = returnCode;
    }

    public int getReturnCode() {
        return ReturnCode;
    }

    private void writeData() {
        createFile();
        try {
            File file = new File("data/" + a + "." + b + ".txt");
            FileWriter w = new FileWriter(file);
            for (String log : Logs) {
                w.append(String.valueOf(log) + "\n");
            }
            w.flush();
            w.close();
            Logs.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile() {
        createFolder();
        if (!isFileExist()) {
            File file = new File("data/" + a + "." + b + ".txt");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isFileExist() {
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