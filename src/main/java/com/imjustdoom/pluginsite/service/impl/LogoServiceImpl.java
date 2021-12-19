package com.imjustdoom.pluginsite.service.impl;

import com.imjustdoom.pluginsite.PluginSiteApplication;
import com.imjustdoom.pluginsite.service.LogoService;
import com.imjustdoom.pluginsite.util.FileUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LogoServiceImpl implements LogoService {

    @Override
    public void createLogo(int id) {
        if (!FileUtil.doesFileExist("./resources/logos/" + id)) {
            try {
                Files.createDirectory(Paths.get("./resources/logos/" + id));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!FileUtil.doesFileExist("./resources/logos/" + id + "/logo.png")) {
            InputStream stream = PluginSiteApplication.class.getResourceAsStream("/pictures/logo.png");
            assert stream != null;
            try {
                Files.copy(stream, Path.of("./resources/logos/" + id + "/logo.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateLogo(int id) {

    }

    @Override
    public boolean logoExists(int id) {
        return Paths.get("resources/logos/%s".formatted(id)).toFile().exists();
    }

    @Override
    public HttpEntity<byte[]> serveLogo(int id) {
        try {
            Path path = Paths.get("resources/logos/%s".formatted(id));
            Resource file = new UrlResource(path.resolve("logo.png").toUri());

            byte[] image = file.getInputStream().readAllBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(image.length);

            return new HttpEntity<>(image, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HttpEntity<byte[]> serveDefaultLogo() {
        try {
            Path path = Paths.get("resources/logos/default");
            Resource file = new UrlResource(path.resolve("logo.png").toUri());

            byte[] image = file.getInputStream().readAllBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(image.length);

            return new HttpEntity<>(image, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
