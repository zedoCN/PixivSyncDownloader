import top.zedo.pixivsyncdownloader.utils.ANSICode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImagePrint {
    public static void main(String[] args) throws IOException, InterruptedException {
        List<Path> files = new ArrayList<>(Files.list(Path.of(args[0])).toList());

        Random rand = new Random();
        while (true) {

            Path file = files.get(Math.abs(rand.nextInt()) % files.size());
            if (Files.isDirectory(file))
                continue;
            BufferedImage image = ImageIO.read(Files.newInputStream(file));
            if (image == null)
                continue;
            float r = (float) image.getHeight() / image.getWidth();
            int w = Integer.parseInt(args[1]);//940
            int h = (int) (w * r);
            BufferedImage newImage = new BufferedImage(w * 2, h * 2, BufferedImage.TYPE_INT_RGB);
            newImage.getGraphics().drawImage(image, 0, 0, w * 2, h * 2, null);

            for (int y = 0; y < newImage.getHeight(); y += 2) {
                for (int x = 0; x < newImage.getWidth(); x++) {
                    Color color = new Color(newImage.getRGB(x, y));
                    Color color2 = new Color(newImage.getRGB(x, y + 1));
                    System.out.print(ANSICode.foregroundColor(color2.getRed(), color2.getGreen(), color2.getBlue()));
                    //System.out.print(ANSICode.underlineColor(color2.getRed(), color2.getGreen(), color2.getBlue()));
                    System.out.print(ANSICode.backgroundColor(color.getRed(), color.getGreen(), color.getBlue()));
                    System.out.print("▄");
                    System.out.print(ANSICode.RESET);
                }
                System.out.print("\n");
                System.out.flush();
                Thread.sleep(50);
            }
            Thread.sleep(2000);
        }
    }
}
