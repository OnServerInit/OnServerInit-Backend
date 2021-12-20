package com.imjustdoom.pluginsite.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.imjustdoom.pluginsite.PluginSiteApplication;
import com.imjustdoom.pluginsite.dtos.in.CreateResourceRequest;
import com.imjustdoom.pluginsite.dtos.in.CreateUpdateRequest;
import com.imjustdoom.pluginsite.dtos.out.SimpleResourceDto;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.model.Update;
import com.imjustdoom.pluginsite.repositories.AccountRepository;
import com.imjustdoom.pluginsite.repositories.ResourceRepository;
import com.imjustdoom.pluginsite.repositories.UpdateRepository;
import com.imjustdoom.pluginsite.service.LogoService;
import com.imjustdoom.pluginsite.service.ResourceService;
import com.imjustdoom.pluginsite.util.FileUtil;
import com.imjustdoom.pluginsite.util.UrlUtil;
import lombok.AllArgsConstructor;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class ResourcesController {

    private final LogoService logoService;
    private final ResourceService resourceService;
    private final ResourceRepository resourceRepository;
    private final AccountRepository accountRepository;
    private final UpdateRepository updateRepository;

    @GetMapping("/resources")
    public String resources(Account account, @RequestParam(name = "category", required = false, defaultValue = "all") String category, @RequestParam(name = "search", required = false) String search, @RequestParam(name = "sort", required = false, defaultValue = "updated") String sort, @RequestParam(name = "page", required = false, defaultValue = "1") String page, Model model) throws SQLException {

        // TODO: clean up more and make it easier to read
        if (Integer.parseInt(page) < 1) return "redirect:/resources?page=1";

        List<SimpleResourceDto> data;

        int resources, total, remainder;

        if (search != null && !search.equals("")) {

            data = resourceService.searchResources(search, sort, page);
            resources = FuzzySearch.extractSorted(search, resourceRepository.findAllByStatusEqualsIgnoreCase("public"), Resource::getName).size();
            total = resources / 25;
            remainder = resources % 25;
            if (remainder > 1) total++;

            model.addAttribute("results", resources);
        } else if (!category.equalsIgnoreCase("all")) {
            Sort sort1 = Sort.by(sort).descending();
            if (sort.equalsIgnoreCase("name")) sort1 = sort1.ascending();
            Pageable pageable = PageRequest.of(Integer.parseInt(page) - 1, 25, sort1);

            resources = resourceRepository.findAllByCategoryAndStatusEqualsIgnoreCase("public", category, pageable).size();
            total = resources / 25;
            remainder = resources % 25;
            if (remainder > 1) total++;

            data = resourceService.getResourcesWithCategory(sort, page, category);
        } else {

            resources = resourceRepository.findAll().size();
            total = resources / 25;
            remainder = resources % 25;
            if (remainder > 1) total++;

            data = resourceService.getResources(sort, page);
        }

        model.addAttribute("total", total);
        model.addAttribute("files", data);
        model.addAttribute("account", account);
        model.addAttribute("page", Integer.parseInt(page));

        return "resource/resources";
    }

    @GetMapping("/resources/{id_s}")
    public String resource(Account account, @RequestParam(name = "sort", required = false, defaultValue = "uploaded") String sort, @PathVariable("id_s") String id_s, @RequestParam(name = "field", required = false, defaultValue = "") String field, Model model) throws SQLException, MalformedURLException {
        int id;
        try {
            id = Integer.parseInt(id_s);
        } catch (NumberFormatException e) {
            return "error/404";
        }

        Optional<Resource> optionalResource = resourceRepository.findById(id);

        if (optionalResource.isEmpty()) return "error/404";

        Resource resource = resourceRepository.getById(id);

        if(!resource.getStatus().equalsIgnoreCase("public") && !account.getRole().equalsIgnoreCase("role_admin")) {
            model.addAttribute("account", account);
            return "error/resourceDeleted";
        }

        String description = UrlUtil.encode(resource.getDescription());

        description.replaceAll("script", "error style=\"display:none;\"");
        Parser parser = Parser.builder().build();
        Node document = parser.parse(description);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);

        resource.setDescription(html);

        model.addAttribute("account", account);
        model.addAttribute("resource", resource);
        model.addAttribute("editUrl", "/resources/%s/edit".formatted(id));
        model.addAttribute("uploadUrl", "/resources/%s/upload/".formatted(id));

        Integer totalDownloads = updateRepository.getTotalDownloads(resource.getId());
        model.addAttribute("totalDownloads", totalDownloads == null ? 0 : totalDownloads);

        switch (field.toLowerCase()) {
            case "updates":
                Sort sort1 = Sort.by(sort).descending();

                // TODO: improve getting the versions and software. 100% not the best way to do this
                List<Update> data = updateRepository.findAllByResourceId(id, sort1);
                List<List<String>> versions = new ArrayList<>();
                List<String> versionLists = new ArrayList<>();
                for (Update update : data) {
                    JsonObject jsonObject = JsonParser.parseString(update.getVersions()).getAsJsonObject();
                    List<String> versionList = new ArrayList<>();

                    versionList.add(jsonObject.get("versions").getAsJsonArray().get(0).getAsString());
                    boolean first = true;
                    StringBuilder versionString = new StringBuilder();
                    String splitter = "";
                    for (JsonElement v : jsonObject.get("versions").getAsJsonArray()) {
                        if (first) {
                            first = false;
                            continue;
                        }
                        versionString.append(splitter);
                        splitter = ", ";
                        versionString.append(v.getAsString());
                    }
                    versionLists.add(versionString.toString());
                    versions.add(versionList);
                }

                model.addAttribute("versions", versions);
                model.addAttribute("versionLists", versionLists);
                model.addAttribute("updates", data);
                return "resource/updates";
            default:
                return "resource/resource";
        }
    }

    @GetMapping("/resources/{id}/edit/update/{fileId}")
    public String editResourceUpdate(@PathVariable("id") int id, @PathVariable("fileId") int fileId, Model model, Account account) {

        Optional<Resource> optionalResource = resourceRepository.findById(id);
        if (optionalResource.isEmpty()) return "error/404";

        Optional<Update> optionalUpdate = updateRepository.findById(fileId);
        if (optionalUpdate.isEmpty()) return "error/404";
        Update update = optionalUpdate.get();

        model.addAttribute("update", update);
        model.addAttribute("url", PluginSiteApplication.config.domain + "/resources/" + id);
        model.addAttribute("account", account);
        model.addAttribute("resourceid", id);

        return "resource/editUpdate";
    }

    @PostMapping("/resources/{id}/edit/update/{fileId}")
    public String editUpdateSubmit(@ModelAttribute Update update, @PathVariable("id") int id, @PathVariable("fileId") int fileId) {

        //update.getId() doesnt actually return the id of the update for some reason
        // figure out why in the future
        updateRepository.setInfo(fileId, update.getName(), update.getDescription(), update.getVersion());

        return "redirect:/resources/%s".formatted(id);
    }

    @GetMapping("/resources/{id}/edit")
    public String editResource(@RequestParam(name = "error", required = false) String error, @PathVariable("id") int id, Model model, Account account) {
        model.addAttribute("error", error);
        model.addAttribute("maxUploadSize", PluginSiteApplication.config.getMaxUploadSizeByte());

        Optional<Resource> optionalResource = resourceRepository.findById(id);
        Resource resource = optionalResource.get();

        if (optionalResource.isEmpty()) return "error/404";

        model.addAttribute("authorid", resource.getAuthor());
        model.addAttribute("resource", resource);
        model.addAttribute("url", "/resources/" + id + "/edit");
        model.addAttribute("account", account);

        return "resource/edit";
    }

    @PostMapping("/resources/{id}/edit")
    public String editSubmit(@RequestParam("logo") MultipartFile file, @ModelAttribute Resource resource, @PathVariable("id") int id) throws IOException {

        if (!file.isEmpty()) {
            if (!file.getContentType().contains("image")) {
                return "redirect:/resources/edit/%s/?error=logotype".formatted(resource.getId());
            }

            if (file.getSize() > 10000) {
                return "redirect:/resources/edit/%s/?error=filesize".formatted(resource.getId());
            }

            BufferedImage image = ImageIO.read(file.getInputStream());

            if (image.getHeight() != image.getWidth())
                return "redirect:/resources/edit/%s/?error=logosize".formatted(resource.getId());

            Path destinationFile = Path.of("./resources/logos/%s/logo.png".formatted(id)).normalize().toAbsolutePath();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }

        resourceRepository.setInfo(resource.getId(), resource.getName(), resource.getBlurb(), resource.getDescription(),
                resource.getDonation(), resource.getSource(), resource.getSupport(), resource.getCategory());

        return "redirect:/resources/%s".formatted(resource.getId());
    }

    //TODO: Do sanity checks
    @PostMapping("/resources/create")
    public String createSubmit(@ModelAttribute CreateResourceRequest resourceRequest, Account account) {
        if (resourceRepository.getResourcesCreateLastHour(account.getId()) > PluginSiteApplication.config.maxCreationsPerHour)
            return "redirect:/resources/create?error=createlimit";

        if (resourceRepository.existsByNameEqualsIgnoreCase(resourceRequest.getName()))
            return "redirect:/resources/create?error=nametaken";

        Resource resource = resourceService.createResource(resourceRequest, account);

        logoService.createLogo(resource.getId());

        return "redirect:/resources/%s".formatted(resource.getId());
    }

    @GetMapping("/resources/create")
    public String create(Model model, Account account, @RequestParam(name = "error", required = false) String error) {
        model.addAttribute("error", error);
        model.addAttribute("limit", PluginSiteApplication.config.maxCreationsPerHour);
        model.addAttribute("account", account);
        model.addAttribute("resource", new CreateResourceRequest());
        model.addAttribute("url", PluginSiteApplication.config.domain + "/resources");
        return "resource/create";
    }

    @GetMapping("/resources/{id}/upload")
    public String uploadFile(@RequestParam(name = "error", required = false) String error, @PathVariable("id") int id, Model model, Account account) {

        Optional<Resource> optionalResource = resourceRepository.findById(id);
        Resource resource = optionalResource.get();

        if (optionalResource.isEmpty()) return "error/404";

        model.addAttribute("resource", resource);
        model.addAttribute("update", new CreateUpdateRequest());
        model.addAttribute("url", PluginSiteApplication.config.domain + "/resources/%s/upload/".formatted(id));
        model.addAttribute("mainUrl", PluginSiteApplication.config.domain + "/resources/%s".formatted(id));
        model.addAttribute("error", error);
        model.addAttribute("maxUploadSize", PluginSiteApplication.config.getMaxUploadSizeByte());
        model.addAttribute("account", account);
        model.addAttribute("limit", PluginSiteApplication.config.maxUpdatesPerHour);

        return "resource/upload";
    }

    @PostMapping("/resources/{id}/upload")
    public String uploadFilePost(Account account, @RequestParam(name = "softwareCheckbox") List<String> softwareBoxes, @RequestParam(name = "versionCheckbox") List<String> versionBoxes, @PathVariable("id") int id, @RequestParam("file") MultipartFile file, @ModelAttribute CreateUpdateRequest updateRequest) throws IOException {

        if (updateRepository.getUpdatesCreateLastHour(account.getId()) > PluginSiteApplication.config.maxUpdatesPerHour) {
            return "redirect:/resources/%s/upload?error=uploadlimit".formatted(id);
        }

        if ((file.isEmpty() || file.getSize() > PluginSiteApplication.config.getMaxUploadSizeByte()) && updateRequest.getExternalLink().equals("")) {
            return "redirect:/resources/%s/upload?error=filesize".formatted(id);
        }
        if ((!file.getOriginalFilename().endsWith(".jar") && !file.getOriginalFilename().endsWith(".zip")) && updateRequest.getExternalLink().equals("")) {
            return "redirect:/resources/%s/upload?error=filetype".formatted(id);
        }

        JsonObject versions = new JsonObject();
        JsonArray versionsArray = new JsonArray();
        for (String s : versionBoxes) versionsArray.add(s);
        versions.add("versions", versionsArray);

        JsonObject software = new JsonObject();
        JsonArray softwareArray = new JsonArray();
        for (String s : softwareBoxes) softwareArray.add(s);
        software.add("software", softwareArray);

        Update update = new Update(updateRequest.getDescription(), file.getOriginalFilename(),
                updateRequest.getVersion(), "", updateRequest.getName(), versions, software,
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

        String download = "%s/files/%s/download/%s".formatted(PluginSiteApplication.config.domain, update.getResource().getId(), update.getId());
        resourceRepository.setDownload(id, download);
        updateRepository.setDownload(update.getId(), download);

        return "redirect:/resources/%s".formatted(id);
    }

    @GetMapping("/resources/{id}/delete")
    public String delete(Account account, Model model, @PathVariable("id") int id) {
        model.addAttribute("account", account);
        model.addAttribute("resource", resourceRepository.findById(id).get());
        return "resource/delete";
    }

    @PostMapping("/resources/{id}/delete")
    public String delete(@PathVariable("id") int id) {
        resourceRepository.updateStatusById(id, "removed");
        return "redirect:/";
    }
}