package top.zedo.pixivsyncdownloader;

import top.zedo.pixivsyncdownloader.utils.OutputUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        OutputUtils.setCharset(StandardCharsets.UTF_8);


        List<String> argList = Arrays.asList(args);

        if (argList.contains("-webui")) {
            WebUIServer.start(Config.DATA.serverPort);
        }


    }


}
