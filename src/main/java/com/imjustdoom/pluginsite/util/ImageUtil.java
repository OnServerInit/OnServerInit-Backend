package com.imjustdoom.pluginsite.util;

import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtil {

    private static final Tika TIKA = new Tika();

    public static byte[] handleImage(MultipartFile file) {
        try {
            String fileType = TIKA.detect(file.getInputStream());
            if (!fileType.equals("image/jpeg") && !fileType.equals("image/png")) {
                // todo invalid type input.
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "File type is not image/jpeg or image/png. Supplied: " + fileType);
            }
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            ByteArrayOutputStream compressedStream = new ByteArrayOutputStream();

            try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(compressedStream)) {
                ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("JPEG").next();
                imageWriter.setOutput(outputStream);

                ImageWriteParam writerConfig = imageWriter.getDefaultWriteParam();
                writerConfig.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writerConfig.setCompressionQuality(0.7f); // 0.0 - 1.0
                // todo we can make the writer config its own static variable

                imageWriter.write(null, new IIOImage(bufferedImage, null, null), writerConfig);
                imageWriter.dispose();

                return compressedStream.toByteArray();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(); // todo better exception
        }
    }
}
