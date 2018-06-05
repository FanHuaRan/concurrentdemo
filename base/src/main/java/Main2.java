import java.util.Scanner;

/**
 * @author FanHuaran
 * @description
 * @create 2018-04-20 20:50
 **/
public class Main2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            int n = scanner.nextInt();
            int total = 0;
            // 得出总位数
            int temp = get(n) - 1;
            for (int i = 0; i < temp; i++) {
                int current = (int) Math.pow(10, i) * (i + 1);
                total += current * 9;
            }
            int last = n % 10;
            total += (last + 1) * (temp + 1);
//            total += (current * temp);
//            n /= 10;
            System.out.println(total);
        }
    }

    private static int get(int n) {
        int size = 1;
        while ((n = n / 10) != 0) {
            size++;
        }
        return size;
    }
}