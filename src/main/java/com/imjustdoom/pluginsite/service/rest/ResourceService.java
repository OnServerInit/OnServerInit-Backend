package com.imjustdoom.pluginsite.service.rest;

import com.imjustdoom.pluginsite.config.custom.SiteConfig;
import com.imjustdoom.pluginsite.config.exception.RestErrorCode;
import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.dtos.in.CreateResourceRequest;
import com.imjustdoom.pluginsite.dtos.out.SimpleResourceDto;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;
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
    private final SiteConfig siteConfig;

    public Page<SimpleResourceDto> searchResources(Pageable pageable, Predicate query) throws RestException {
        return this.resourceRepository.findAll(query, pageable)
            .map(resource -> {
                int totalDownloads = this.updateRepository.getTotalDownloads(resource.getId()).orElse(0);
                return SimpleResourceDto.create(resource, totalDownloads);
            });
    }

    //TODO: More sanity checks
    public Resource createResource(Account account, CreateResourceRequest request) throws RestException {
        if (this.resourceRepository.getResourcesCreateLastHour(account.getId()) > this.siteConfig.getMaxCreationsPerHour()) throw new RestException(RestErrorCode.TOO_MANY_RESOURCE_UPDATES);
        if (this.resourceRepository.existsByNameEqualsIgnoreCase(request.getName())) throw new RestException(RestErrorCode.RESOURCE_NAME_NOT_AVAILABLE);
        if (request.getName().isEmpty() || request.getBlurb().isEmpty() || request.getDescription().isEmpty()) throw new RestException(RestErrorCode.REQUIRED_ARGUMENTS_MISSING);

        Resource resource = new Resource(request.getName(), request.getDescription(),
            request.getBlurb(), request.getDonation(), request.getSource(),
            "", account, request.getSupport(), request.getCategory());

        this.resourceRepository.save(resource);

        return this.resourceRepository.save(resource);
    }
}
