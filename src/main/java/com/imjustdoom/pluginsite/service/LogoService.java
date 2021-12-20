package com.imjustdoom.pluginsite.service;

import org.springframework.web.multipart.MultipartFile;

public interface LogoService {

    void updateLogo(int id, MultipartFile logo);

    boolean logoExists(int id);

    byte[] serveDefaultLogo();

    byte[] serverLogo(int id);
}
