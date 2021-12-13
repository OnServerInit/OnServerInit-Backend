package com.imjustdoom.pluginsite.service.impl;

import com.imjustdoom.pluginsite.service.LogoService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LogoServiceImpl implements LogoService {

    @Override
    public void createLogo(int id) {

    }

    @Override
    public void updateLogo(int id) {

    }

    @Override
    public boolean logoExists(int id) {
        return (Paths.get("resources/logos/%s".formatted(id)).toFile().exists()) ? true : false;
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
    public HttpEntity<byte[]> serveDefaultLogo(){
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
