package com.imjustdoom.pluginsite.service;

import org.springframework.web.multipart.MultipartFile;

public interface ProfilePictureService {

    void updateProfilePicture(int id, MultipartFile logo);

    boolean logoExists(int id);

    byte[] serveDefaultProfilePicture();

    byte[] serverProfilePicture(int id);
}
