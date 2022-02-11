package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.config.custom.SiteConfig;
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
import com.imjustdoom.pluginsite.util.DateUtil;
import com.imjustdoom.pluginsite.util.FileUtil;
import com.imjustdoom.pluginsite.util.ImageUtil;
import com.imjustdoom.pluginsite.util.RequestUtil;
import com.imjustdoom.pluginsite.util.UrlUtil;
import lombok.AllArgsConstructor;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RequestMapping("/resources")
public class ResourcesController {
    private final LogoService logoService;
    private final ResourceService resourceService;
    private final ResourceRepository resourceRepository;
    private final AccountRepository accountRepository;
    private final UpdateRepository updateRepository;

    private final SiteConfig siteConfig;
    private final UrlUtil urlUtil;

    @GetMapping
    public String resources(Account account, @RequestParam(name = "category", required = false, defaultValue = "all") String category, @RequestParam(name = "search", required = false) String search, @RequestParam(name = "sort", required = false, defaultValue = "updated") String sort, @RequestParam(name = "page", required = false, defaultValue = "1") String page, Model model) {

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

            resources = resourceRepository.findAllByCategoryEqualsAndStatusEquals(category, "public", pageable).size();
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

    @GetMapping("/{id_s}")
    public String resource(Account account, @RequestParam(name = "software", required = false, defaultValue = "all") String softwareParam, @RequestParam(name = "sort", required = false, defaultValue = "uploaded") String sort, @PathVariable("id_s") String id_s, @RequestParam(name = "field", required = false, defaultValue = "") String field, Model model) throws MalformedURLException {
        int id;
        try {
            id = Integer.parseInt(id_s);
        } catch (NumberFormatException e) {
            return "error/404";
        }

        Optional<Resource> optionalResource = resourceRepository.findById(id);

        if (optionalResource.isEmpty()) return "error/404";

        Resource resource = resourceRepository.getById(id);

        if (!resource.getStatus().equalsIgnoreCase("public") && !account.getRole().equalsIgnoreCase("role_admin")) {
            model.addAttribute("account", account);
            return "error/resourceDeleted";
        }

        String description = urlUtil.encode(resource.getDescription());

        description.replaceAll("script", "error style=\"display:none;\"");
        Parser parser = Parser.builder().build();
        Node document = parser.parse(description);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);

        resource.setDescription(html);

        List<Update> updates = updateRepository.findAllByResourceIdAndStatusEquals(id, "public", Sort.by("uploaded").descending());

        model.addAttribute("download", updates.size() > 0 ? updates.get(0).getDownload() : null);
        model.addAttribute("account", account);
        model.addAttribute("resource", resource);
        model.addAttribute("editUrl", "/resources/%s/edit".formatted(id));
        model.addAttribute("uploadUrl", "/resources/%s/upload/".formatted(id));

        Integer totalDownloads = updateRepository.getTotalDownloads(resource.getId());
        model.addAttribute("totalDownloads", totalDownloads == null ? 0 : totalDownloads);
        model.addAttribute("created", resource.getCreated().format(DateUtil.getDateFormatter()));
        model.addAttribute("updated", resource.getUpdated().format(DateUtil.getDateFormatter()));

        switch (field.toLowerCase()) {
            case "updates":
                Sort sort1 = Sort.by(sort).descending();
                if (sort.equalsIgnoreCase("name")) sort1 = sort1.ascending();

                // TODO: improve getting the versions and software. 100% not the best way to do this
                List<Update> data;
                if (!softwareParam.equals("all")) {
                    data = updateRepository.findAllByResourceIdAndStatusEqualsAndSoftware(id, "public", softwareParam, sort1);
                    //data = new ArrayList<>();
                } else {
                    data = updateRepository.findAllByResourceIdAndStatusEquals(id, "public", sort1);
                }
                List<List<String>> versions = new ArrayList<>();
                List<String> versionLists = new ArrayList<>();
                List<String> softwareLists = new ArrayList<>();
                for (Update update : data) {
                    List<String> versionList = new ArrayList<>();

                    versionList.add(update.getVersions().get(0));
                    boolean first = true;
                    StringBuilder versionString = new StringBuilder();
                    String splitter = "";
                    for (String v : update.getVersions()) {
                        if (first) {
                            first = false;
                            continue;
                        }
                        versionString.append(splitter);
                        splitter = ", ";
                        versionString.append(v);
                    }
                    versionLists.add(versionString.toString());
                    versions.add(versionList);

                    StringBuilder softwareString = new StringBuilder();
                    splitter = "";
                    for (String v : update.getSoftware()) {
                        softwareString.append(splitter);
                        splitter = ", ";
                        softwareString.append(v);
                    }
                    softwareLists.add(softwareString.toString());
                }

                model.addAttribute("versions", versions);
                model.addAttribute("versionLists", versionLists);
                model.addAttribute("softwareLists", softwareLists);
                model.addAttribute("updates", data);
                return "resource/updates";
            default:
                return "resource/resource";
        }
    }

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

