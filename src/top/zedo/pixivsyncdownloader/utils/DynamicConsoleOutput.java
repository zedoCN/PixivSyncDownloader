package top.zedo.pixivsyncdownloader.utils;

/**
 * 动态控制台输出
 */
public class DynamicConsoleOutput {
    private final StringBuilder sb = new StringBuilder();

    /**
     * 获取StringBuilder
     *
     * @return StringBuilder
     */
    public StringBuilder getStringBuilder() {
        return sb;
    }

    /**
     * 增加字符串
     *
     * @param v 字符串
     */
    public void appendString(String v) {
        sb.append(v);
    }

    /**
     * 增加字符串
     *
     * @param v 字符串
     */
    public void appendString(Object v) {
        sb.append(v);
    }

    /**
     * 增加格式化字符串
     *
     * @param format 格式化字符串
     * @param args   参数
     */
    public void appendFormat(String format, Object... args) {
        sb.append(String.format(format, args));
    }

    /**
     * 清除缓存内容
     */
    public void clear() {
        sb.setLength(0);
    }

    /**
     * 更新控制台输出
     */
    public void update() {
        // or \b  + "\u001B[1K"  \u0008
        //sb.insert(0, "\b".repeat(sb.length()) );
        //sb.insert(0, "\u001B2J");
        sb.insert(0, "\u0008".repeat(sb.length() * 4));
        System.out.print(sb);
        clear();
    }
}