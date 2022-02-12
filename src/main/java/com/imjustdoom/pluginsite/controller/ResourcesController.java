package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.config.custom.SiteConfig;
import com.imjustdoom.pluginsite.dtos.in.CreateResourceRequest;
import com.imjustdoom.pluginsite.dtos.in.CreateUpdateRequest;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.model.Update;
import com.imjustdoom.pluginsite.repositories.AccountRepository;
import com.imjustdoom.pluginsite.repositories.ResourceRepository;
import com.imjustdoom.pluginsite.repositories.UpdateRepository;
import com.imjustdoom.pluginsite.service.LogoService;
import com.imjustdoom.pluginsite.service.ResourceService;
import com.imjustdoom.pluginsite.util.FileUtil;
import com.imjustdoom.pluginsite.util.ImageUtil;
import com.imjustdoom.pluginsite.util.RequestUtil;
import com.imjustdoom.pluginsite.util.UrlUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class ResourcesController {
    private final LogoService logoService;
    private final ResourceService resourceService;
    private final ResourceRepository resourceRepository;
    private final AccountRepository accountRepository;
    private final UpdateRepository updateRepository;

    private final SiteConfig siteConfig;
    private final UrlUtil urlUtil;

    @GetMapping("/{id}/edit/update/{fileId}")
    public String editResourceUpdate(@RequestParam(name = "error", required = false) String error, @PathVariable("id") int id, @PathVariable("fileId") int fileId, Model model, Account account) {

        Optional<Resource> optionalResource = resourceRepository.findById(id);
        if (optionalResource.isEmpty()) return "error/404";

        Optional<Update> optionalUpdate = updateRepository.findById(fileId);
        if (optionalUpdate.isEmpty()) return "error/404";
        Update update = optionalUpdate.get();

        model.addAttribute("error", error);
        model.addAttribute("update", update);
        model.addAttribute("url", this.siteConfig.getDomain() + "/resources/" + id);
        model.addAttribute("account", account);
        model.addAttribute("resourceid", id);

        return "resource/editUpdate";
    }

    @PostMapping("/{id}/edit/update/{fileId}")
    public String editUpdateSubmit(@ModelAttribute Update update, @PathVariable("id") int id, @PathVariable("fileId") int fileId) {

        if (update.getName().equalsIgnoreCase("")
                || update.getVersion().equalsIgnoreCase("")
                || update.getDescription().equalsIgnoreCase(""))
            return "redirect:/resources/%s/edit/update/%s?error=invalidinput".formatted(id, fileId);

        //update.getId() doesnt actually return the id of the update for some reason
        // figure out why in the future
        updateRepository.setInfo(fileId, update.getName(), update.getDescription(), update.getVersion());

        return "redirect:/resources/%s".formatted(id);
    }

    @PostMapping("/{id}/edit")
    public String editSubmit(@RequestParam("logo") MultipartFile file, RedirectAttributes redirectAttributes,
                             @ModelAttribute CreateResourceRequest resource, @PathVariable("id") int id) {

        redirectAttributes.addFlashAttribute("resource", resource);
        if (resourceRepository.existsByNameEqualsIgnoreCaseAndIdEqualsNot(id, resource.getName()))
            return "redirect:/resources/%s/edit?error=nametaken".formatted(id);

        if (resource.getName().equalsIgnoreCase("")
                || resource.getBlurb().equalsIgnoreCase("")
                || resource.getDescription().equalsIgnoreCase(""))
            return "redirect:/resources/%s/edit?error=invalidinput".formatted(id);

        if (!file.isEmpty()) {
            if (file.getSize() > 1024000) {
                return "redirect:/resources/%s/edit?error=filesize".formatted(id);
            }

            resourceRepository.updateLogoById(id, ImageUtil.handleImage(file));
        }
        // todo handle errors from ImageUtil

        resourceRepository.setInfo(id, resource.getName(), resource.getBlurb(), resource.getDescription(),
                resource.getDonation(), resource.getSource(), resource.getSupport(), resource.getCategory());

        return "redirect:/resources/%s".formatted(id);
    }

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

    @PostMapping("/{id}/update/{update}/status")
    public ResponseEntity<HashMap<String, String>> changeStatus(Account account, @PathVariable("id") int id,
                                                                @PathVariable("update") int updateId,
                                                                @RequestBody String request) {

        HashMap<String, String> response = new HashMap<>();

        Optional<Resource> optionalResource = resourceRepository.findById(id);
        Resource resource = optionalResource.get();

        if (account.getId() != resource.getAuthor().getId()) {
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        HashMap<String, String> params = RequestUtil.getParams(request);
        String status = params.get("status");

        updateRepository.updateStatusById(updateId, status);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}