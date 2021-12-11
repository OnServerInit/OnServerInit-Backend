package com.imjustdoom.pluginsite.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import com.imjustdoom.pluginsite.util.FileUtil;
import lombok.AllArgsConstructor;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Controller
@AllArgsConstructor
public class ResourcesController {

    private final LogoService logoService;
    private final ResourceRepository resourceRepository;
    private final AccountRepository accountRepository;
    private final UpdateRepository updateRepository;

    /**
     * @param model
     * @param timezone
     * @param username
     * @param userId
     * @return
     */
    @GetMapping("/resources")
    public String resources(@RequestParam(name = "search", required = false) String search, @RequestParam(name = "sort", required = false, defaultValue = "updated") String sort, @RequestParam(name = "page", required = false, defaultValue = "1") String page, Model model, TimeZone timezone, @CookieValue(value = "username", defaultValue = "") String username, @CookieValue(value = "id", defaultValue = "") String userId) throws SQLException {

        if (Integer.parseInt(page) < 1) return "redirect:/resources?page=1";

        String orderBy = switch (sort) {
            case "created" -> "ORDER BY creation DESC";
            case "updated" -> "ORDER BY updated DESC";
            case "downloads" -> "ORDER BY downloads DESC";
            case "alphabetical" -> "ORDER BY name ASC";
            default -> "";
        };

        List<SimpleResourceDto> data = new ArrayList<>();
        List<String> searchList = new ArrayList<>();
        int resources, total, remainder;

        if (search != null && !search.equals("")) {
            List<BoundExtractedResult<Resource>> searchResults;
            searchResults = FuzzySearch.extractSorted(search, resourceRepository.findAll(), Resource::getName);
            for (BoundExtractedResult<Resource> extractedResult : searchResults) {
                if (extractedResult.getScore() < 30) continue;
                searchList.add(extractedResult.getString());

                Optional<Resource> optionalResource = resourceRepository.findByNameEqualsIgnoreCase(extractedResult.getString());
                Resource resource = optionalResource.get();

                Integer downloads = updateRepository.getTotalDownloads(resource.getId());
                data.add(SimpleResourceDto.create(resource, downloads == null ? 0 : downloads));

            }

            resources = searchList.size();
            total = resources / 25;
            remainder = resources % 25;
            if (remainder > 1) total++;
        } else {

            total = resourceRepository.findAll().size() / 25;
            remainder = resourceRepository.findAll().size() % 25;
            if (remainder > 1) total++;

            Sort sort1 = Sort.by(sort).ascending();
            Pageable pageable = PageRequest.of(Integer.parseInt(page) - 1, 25, sort1);

            for (Resource resource : resourceRepository.findAll(pageable)) {
                Integer downloads = updateRepository.getTotalDownloads(resource.getId());
                data.add(SimpleResourceDto.create(resource, downloads == null ? 0 : downloads));
            }
        }

        model.addAttribute("total", total);
        model.addAttribute("files", data);
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        model.addAttribute("page", Integer.parseInt(page));

        return "resource/resources";
    }

    @GetMapping("/resources/{id}")
    public String resources(@RequestParam(name = "sort", required = false, defaultValue = "uploaded") String sort, @PathVariable("id") int id, @CookieValue(value = "id", defaultValue = "") String userId, @RequestParam(name = "field", required = false, defaultValue = "") String field, Model model, @CookieValue(value = "id", defaultValue = "") String authorid, @CookieValue(value = "username", defaultValue = "") String username, TimeZone timeZone) throws SQLException, MalformedURLException {

        Optional<Resource> optionalResource = resourceRepository.findById(id);

        if (optionalResource.isEmpty()) return "resource/404";

        Resource resource = resourceRepository.getById(id);

        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        model.addAttribute("resource", resource);
        model.addAttribute("editUrl", "/resources/edit/" + id);
        model.addAttribute("uploadUrl", "/resources/upload/" + id);
        model.addAttribute("authorid", authorid);
        model.addAttribute("totalDownloads", updateRepository.getTotalDownloads(resource.getId()));

        switch (field.toLowerCase()) {
            case "updates":
                String orderBy = switch (sort) {
                    case "uploaded" -> "ORDER BY uploaded DESC";
                    case "downloads" -> "ORDER BY downloads DESC";
                    case "alphabetical" -> "ORDER BY name ASC";
                    default -> "";
                };

                Sort sort1 = Sort.by(sort).descending();

                model.addAttribute("updates", updateRepository.findAllByResourceId(id, sort1));
                return "resource/updates";
            default:
                return "resource/resource";
        }
    }

    @GetMapping("/resources/edit/{id}/update/{fileId}")
    public String editResourceUpdate(@PathVariable("id") int id, @PathVariable("fileId") int fileId, Model model, @CookieValue(value = "username", defaultValue = "") String username, @CookieValue(value = "id", defaultValue = "") String userId) throws SQLException {
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);

        Optional<Resource> optionalResource = resourceRepository.findById(id);
        Resource resource = optionalResource.get();
        if (optionalResource.isEmpty()) return "resource/404";

        Optional<Update> optionalUpdate = updateRepository.findById(fileId);
        Update update = optionalUpdate.get();
        if (optionalUpdate.isEmpty()) return "resource/404";

        CreateUpdateRequest createUpdateRequest = new CreateUpdateRequest();
        createUpdateRequest.setName(update.getName());
        createUpdateRequest.setVersion(update.getVersion());
        createUpdateRequest.setDescription(update.getDescription());

        model.addAttribute("update", update);

