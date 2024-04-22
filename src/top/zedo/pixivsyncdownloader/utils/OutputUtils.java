package top.zedo.pixivsyncdownloader.utils;

import com.sun.jna.Library;
import com.sun.jna.Native;

import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OutputUtils {
    private static final Map<String, CharsetCodePage> CHARSET_MAP = new HashMap<>();

    static {
        for (CharsetCodePage codePage : CharsetCodePage.values()) {
            CHARSET_MAP.put(codePage.getCharset().name(), codePage);
        }
    }

    private static CharsetCodePage getByCharset(Charset charset) {
        return CHARSET_MAP.get(charset.name());
    }

    private enum CharsetCodePage {
        OEM_US(Charset.forName("IBM437"), 437),
        OEM_LATIN1(Charset.forName("IBM850"), 850),
        ANSI_SHIFT_JIS(Charset.forName("Shift_JIS"), 932),
        ANSI_SIMPLIFIED_CHINESE(Charset.forName("GBK"), 936),
        ANSI_TRADITIONAL_CHINESE(Charset.forName("Big5"), 950),
        ANSI_LATIN1(Charset.forName("windows-1252"), 1252),
        UTF8(StandardCharsets.UTF_8, 65001),
        OEM_PORTUGUESE(Charset.forName("IBM860"), 860),
        OEM_CANADIAN_FRENCH(Charset.forName("IBM863"), 863),
        OEM_NORDIC(Charset.forName("IBM865"), 865),
        ANSI_LATIN2(Charset.forName("windows-1250"), 1250),
        ANSI_CYRILLIC(Charset.forName("windows-1251"), 1251),
        ANSI_GREEK(Charset.forName("windows-1253"), 1253),
        ANSI_TURKISH(Charset.forName("windows-1254"), 1254),
        ANSI_HEBREW(Charset.forName("windows-1255"), 1255),
        ANSI_ARABIC(Charset.forName("windows-1256"), 1256),
        ANSI_BALTIC(Charset.forName("windows-1257"), 1257),
        ANSI_VIETNAMESE(Charset.forName("windows-1258"), 1258),
        ANSI_THAI(Charset.forName("windows-874"), 874),
        IBM_EBCDIC_TURKISH(Charset.forName("IBM1026"), 1026),
        IBM_EBCDIC_US_CANADA(Charset.forName("IBM1140"), 1140),
        IBM_EBCDIC_GERMANY(Charset.forName("IBM1141"), 1141),
        IBM_EBCDIC_DENMARK_NORWAY(Charset.forName("IBM1142"), 1142),
        IBM_EBCDIC_FINLAND_SWEDEN(Charset.forName("IBM1143"), 1143),
        IBM_EBCDIC_ITALY(Charset.forName("IBM1144"), 1144),
        IBM_EBCDIC_LATIN_AMERICA_SPAIN(Charset.forName("IBM1145"), 1145),
        IBM_EBCDIC_UNITED_KINGDOM(Charset.forName("IBM1146"), 1146),
        IBM_EBCDIC_FRANCE(Charset.forName("IBM1147"), 1147),
        IBM_EBCDIC_INTERNATIONAL(Charset.forName("IBM1148"), 1148),
        IBM_EBCDIC_ICELANDIC(Charset.forName("IBM1149"), 1149),
        KOI8_R(Charset.forName("KOI8-R"), 20866),
        KOI8_U(Charset.forName("KOI8-U"), 21866);

        private final Charset charset;
        private final int codePage;

        CharsetCodePage(Charset charset, int codePage) {
            this.charset = charset;
            this.codePage = codePage;
        }

        public Charset getCharset() {
            return charset;
        }

        public int getCodePage() {
            return codePage;
        }
    }


    public static void setCharset(Charset charset) {
        CharsetCodePage codePage = Objects.requireNonNull(getByCharset(charset), charset + "的代码页不存在");
        System.setOut(new PrintStream(System.out, true, charset));
        System.setErr(new PrintStream(System.err, true, charset));
        Kernel32 kernel32 = Native.load("kernel32", Kernel32.class);
        kernel32.SetConsoleOutputCP(codePage.getCodePage());
    }


    private interface Kernel32 extends Library {
        void SetConsoleOutputCP(int wCodePageID);
    }
}
