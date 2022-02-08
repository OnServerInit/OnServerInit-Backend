package com.imjustdoom.pluginsite.service;

import com.imjustdoom.pluginsite.config.custom.SiteConfig;
import com.imjustdoom.pluginsite.dtos.in.CreateResourceRequest;
import com.imjustdoom.pluginsite.dtos.out.SimpleResourceDto;
import com.imjustdoom.pluginsite.model.Account;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

public interface ResourceService {

    String postCreateResource(CreateResourceRequest resourceRequest, Account account, SiteConfig siteConfig, RedirectAttributes redirectAttributes);

    List<SimpleResourceDto> searchResources(String search, String sortBy, String page);

    List<SimpleResourceDto> getResourcesWithCategory(String sort, String page, String category);

    List<SimpleResourceDto> getResources(String sort, String page);
}
