package top.zedo.pixivsyncdownloader.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import top.zedo.pixivsyncdownloader.Config;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;

public class ImageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Path root = Path.of(Config.DATA.repositoryPath);
        resp.addHeader("Access-Control-Allow-Origin", "*");
        String name = Objects.requireNonNull(req.getParameter("name"), "文件名不能为空");
        String type = Objects.requireNonNullElse(req.getParameter("type"), "original");
        if (!Files.exists(root.resolve(name)))
            throw new ServletException("404 未找到");

        byte[] buf = new byte[10240];
        ServletOutputStream outputStream = resp.getOutputStream();

        try (InputStream inputStream = Files.newInputStream(root.resolve(name))) {
            switch (type) {
                case "original" -> {
                    switch (name.substring(name.lastIndexOf(".") + 1).toLowerCase()) {
                        case "jpg", "jpeg" -> resp.setContentType("image/jpeg");
                        case "png" -> resp.setContentType("image/png");
                        case "gif" -> resp.setContentType("image/gif");
                    }
                    resp.setContentLength(inputStream.available());
                    int len;
                    while ((len = inputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, len);
                    }
                }
                case "preview" -> {
                    float quality = Float.parseFloat(Objects.requireNonNullElse(req.getParameter("quality"), "0.4"));
                    BufferedImage image = ImageIO.read(inputStream);

                    // 将图片转换为 RGB 颜色空间
                    if (image.getType() != BufferedImage.TYPE_INT_RGB) {
                        BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                        ColorConvertOp op = new ColorConvertOp(null);
                        op.filter(image, convertedImage);
                        image = convertedImage;
                    }


                    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
                    if (!writers.hasNext()) {
                        throw new ServletException("没有jpeg编码器");
                    }
                    ImageWriter writer = writers.next();
                    ImageWriteParam param = writer.getDefaultWriteParam();
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(quality);
                    ByteArrayOutputStream previewOutputStream = new ByteArrayOutputStream();
                    try (ImageOutputStream ios = ImageIO.createImageOutputStream(previewOutputStream)) {
                        writer.setOutput(ios);
                        writer.write(null, new IIOImage(image, null, null), param);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        writer.dispose();
                    }
                    resp.setContentType("image/jpeg");
                    resp.setContentLength(previewOutputStream.size());
                    outputStream.write(previewOutputStream.toByteArray());
                }
            }
        }

        resp.flushBuffer();
    }
}
