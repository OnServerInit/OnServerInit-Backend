package com.imjustdoom.pluginsite.service.impl;

import com.imjustdoom.pluginsite.dtos.in.CreateUpdateRequest;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.model.Update;
import com.imjustdoom.pluginsite.repositories.UpdateRepository;
import com.imjustdoom.pluginsite.service.UpdateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class UpdateServiceImpl implements UpdateService {

    private final UpdateRepository updateRepository;

    @Override
    public Update createUpdate(CreateUpdateRequest updateRequest, Account account) {
        Update resource = new Update(updateRequest.getDescription(), "", "", "", "",
                new ArrayList<>(), new ArrayList<>(), new Resource(), updateRequest.getExternalLink());

        updateRepository.save(resource);

        return resource;
    }
}
