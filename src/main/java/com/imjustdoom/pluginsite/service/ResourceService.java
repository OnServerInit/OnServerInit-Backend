package com.imjustdoom.pluginsite.service;

import com.imjustdoom.pluginsite.config.custom.SiteConfig;
import com.imjustdoom.pluginsite.config.exception.RestErrorCode;
import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.dtos.in.CreateResourceRequest;
import com.imjustdoom.pluginsite.dtos.out.SimpleResourceDto;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.repositories.ResourceRepository;
import com.imjustdoom.pluginsite.repositories.UpdateRepository;
import com.imjustdoom.pluginsite.util.ImageUtil;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
        if (this.resourceRepository.getResourcesCreateLastHour(account.getId()) > this.siteConfig.getMaxCreationsPerHour())
            throw new RestException(RestErrorCode.TOO_MANY_RESOURCE_CREATIONS);
        if (this.resourceRepository.existsByNameEqualsIgnoreCase(request.getName()))
            throw new RestException(RestErrorCode.RESOURCE_NAME_NOT_AVAILABLE);
        if (request.getName().isEmpty() || request.getBlurb().isEmpty() || request.getDescription().isEmpty())
            throw new RestException(RestErrorCode.REQUIRED_ARGUMENTS_MISSING);

        Resource resource = new Resource(request.getName(), request.getDescription(),
            request.getBlurb(), request.getDonation(), request.getSource(),
            account, request.getSupport(), request.getCategory());

        return this.resourceRepository.save(resource);
    }

    // TODO: More sanity checks
    // todo: also this is horrible, can we just use querydsl?
    public Resource updateResource(Account account, int resourceId, MultipartFile file, CreateResourceRequest request) throws RestException {
        Resource resource = this.resourceRepository.findById(resourceId).orElseThrow(() -> new RestException(RestErrorCode.RESOURCE_NOT_FOUND));

        String name = request.getName();
        if (name != null && !name.isEmpty())
            resource.setName(name);

        String description = request.getDescription();
        if (description != null && !description.isEmpty())
            resource.setDescription(description);

        String blurb = request.getBlurb();
        if (blurb != null && !blurb.isEmpty())
            resource.setBlurb(blurb);

        String category = request.getCategory();
        if (category != null && !category.isEmpty())
            resource.setCategory(category);

        String donation = request.getDonation();
        if (donation != null && !donation.isEmpty())
            resource.setDonation(donation);

        String support = request.getSupport();
        if (support != null && !support.isEmpty())
            resource.setSupport(support);

        String source = request.getSource();
        if (source != null && !source.isEmpty())
            resource.setSource(source);

        if (file != null && !file.isEmpty())
            resource.setLogo(ImageUtil.handleImage(file));

        return this.resourceRepository.save(resource);
    }
}
