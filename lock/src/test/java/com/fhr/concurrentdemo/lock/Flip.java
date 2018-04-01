package com.fhr.concurrentdemo.lock;

/**
 * @author FanHuaran
 * @description 翻转棋盘
 * @create 2018-04-01 12:42
 **/
public class Flip {
    public int[][] flipChess(int[][] A, int[][] f) {
        if (A == null || f == null) {
            return A;
        }
        for (int[] change : f) {
            int i = change[0] - 1;
            int j = change[1] - 1;
            if (i - 1 >= 0) {
                A[i - 1][j] = A[i - 1][j] == 0 ? 1 : 0;
            }
            if (i + 1 < 4) {
                A[i + 1][j] = A[i + 1][j] == 0 ? 1 : 0;
            }
            if (j - 1 >= 0) {
                A[i][j - 1] = A[i][j - 1] == 0 ? 1 : 0;
            }
            if (j + 1 < 4) {
                A[i][j + 1] = A[i][j + 1] == 0 ? 1 : 0;
            }
        }
        return A;
    }
}
