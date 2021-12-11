package com.imjustdoom.pluginsite.service;

import org.springframework.http.HttpEntity;

public interface LogoService {

    void createLogo(int id);

    void updateLogo(int id);

    HttpEntity<byte[]> serveLogo(int id);
}
