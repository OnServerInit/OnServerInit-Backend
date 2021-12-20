package com.imjustdoom.pluginsite.service.impl;

import com.google.gson.JsonObject;
import com.imjustdoom.pluginsite.dtos.in.CreateResourceRequest;
import com.imjustdoom.pluginsite.dtos.in.CreateUpdateRequest;
import com.imjustdoom.pluginsite.dtos.out.SimpleResourceDto;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.model.Update;
import com.imjustdoom.pluginsite.repositories.ResourceRepository;
import com.imjustdoom.pluginsite.repositories.UpdateRepository;
import com.imjustdoom.pluginsite.service.ResourceService;
import com.imjustdoom.pluginsite.service.UpdateService;
import lombok.AllArgsConstructor;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UpdateServiceImpl implements UpdateService {

    private final UpdateRepository updateRepository;

    @Override
    public Update createUpdate(CreateUpdateRequest updateRequest, Account account) {
        Update resource = new Update(updateRequest.getDescription(), "", "", "", "", new JsonObject(), new JsonObject(), new Resource(), updateRequest.getExternalLink());

        updateRepository.save(resource);

        return resource;
    }
}
