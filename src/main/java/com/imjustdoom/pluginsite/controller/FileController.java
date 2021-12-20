package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.PluginSiteApplication;
import com.imjustdoom.pluginsite.model.Update;
import com.imjustdoom.pluginsite.repositories.UpdateRepository;
import com.imjustdoom.pluginsite.service.LogoService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class FileController {

    private final LogoService logoService;
    private final UpdateRepository updateRepository;

    @GetMapping("/logo/{id}")
    @ResponseBody
    public HttpEntity<byte[]> serveLogo(@PathVariable("id") int id) {
        if (logoService.logoExists(id)) {
            return logoService.serveLogo(id);
        } else {
            return logoService.serveDefaultLogo();
        }
    }

    @GetMapping("/files/{id}/download/{fileId}")
    @ResponseBody
    public ResponseEntity serveFile(@PathVariable("id") int id, @PathVariable("fileId") int fileId) throws MalformedURLException {

        Optional<Update> optional = updateRepository.findById(fileId);
        Update update = optional.get();

        Path path = Paths.get("resources/plugins/" + fileId + "/");
        Resource file = new UrlResource(path.resolve(update.getFilename()).toUri());

        updateRepository.addDownload(fileId);

         if(!update.getExternal().equalsIgnoreCase("")) {
             HttpHeaders headers = new HttpHeaders();
             headers.add("Location", update.getExternal());
             return new ResponseEntity<String>(headers, HttpStatus.FOUND);
         }

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
