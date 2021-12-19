package com.imjustdoom.pluginsite.service;

import com.imjustdoom.pluginsite.dtos.in.CreateResourceRequest;
import com.imjustdoom.pluginsite.dtos.out.SimpleResourceDto;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;

import java.util.List;

public interface ResourceService {

    Resource createResource(CreateResourceRequest resourceRequest, Account account);

    List<SimpleResourceDto> searchResources(String search, String page);

    List<SimpleResourceDto> getResources(String sort, String page);
}
