package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.config.exception.RestErrorCode;
import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.dtos.in.CreateResourceRequest;
import com.imjustdoom.pluginsite.dtos.out.SimpleResourceDto;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.repositories.ResourceRepository;
import com.imjustdoom.pluginsite.repositories.UpdateRepository;
import com.imjustdoom.pluginsite.service.ResourceService;
import com.imjustdoom.pluginsite.service.ResourceUpdateService;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final ResourceUpdateService resourceUpdateService;
    private final ResourceRepository resourceRepository;
    private final UpdateRepository updateRepository;

    @GetMapping
    public Page<SimpleResourceDto> searchResources(@PageableDefault(size = 25, sort = "updated", direction = Sort.Direction.DESC) Pageable pageable,
                                                   @QuerydslPredicate(root = Resource.class) Predicate predicate) throws RestException {
        if (pageable.getPageSize() > 50) throw new RestException(RestErrorCode.PAGE_SIZE_TOO_LARGE, "Page size is too large (%s > %s)", pageable.getPageSize(), 50);
        return this.resourceService.searchResources(pageable, predicate);
    }

    @PostMapping
    public void createResource(Account account, CreateResourceRequest request) throws RestException {
        this.resourceService.createResource(account, request);
    }

    @PostMapping("/{resourceId}/edit")
    public void updateResourceInfo(Account account, @PathVariable int resourceId, @RequestParam(value = "file", required = false) MultipartFile file, CreateResourceRequest request) throws RestException {
        this.resourceService.updateResource(account, resourceId, file, request);
    }

    // TODO: make this return Resource instead of SimpleResourceDto, issue is something with Account giving this error "SyntaxError: JSON.parse: unterminated string at line 1 column 3005806 of the JSON data"
    @GetMapping("/{resourceId}")
    public SimpleResourceDto getResource(@PathVariable int resourceId) throws RestException {
        Resource resource = this.resourceService.getResource(resourceId);
        int totalDownloads = this.updateRepository.getTotalDownloads(resource.getId()).orElse(0);

        return SimpleResourceDto.create(resource, totalDownloads);
    }

    // TODO: properly delete
    @DeleteMapping("/{resourceId}/delete")
    public void deleteResource(@PathVariable int resourceId) {
        this.resourceRepository.updateStatusById(resourceId, "removed");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET");
            }
        };
    }
}
