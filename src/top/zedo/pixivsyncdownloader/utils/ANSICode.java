package top.zedo.pixivsyncdownloader.utils;

/**
 * ANSI 控制码
 */
public enum ANSICode {
    /**
     * 钟
     */
    BELL("\u0007"),
    // 文本颜色
    /**
     * 文本颜色：黑色
     */
    BLACK("\u001B[30m"),
    /**
     * 文本颜色：红色
     */
    RED("\u001B[31m"),
    /**
     * 文本颜色：绿色
     */
    GREEN("\u001B[32m"),
    /**
     * 文本颜色：黄色
     */
    YELLOW("\u001B[33m"),
    /**
     * 文本颜色：蓝色
     */
    BLUE("\u001B[34m"),
    /**
     * 文本颜色：洋红色
     */
    MAGENTA("\u001B[35m"),
    /**
     * 文本颜色：青色
     */
    CYAN("\u001B[36m"),
    /**
     * 文本颜色：白色
     */
    WHITE("\u001B[37m"),
    /**
     * 文本颜色：亮黑色
     */
    BRIGHT_BLACK("\u001B[90m"),
    /**
     * 文本颜色：亮红色
     */
    BRIGHT_RED("\u001B[91m"),
    /**
     * 文本颜色：亮绿色
     */
    BRIGHT_GREEN("\u001B[92m"),
    /**
     * 文本颜色：亮黄色
     */
    BRIGHT_YELLOW("\u001B[93m"),
    /**
     * 文本颜色：亮蓝色
     */
    BRIGHT_BLUE("\u001B[94m"),
    /**
     * 文本颜色：亮洋红色
     */
    BRIGHT_MAGENTA("\u001B[95m"),
    /**
     * 文本颜色：亮青色
     */
    BRIGHT_CYAN("\u001B[96m"),
    /**
     * 文本颜色：亮白色
     */
    BRIGHT_WHITE("\u001B[97m"),

    // 背景颜色
    /**
     * 背景颜色：黑色
     */
    BG_BLACK("\u001B[40m"),
    /**
     * 背景颜色：红色
     */
    BG_RED("\u001B[41m"),
    /**
     * 背景颜色：绿色
     */
    BG_GREEN("\u001B[42m"),
    /**
     * 背景颜色：黄色
     */
    BG_YELLOW("\u001B[43m"),
    /**
     * 背景颜色：蓝色
     */
    BG_BLUE("\u001B[44m"),
    /**
     * 背景颜色：洋红色
     */
    BG_MAGENTA("\u001B[45m"),
    /**
     * 背景颜色：青色
     */
    BG_CYAN("\u001B[46m"),
    /**
     * 背景颜色：白色
     */
    BG_WHITE("\u001B[47m"),
    /**
     * 背景颜色：亮黑色
     */
    BG_BRIGHT_BLACK("\u001B[100m"),
    /**
     * 背景颜色：亮红色
     */
    BG_BRIGHT_RED("\u001B[101m"),
    /**
     * 背景颜色：亮绿色
     */
    BG_BRIGHT_GREEN("\u001B[102m"),
    /**
     * 背景颜色：亮黄色
     */
    BG_BRIGHT_YELLOW("\u001B[103m"),
    /**
     * 背景颜色：亮蓝色
     */
    BG_BRIGHT_BLUE("\u001B[104m"),
    /**
     * 背景颜色：亮洋红色
     */
    BG_BRIGHT_MAGENTA("\u001B[105m"),
    /**
     * 背景颜色：亮青色
     */
    BG_BRIGHT_CYAN("\u001B[106m"),
    /**
     * 背景颜色：亮白色
     */
    BG_BRIGHT_WHITE("\u001B[107m"),

    // 样式
    /**
     * 重置所有样式
     */
    RESET("\u001B[0m"),
    /**
     * 加粗
     */
    BOLD("\u001B[1m"),
    /**
     * 淡色（不是所有终端都支持）
     */
    FAINT("\u001B[2m"),
    /**
     * 斜体（不是所有终端都支持）
     */
    ITALIC("\u001B[3m"),
    /**
     * 下划线
     */
    UNDERLINE("\u001B[4m"),
    /**
     * 闪烁（不是所有终端都支持）
     */
    BLINK("\u001B[5m"),
    /**
     * 快闪烁（不是所有终端都支持）
     */
    BLINK_FAST("\u001B[6m"),
    /**
     * 反显
     */
    REVERSE("\u001B[7m"),
    /**
     * 隐藏
     */
    HIDDEN("\u001B[8m"),
    /**
     * 划除
     */
    Erase("\u001B[9m"),
    /**
     * 双下划线；或：取消粗体
     */
    DOUBLE_UNDERLINE("\u001B[21m"),
    /**
     * 镶框
     */
    Framed("\u001B[51m"),
    /**
     * 上划线
     */
    Upline("\u001B[53m"),
    ;

    private final String code;

    ANSICode(String code) {
        this.code = code;
    }

    /**
     * 获取当前 ANSI 控制码的字符串表示形式。
     *
     * @return ANSI 控制码的字符串表示形式
     */
    @Override
    public String toString() {
        return code;
    }

    /**
     * 前景色
     *
     * @param r,g,b 红绿蓝 0-255
     */
    public static String foregroundColor(int r, int g, int b) {
        return "\u001B[38;2;" + r + ";" + g + ";" + b + "m";
    }

    /**
     * 背景色
     *
     * @param r,g,b 红绿蓝 0-255
     */
    public static String backgroundColor(int r, int g, int b) {
        return "\u001B[48;2;" + r + ";" + g + ";" + b + "m";
    }

    /**
     * 下划线颜色
     *
     * @param r,g,b 红绿蓝 0-255
     */
    public static String underlineColor(int r, int g, int b) {
        return "\u001B[58;2;" + r + ";" + g + ";" + b + "m";
    }
}