        return "resource/editUpdate";
    }

    @PostMapping("/resources/edit/{id}/update/{fileId}")
    public String editUpdateSubmit(@ModelAttribute Update update, @PathVariable("id") int id, @PathVariable("fileId") int fileId) throws SQLException {

        /**PreparedStatement preparedStatement = PluginSiteApplication.getDB().getConn().prepareStatement(
                "UPDATE files SET name=?, description=?, version=? WHERE fileId=?");
        preparedStatement.setString(1, update.getName());
        preparedStatement.setString(2, update.getDescription());
        preparedStatement.setString(3, update.getVersion());
        preparedStatement.setString(4, String.valueOf(fileId));
        preparedStatement.executeUpdate();**/

        return "redirect:/resources/%s".formatted(id);
    }

    @GetMapping("/resources/edit/{id}")
    public String editResource(@RequestParam(name = "error", required = false) String error, @PathVariable("id") int id, Model model, @CookieValue(value = "username", defaultValue = "") String username, @CookieValue(value = "id", defaultValue = "") String userId) throws SQLException {
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        model.addAttribute("error", error);
        model.addAttribute("maxUploadSize", PluginSiteApplication.config.getMaxUploadSize());

        Optional<Resource> optionalResource = resourceRepository.findById(id);
        Resource resource = optionalResource.get();

        if (optionalResource.isEmpty()) return "resource/404";

        model.addAttribute("authorid", resource.getAuthor());
        model.addAttribute("resource", resource);
        model.addAttribute("url", "/resources/edit/" + id);
        return "resource/edit";
    }

    @PostMapping("/resources/edit/{id}")
    public String editSubmit(@RequestParam("logo") MultipartFile file, @ModelAttribute Resource resource, @PathVariable("id") int id) throws SQLException, IOException {

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

        /**PreparedStatement preparedStatement = PluginSiteApplication.getDB().getConn().prepareStatement(
                "UPDATE resources SET name=?, blurb=?, description=?, donation=?, source=?, support=? WHERE id=?;");
        preparedStatement.setString(1, resource.getName());
        preparedStatement.setString(2, resource.getBlurb());
        preparedStatement.setString(3, resource.getDescription());
        preparedStatement.setString(4, resource.getDonation());
        preparedStatement.setString(5, resource.getSource());
        preparedStatement.setString(6, resource.getSupport());
        preparedStatement.setString(7, String.valueOf(resource.getId()));
        preparedStatement.executeUpdate();**/

        return "redirect:/resources/%s".formatted(resource.getId());
    }

    //TODO: Do sanity checks
    @PostMapping("/resources/create")
    public RedirectView createSubmit(@ModelAttribute CreateResourceRequest resourceRequest, @CookieValue(value = "id", defaultValue = "") String authorid, @CookieValue(value = "id", defaultValue = "") String userId) throws SQLException, IOException {

        Optional<Account> optionalAccount = accountRepository.findById(Integer.valueOf(authorid));
        if (optionalAccount.isEmpty()) throw new RuntimeException(optionalAccount.toString());

        Account account = optionalAccount.get();

        Resource resource = new Resource(resourceRequest.getName(), resourceRequest.getDescription(),
                resourceRequest.getBlurb(), resourceRequest.getDonationLink(), resourceRequest.getSourceCodeLink(),
                "", account, resourceRequest.getSupportLink());

        resourceRepository.save(resource);

        int id = resource.getId();

        if (!FileUtil.doesFileExist("./resources/logos/" + id)) {
            try {
                Files.createDirectory(Paths.get("./resources/logos/" + id));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!FileUtil.doesFileExist("./resources/logos/" + id + "/logo.png")) {
            InputStream stream = PluginSiteApplication.class.getResourceAsStream("/pictures/logo.png");
            assert stream != null;
            Files.copy(stream, Path.of("./resources/logos/" + id + "/logo.png"));
        }

        return new RedirectView("/resources/" + id);
    }

    @GetMapping("/resources/create")
    public String create(Model model, @CookieValue(value = "username", defaultValue = "") String username, @CookieValue(value = "id", defaultValue = "") String userId) {
        model.addAttribute("username", username);
        model.addAttribute("resource", new CreateResourceRequest());
        model.addAttribute("userId", userId);
        return "resource/create";
    }

    @GetMapping("/resources/upload/{id}")
    public String uploadFile(@RequestParam(name = "error", required = false) String error, @CookieValue(value = "id", defaultValue = "") String userId, @PathVariable("id") int id, Model model, @CookieValue(value = "username", defaultValue = "") String username) throws SQLException {

        Optional<Resource> optionalResource = resourceRepository.findById(id);
        Resource resource = optionalResource.get();

        if (optionalResource.isEmpty()) return "resource/404";

        model.addAttribute("resource", resource);
        model.addAttribute("update", new CreateUpdateRequest());
        model.addAttribute("url", "/resources/upload/" + id);
        model.addAttribute("error", error);
        model.addAttribute("maxUploadSize", PluginSiteApplication.config.getMaxUploadSize());
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);

        return "resource/upload";
    }

    @PostMapping("/resources/upload/{id}")
    public String uploadFilePost(@PathVariable("id") int id, @RequestParam("file") MultipartFile file, @ModelAttribute CreateUpdateRequest updateRequest, @CookieValue(value = "id", defaultValue = "") String authorid) throws IOException, SQLException {

        if (file.isEmpty() && updateRequest.getExternalLink() == null) {
            //return "redirect:/resources/upload/" + updateRequest.getId() + "/?error=filesize";
        }
        if (!file.getOriginalFilename().endsWith(".jar") && updateRequest.getExternalLink() == null) {
            //return "redirect:/resources/upload/" + updateRequest.getId() + "/?error=filetype";
        }

        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        array.add("1.17.1");
        json.add("versions", array);

        Update update = new Update(updateRequest.getDescription(), file.getOriginalFilename(), updateRequest.getVersion(), "", updateRequest.getName(), json);

        update.setResource(resourceRepository.getById(id));
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
}