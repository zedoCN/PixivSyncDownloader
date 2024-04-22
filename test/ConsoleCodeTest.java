import top.zedo.pixivsyncdownloader.utils.ANSICode;

public class ConsoleCodeTest {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (ANSICode color : ANSICode.values()) {
            index %= 10;
            index++;
            if (index == 1)
                sb.append("\n");
            sb.append(ANSICode.RESET).append(" ").append(color).append(color.name()).append(ANSICode.RESET);
        }

        System.out.println(sb);
    }
}
