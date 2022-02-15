package com.imjustdoom.pluginsite.controller;


import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.dtos.in.CreateUpdateRequest;
import com.imjustdoom.pluginsite.dtos.in.resource.EditResourceUpdateRequest;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Update;
import com.imjustdoom.pluginsite.service.ResourceUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/resource/update")
@RequiredArgsConstructor
public class ResourceUpdateController {
    private final ResourceUpdateService resourceUpdateService;

    @PatchMapping("/{updateId}/status")
    public Update changeStatus(Account account, @PathVariable int updateId, @RequestParam String status) throws RestException {
        return this.resourceUpdateService.changeStatus(account, updateId, status);
    }

    @PatchMapping("/{updateId}")
    public Update editUpdate(Account account, @PathVariable int updateId, @RequestBody EditResourceUpdateRequest request) throws RestException {
        return this.resourceUpdateService.editUpdate(account, updateId, request);
    }

    @PostMapping("/")
    public void createUpdate(Account account, @RequestParam int resourceId,
                             @RequestParam MultipartFile file, @RequestBody CreateUpdateRequest updateRequest) throws RestException {
        this.resourceUpdateService.createUpdate(account, resourceId, file, updateRequest);
    }

    @GetMapping(value = "/{updateId}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<File> getFile(@PathVariable int updateId) throws RestException {
        ResourceUpdateService.FileReturn fileReturn = this.resourceUpdateService.getDownload(updateId);
        return ResponseEntity.ok()
            .headers(httpHeaders -> httpHeaders.setContentDisposition(
                ContentDisposition.attachment()
                    .filename(fileReturn.realName())
                    .build()
            ))
            .body(fileReturn.file());
    }
}
