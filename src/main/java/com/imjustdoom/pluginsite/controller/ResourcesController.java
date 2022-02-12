package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.config.custom.SiteConfig;
import com.imjustdoom.pluginsite.dtos.in.CreateUpdateRequest;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Update;
import com.imjustdoom.pluginsite.repositories.AccountRepository;
import com.imjustdoom.pluginsite.repositories.ResourceRepository;
import com.imjustdoom.pluginsite.repositories.UpdateRepository;
import com.imjustdoom.pluginsite.service.LogoService;
import com.imjustdoom.pluginsite.service.ResourceService;
import com.imjustdoom.pluginsite.util.FileUtil;
import com.imjustdoom.pluginsite.util.UrlUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@AllArgsConstructor
public class ResourcesController {
    private final LogoService logoService;
    private final ResourceService resourceService;
    private final ResourceRepository resourceRepository;
    private final AccountRepository accountRepository;
    private final UpdateRepository updateRepository;

    private final SiteConfig siteConfig;
    private final UrlUtil urlUtil;

    @PostMapping("/{id}/upload")
    public String uploadFilePost(Account account, @RequestParam(name = "softwareCheckbox") List<String> softwareBoxes,
                                 @RequestParam(name = "versionCheckbox") List<String> versionBoxes,
                                 @PathVariable("id") int id, @RequestParam("file") MultipartFile file,
                                 @ModelAttribute CreateUpdateRequest updateRequest, RedirectAttributes redirectAttributes)
            throws IOException {

        redirectAttributes.addFlashAttribute("updateRequest", updateRequest);
        if (updateRepository.getUpdatesCreateLastHour(account.getId()) > this.siteConfig.getMaxUpdatesPerHour()) {
            return "redirect:/resources/%s/upload?error=uploadlimit".formatted(id);
        }

        if ((file.isEmpty() || file.getSize() > this.siteConfig.getMaxUploadSize().toBytes()) && updateRequest.getExternalLink().equals("")) {
            return "redirect:/resources/%s/upload?error=filesize".formatted(id);
        }
        if ((!file.getOriginalFilename().endsWith(".jar") && !file.getOriginalFilename().endsWith(".zip")) && updateRequest.getExternalLink().equals("")) {
            return "redirect:/resources/%s/upload?error=filetype".formatted(id);
        }

        if (updateRequest.getName().equalsIgnoreCase("")
                || updateRequest.getVersion().equalsIgnoreCase("")
                || updateRequest.getDescription().equalsIgnoreCase(""))
            return "redirect:/resources/%s/upload?error=invalidinput";

        Update update = new Update(updateRequest.getDescription(), file.getOriginalFilename(),
                updateRequest.getVersion(), "", updateRequest.getName(), versionBoxes, softwareBoxes,
                resourceRepository.findById(id).get(), updateRequest.getExternalLink());
        updateRepository.save(update);

        if (updateRequest.getExternalLink() == null || updateRequest.getExternalLink().equals("")) {
            if (!FileUtil.doesFileExist("./resources/plugins/" + update.getId())) {
                try {
                    Files.createDirectory(Paths.get("./resources/plugins/" + update.getId()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Path destinationFile = Path.of("./resources/plugins/" + update.getId() + "/" + file.getOriginalFilename()).normalize().toAbsolutePath();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }

        String download = "%s/files/%s/download/%s".formatted(this.siteConfig.getDomain(), update.getResource().getId(), update.getId());
        resourceRepository.setDownload(id, download);
        updateRepository.setDownload(update.getId(), download);

        return "redirect:/resources/%s".formatted(id);
    }
}