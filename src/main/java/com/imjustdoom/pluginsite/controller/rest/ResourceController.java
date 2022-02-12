package com.imjustdoom.pluginsite.controller.rest;

import com.imjustdoom.pluginsite.config.exception.RestErrorCode;
import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.dtos.in.CreateResourceRequest;
import com.imjustdoom.pluginsite.dtos.in.resource.EditResourceUpdateRequest;
import com.imjustdoom.pluginsite.dtos.out.SimpleResourceDto;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.model.Update;
import com.imjustdoom.pluginsite.repositories.ResourceRepository;
import com.imjustdoom.pluginsite.service.rest.ResourceService;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final ResourceRepository resourceRepository;

    @GetMapping
    public Page<SimpleResourceDto> searchResources(@PageableDefault(size = 25, sort = "updated", direction = Sort.Direction.DESC) Pageable pageable,
                                                   @QuerydslPredicate(root = Resource.class) Predicate predicate) throws RestException {
        if (pageable.getPageSize() > 50)
            throw new RestException(RestErrorCode.PAGE_SIZE_TOO_LARGE, "Page size is too large (%s > %s)", pageable.getPageSize(), 50);
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

    // todo properly delete
    @DeleteMapping("/{resourceId}")
    public void deleteResource(@PathVariable int resourceId) {
        this.resourceRepository.updateStatusById(resourceId, "removed");
    }

    @PatchMapping("/{resourceId}/update/{updateId}/status")
    public Update changeUpdateStatus(Account account, @PathVariable int resourceId, @PathVariable int updateId, @RequestParam String status) throws RestException {
        return this.resourceService.changeUpdateStatus(account, resourceId, updateId, status);
    }

    @PatchMapping("/{resourceId}/edit/update/{updateId}")
    public Update editResourceUpdate(Account account, @PathVariable int resourceId, @PathVariable int updateId, @RequestBody EditResourceUpdateRequest request) throws RestException {
        return this.resourceService.editResourceUpdate(account, resourceId, updateId, request);
    }
}
