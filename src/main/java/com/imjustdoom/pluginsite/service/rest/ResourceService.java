package com.imjustdoom.pluginsite.service.rest;

import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.dtos.out.SimpleResourceDto;
import com.imjustdoom.pluginsite.repositories.ResourceRepository;
import com.imjustdoom.pluginsite.repositories.UpdateRepository;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final UpdateRepository updateRepository;

    public Page<SimpleResourceDto> searchResources(Pageable pageable, Predicate query) throws RestException {
        return this.resourceRepository.findAll(query, pageable)
            .map(resource -> {
                int totalDownloads = this.updateRepository.getTotalDownloads(resource.getId());
                return SimpleResourceDto.create(resource, totalDownloads);
            });
    }
}
