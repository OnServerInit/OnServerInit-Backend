package com.imjustdoom.pluginsite.service.impl;

import com.imjustdoom.pluginsite.PluginSiteApplication;
import com.imjustdoom.pluginsite.repositories.AccountRepository;
import com.imjustdoom.pluginsite.service.ProfilePictureService;
import com.imjustdoom.pluginsite.util.ImageUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;

@Service
@AllArgsConstructor
public class ProfilePictureServiceImpl implements ProfilePictureService {

    private final AccountRepository accountRepository;

    @Override
    public void updateProfilePicture(int id, MultipartFile profilePicture) {

        byte[] image = ImageUtil.handleImage(profilePicture);

        accountRepository.updateProfilePictureById(id, image);

        //if (image.getHeight() != image.getWidth())
            //return "redirect:/resources/%s/edit?error=logosize".formatted(id);
    }

    @Override
    public boolean logoExists(int id) {
        return Paths.get("resources/logos/%s".formatted(id)).toFile().exists();
    }

    @Override
    public byte[] serveDefaultProfilePicture() {
        try {
            return PluginSiteApplication.class.getResourceAsStream("/pictures/default.png").readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] serverProfilePicture(int id) {
        byte[] image = accountRepository.findAccountProfilePicture(id);
        if(image == null){
            return serveDefaultProfilePicture();
        }
        return accountRepository.findAccountProfilePicture(id);
    }
}
