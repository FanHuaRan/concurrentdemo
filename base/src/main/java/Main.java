import java.util.Scanner;

/**
 * @author FanHuaran
 * @description
 * @create 2018-04-20 20:24
 **/
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            // 获取输入
            int total = scanner.nextInt();
            int n = scanner.nextInt();
            int m = scanner.nextInt();
            int p = scanner.nextInt();

            // 参数准备
            int[] arr = new int[total];
            arr[0] = p;
            for (int i = 1; i < total; i++) {
                arr[i] = (arr[i - 1] + 153) % p;
            }

            // 循环计算
            int result = 0;
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= m; j++) {
                    int gcdValue = getGcd(i, j);
                    result += arr[gcdValue - 1];
                }
            }

            System.out.println(result);
        }
    }

    /**
     * 求最大公约数
     *
     * @param a
     * @param b
     * @return
     */
    private static int getGcd(int a, int b) {
        while (b != 0) {
            int r = b;
            b = a % b;
            a = r;
        }
        return a;
    }

}
