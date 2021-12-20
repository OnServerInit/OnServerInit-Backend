package com.imjustdoom.pluginsite.service.impl;

import com.imjustdoom.pluginsite.PluginSiteApplication;
import com.imjustdoom.pluginsite.repositories.ResourceRepository;
import com.imjustdoom.pluginsite.service.LogoService;
import com.imjustdoom.pluginsite.util.ImageUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;

@Service
@AllArgsConstructor
public class LogoServiceImpl implements LogoService {

    private final ResourceRepository resourceRepository;

    @Override
    public void updateLogo(int id, MultipartFile logo) {

        byte[] image = ImageUtil.handleImage(logo);

        resourceRepository.updateLogoById(id, image);

        //if (image.getHeight() != image.getWidth())
            //return "redirect:/resources/%s/edit?error=logosize".formatted(id);
    }

    @Override
    public boolean logoExists(int id) {
        return Paths.get("resources/logos/%s".formatted(id)).toFile().exists();
    }

    @Override
    public byte[] serveDefaultLogo() {
        try {
            return PluginSiteApplication.class.getResourceAsStream("/pictures/logo.png").readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] serverLogo(int id) {
        byte[] image = resourceRepository.findLogoById(id);
        if(image == null){
            return serveDefaultLogo();
        }
        return resourceRepository.findLogoById(id);
    }
}