    @GetMapping("/{id}/edit")
    public String editResource(@RequestParam(name = "error", required = false) String error, @PathVariable("id") int id,
                               Model model, Account account, @ModelAttribute(name = "resource") CreateResourceRequest resourceModel) {
        model.addAttribute("error", error);
        model.addAttribute("maxUploadSize", this.siteConfig.getMaxUploadSize().toBytes());

        Optional<Resource> optionalResource = resourceRepository.findById(id);
        Resource resource = optionalResource.get();

        if (optionalResource.isEmpty()) return "error/404";

        model.addAttribute("id", id);
        model.addAttribute("authorid", resource.getAuthor().getId());
        model.addAttribute("resource", resourceModel.getName() == null ? resource : resourceModel);
        model.addAttribute("url", "/resources/" + id + "/edit");
        model.addAttribute("account", account);

        return "resource/edit";
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

    //TODO: Do sanity checks
    @PostMapping("/create")
    public String createSubmit(@ModelAttribute CreateResourceRequest resourceRequest, Account account, RedirectAttributes redirectAttributes) {
        return resourceService.postCreateResource(resourceRequest, account, siteConfig, redirectAttributes);
    }

    @GetMapping("/create")
    public String create(Model model, Account account, @RequestParam(name = "error", required = false) String error, @ModelAttribute(name = "resourceRequest") CreateResourceRequest resourceRequest) {
        model.addAttribute("error", error);
        model.addAttribute("limit", this.siteConfig.getMaxCreationsPerHour());
        model.addAttribute("account", account);
        model.addAttribute("resource", resourceRequest.getName() == null ? new CreateResourceRequest() : resourceRequest);
        model.addAttribute("url", this.siteConfig.getDomain() + "/resources");
        return "resource/create";
    }

    @GetMapping("/{id}/upload")
    public String uploadFile(@RequestParam(name = "error", required = false) String error, @PathVariable("id") int id, Model model, Account account, @ModelAttribute(name = "updateRequest") CreateUpdateRequest updateRequest) {

        // TODO: make the checkboxing also save

        Optional<Resource> optionalResource = resourceRepository.findById(id);
        Resource resource = optionalResource.get();

        if (optionalResource.isEmpty()) return "error/404";

        model.addAttribute("resource", resource);
        model.addAttribute("update", updateRequest.getName() == null ? new CreateUpdateRequest() : updateRequest);
        model.addAttribute("url", this.siteConfig.getDomain() + "/resources/%s/upload/".formatted(id));
        model.addAttribute("mainUrl", this.siteConfig.getDomain() + "/resources/%s".formatted(id));
        model.addAttribute("error", error);
        model.addAttribute("maxUploadSize", this.siteConfig.getMaxUploadSize().toBytes());
        model.addAttribute("account", account);
        model.addAttribute("limit", this.siteConfig.getMaxUpdatesPerHour());

        return "resource/upload";
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

    @GetMapping("/{id}/delete")
    public String delete(Account account, Model model, @PathVariable("id") int id) {
        model.addAttribute("account", account);
        model.addAttribute("resource", resourceRepository.findById(id).get());
        return "resource/delete";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") int id) {
        resourceRepository.updateStatusById(id, "removed");
        return "redirect:/";
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