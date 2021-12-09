package com.imjustdoom.pluginsite.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.imjustdoom.pluginsite.PluginSiteApplication;
import com.imjustdoom.pluginsite.cache.ResourceNames;
import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.model.ResourceFile;
import com.imjustdoom.pluginsite.model.Update;
import com.imjustdoom.pluginsite.util.AccountUtil;
import com.imjustdoom.pluginsite.util.DateUtil;
import com.imjustdoom.pluginsite.util.FileUtil;
import com.imjustdoom.pluginsite.util.StringUtil;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
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
import java.util.*;

@Controller
public class ResourcesController {

    /**
     * @param model
     * @param timezone
     * @param username
     * @param userId
     * @return
     */
    @GetMapping("/resources")
    public String resources(@RequestParam(name = "search", required = false) String search, @RequestParam(name = "sort", required = false, defaultValue = "updated") String sort, @RequestParam(name = "page", required = false, defaultValue = "1") String page, Model model, TimeZone timezone, @CookieValue(value = "username", defaultValue = "") String username, @CookieValue(value = "id", defaultValue = "") String userId) {
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        model.addAttribute("page", Integer.parseInt(page));

        if(search != null) {
            List<ExtractedResult> searchList;
            searchList = FuzzySearch.extractSorted(search, ResourceNames.names);
            for (int i = 0; i < searchList.size(); i++) {
                System.out.println(searchList.get(i));
            }
        }

        if (Integer.parseInt(page) < 1) return "redirect:/resources?page=1";
        List<Resource> data = new ArrayList<>();
        try {
            ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT COUNT(id) FROM resources");

            rs.next();
            int resources = rs.getInt(1);

            int startRow = Integer.parseInt(page) * 25 - 25;
            int endRow = startRow + 25;
            int total = resources / 25;
            int remainder = resources % 25;
            if (remainder > 1) total++;

            model.addAttribute("total", total);

            String orderBy = switch (sort) {
                case "created" -> "ORDER BY creation DESC";
                case "updated" -> "ORDER BY updated DESC";
                case "downloads" -> "ORDER BY downloads DESC";
                case "alphabetical" -> "ORDER BY name ASC";
                default -> "";
            };

            rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM resources %s LIMIT %s,25".formatted(orderBy, startRow));
            while (rs.next()) {
                Resource resource = new Resource();
                resource.setName(rs.getString("name"));
                resource.setBlurb(rs.getString("blurb"));
                resource.setId(rs.getInt("id"));
                resource.setDownloads(rs.getInt("downloads"));
                resource.setCreated(DateUtil.formatDate(rs.getInt("creation"), timezone));
                resource.setUpdated(DateUtil.formatDate(rs.getInt("updated"), timezone));
                data.add(resource);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("files", data);

        return "resource/resources";
    }

    @GetMapping("/resources/{id}")
    public String resources(@RequestParam(name = "sort", required = false, defaultValue = "uploaded") String sort, @PathVariable("id") int id, @CookieValue(value = "id", defaultValue = "") String userId, @RequestParam(name = "field", required = false, defaultValue = "") String field, Model model, @CookieValue(value = "id", defaultValue = "") String authorid, @CookieValue(value = "username", defaultValue = "") String username, TimeZone timeZone) throws SQLException, MalformedURLException {

        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM resources WHERE id=%s".formatted(id));
        if (!rs.next()) return "resource/404";

        Resource resource = new Resource();

        resource.setId(id);
        resource.setName(rs.getString("name"));
        resource.setDescription(StringUtil.markdownToHtml(rs.getString("description")));
        resource.setBlurb(rs.getString("blurb"));
        resource.setDonation(rs.getString("donation"));
        resource.setSource(rs.getString("source"));
        resource.setSupport(rs.getString("support"));
        resource.setAuthorid(rs.getInt("authorid"));
        resource.setDownload(rs.getString("download"));
        resource.setCreated(DateUtil.formatDate(rs.getInt("creation"), timeZone));
        resource.setUpdated(DateUtil.formatDate(rs.getInt("updated"), timeZone));
        resource.setDownloads(rs.getInt("downloads"));
        resource.setAuthor(AccountUtil.getAuthorFromId(resource.getAuthorid()));

        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        model.addAttribute("resource", resource);
        model.addAttribute("editUrl", "/resources/edit/" + id);
        model.addAttribute("uploadUrl", "/resources/upload/" + id);
        model.addAttribute("authorid", authorid);

        switch (field.toLowerCase()) {
            case "updates":
                String orderBy = switch (sort) {
                    case "uploaded" -> "ORDER BY uploaded DESC";
                    case "downloads" -> "ORDER BY downloads DESC";
                    case "alphabetical" -> "ORDER BY name ASC";
                    default -> "";
                };

                rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM files WHERE id=%s %s".formatted(id, orderBy));
                List<Update> data = new ArrayList<>();
                while (rs.next()) {
                    Update update = new Update();
                    update.setName(rs.getString("name"));
                    update.setUploaded(DateUtil.formatDate(rs.getInt("uploaded"), timeZone));
                    update.setFilename(rs.getString("filename"));
                    update.setDescription(rs.getString("description"));
                    update.setVersions(rs.getString("versions"));
                    update.setFileId(rs.getInt("fileId"));
                    update.setDownloads(rs.getInt("downloads"));
                    update.setVersion(rs.getString("version"));
                    if (!rs.getString("external").equals("")) {
                        update.setDownload(rs.getString("external"));
                    } else {
                        update.setDownload(PluginSiteApplication.config.domain + "/files/" + id + "/download/" + update.getFileId());
                    }
                    data.add(update);
                }
                model.addAttribute("updates", data);
                return "resource/updates";
            default:
                return "resource/resource";
        }
    }

    @PostMapping("/resources/{id}")
    public String resourcePost(@ModelAttribute Resource resource, @PathVariable("id") int id, Model model) {

        model.addAttribute("resource", resource);

        return "resource/resource";
    }

    @GetMapping("/resources/edit/{id}/update/{fileId}")
    public String editResourceUpdate(@PathVariable("id") int id, @PathVariable("fileId") int fileId, Model model, @CookieValue(value = "username", defaultValue = "") String username, @CookieValue(value = "id", defaultValue = "") String userId) throws SQLException {
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);

        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM resources WHERE id=%s".formatted(id));
        if (!rs.next()) return "resource/404";

        int authorid = rs.getInt("authorid");
        model.addAttribute("authorid", String.valueOf(authorid));

        rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM files WHERE fileId=%s".formatted(fileId));
        if (!rs.next()) return "resource/404";

        Update update = new Update();
        update.setFileId(fileId);
        update.setName(rs.getString("name"));
        update.setVersion(rs.getString("version"));
        update.setDescription(rs.getString("description"));
        model.addAttribute("update", update);

        return "resource/editUpdate";
    }

    @PostMapping("/resources/edit/{id}/update/{fileId}")
    public String editUpdateSubmit(@ModelAttribute Update update, @PathVariable("id") int id, @PathVariable("fileId") int fileId) throws SQLException {
        PreparedStatement preparedStatement = PluginSiteApplication.getDB().getConn().prepareStatement(
                "UPDATE files SET name=?, description=?, version=? WHERE fileId=?");
        preparedStatement.setString(1, update.getName());
        preparedStatement.setString(2, update.getDescription());
        preparedStatement.setString(3, update.getVersion());
        preparedStatement.setString(4, String.valueOf(fileId));
        preparedStatement.executeUpdate();

        return "redirect:/resources/%s".formatted(id);
    }

    @GetMapping("/resources/edit/{id}")
    public String editResource(@RequestParam(name = "error", required = false) String error, @PathVariable("id") int id, Model model, @CookieValue(value = "username", defaultValue = "") String username, @CookieValue(value = "id", defaultValue = "") String userId) throws SQLException {
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        model.addAttribute("error", error);
        model.addAttribute("maxUploadSize", PluginSiteApplication.config.getMaxUploadSize());

        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM resources WHERE id=%s".formatted(id));
        if (!rs.next()) return "resource/404";

        int authorid = rs.getInt("authorid");
        model.addAttribute("authorid", String.valueOf(authorid));

        Resource resource = new Resource();
        resource.setId(id);
        resource.setName(rs.getString("name"));
        resource.setDescription(rs.getString("description"));
        resource.setBlurb(rs.getString("blurb"));
        resource.setDonation(rs.getString("donation"));
        resource.setSource(rs.getString("source"));
        resource.setSource(rs.getString("support"));
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

        PreparedStatement preparedStatement = PluginSiteApplication.getDB().getConn().prepareStatement(
                "UPDATE resources SET name=?, blurb=?, description=?, donation=?, source=?, support=? WHERE id=?;");
        preparedStatement.setString(1, resource.getName());
        preparedStatement.setString(2, resource.getBlurb());
        preparedStatement.setString(3, resource.getDescription());
        preparedStatement.setString(4, resource.getDonation());
        preparedStatement.setString(5, resource.getSource());
        preparedStatement.setString(6, resource.getSupport());
        preparedStatement.setString(7, String.valueOf(resource.getId()));
        preparedStatement.executeUpdate();

        return "redirect:/resources/%s".formatted(resource.getId());
    }

    @PostMapping("/resources/create")
    public RedirectView createSubmit(@ModelAttribute Resource resource, @CookieValue(value = "id", defaultValue = "") String authorid, @CookieValue(value = "id", defaultValue = "") String userId) throws SQLException, IOException {

        System.out.println(resource.getSupport());
        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT id FROM resources WHERE id=(SELECT MAX(id) FROM resources) GROUP BY id");

        int id;

        if (!rs.next()) id = 0;
        else id = rs.getInt("id");

        id++;

        resource.setId(id);

        long created = new Date().getTime() / 1000;
        PluginSiteApplication.getDB().getStmt().executeUpdate("INSERT INTO resources (id, name, blurb, description, download, donation, source, support, creation, updated, downloads, authorid) " +
                "VALUES(" + id + ", '" + resource.getName() + "', '" + resource.getBlurb() + "','" + resource.getDescription() + "', '', '" + resource.getDonation() + "', '" + resource.getSource() + "', '" + resource.getSupport() + "', " + created + ", " + created + ", 0, " + authorid + ")");

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

        return new RedirectView("/resources/" + resource.getId());
    }

    @GetMapping("/resources/create")
    public String create(Model model, @CookieValue(value = "username", defaultValue = "") String username, @CookieValue(value = "id", defaultValue = "") String userId) {
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        model.addAttribute("resource", new Resource());
        return "resource/create";
    }

    @GetMapping("/resources/upload/{id}")
    public String uploadFile(@RequestParam(name = "error", required = false) String error, @CookieValue(value = "id", defaultValue = "") String userId, @PathVariable("id") int id, Model model, @CookieValue(value = "username", defaultValue = "") String username) throws SQLException {

        model.addAttribute("error", error);
        model.addAttribute("maxUploadSize", PluginSiteApplication.config.getMaxUploadSize());
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM resources WHERE id=%s".formatted(id));
        if (!rs.next()) return "resource/404";

        int authorid = rs.getInt("authorid");
        model.addAttribute("authorid", String.valueOf(authorid));

        ResourceFile file = new ResourceFile();
        file.setId(id);
        model.addAttribute("resourceFile", file);
        model.addAttribute("url", "/resources/upload/" + id);
        return "resource/upload";
    }

    @PostMapping("/resources/upload/{id}")
    public String uploadFilePost(@RequestParam("file") MultipartFile file, @ModelAttribute ResourceFile resourceFile, @CookieValue(value = "id", defaultValue = "") String authorid) throws IOException, SQLException {

        if (file.isEmpty() && resourceFile.getExternalDownload() == null) {
            return "redirect:/resources/upload/" + resourceFile.getId() + "/?error=filesize";
        }
        if (!file.getOriginalFilename().endsWith(".jar") && resourceFile.getExternalDownload() == null) {
            return "redirect:/resources/upload/" + resourceFile.getId() + "/?error=filetype";
        }

        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT fileId FROM files WHERE fileId=(SELECT MAX(fileId) FROM files) GROUP BY fileId");

        int fileId;

        if (!rs.next()) fileId = 0;
        else fileId = rs.getInt("fileId");

        fileId++;

        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        array.add("1.17.1");
        json.add("versions", array);

        String SQL, download;

        System.out.println(resourceFile.getExternalDownload());

        long created = new Date().getTime() / 1000;
        if (resourceFile.getExternalDownload() == null || resourceFile.getExternalDownload().equals("")) {
            if (!FileUtil.doesFileExist("./resources/plugins/" + fileId)) {
                try {
                    Files.createDirectory(Paths.get("./resources/plugins/" + fileId));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Path destinationFile = Path.of("./resources/plugins/" + fileId + "/" + file.getOriginalFilename()).normalize().toAbsolutePath();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }

            SQL = "INSERT INTO files (id, fileId, name, version, filename, description, versions, uploaded, external, downloads)" +
                    "VALUES(%s, %s, '%s', '%s', '%s', '%s', '%s', %s, '%s', %s)"
                            .formatted(resourceFile.getId(), fileId, resourceFile.getName(), resourceFile.getVersion(), file.getOriginalFilename(), resourceFile.getDescription(),
                                    json, created, "", 0);

            download = "%s/files/%s/download/%s".formatted(PluginSiteApplication.config.domain, resourceFile.getId(), fileId);
        } else {
            SQL = "INSERT INTO files (id, fileId, name, version, filename, description, versions, uploaded, external, downloads)" +
                    "VALUES(%s, %s, '%s', '%s', '%s', '%s', '%s', %s, '%s', %s)"
                            .formatted(resourceFile.getId(), fileId, resourceFile.getName(), resourceFile.getVersion(), "", resourceFile.getDescription(),
                                    json, created, resourceFile.getExternalDownload(), 0);
            download = "%s/files/%s/download/%s".formatted(PluginSiteApplication.config.domain, resourceFile.getId(), fileId);
        }

        PluginSiteApplication.getDB().getStmt().executeUpdate(SQL);

        System.out.println(download);

        PluginSiteApplication.getDB().getStmt().executeUpdate("UPDATE resources SET download='%s', updated='%s' WHERE id='%s'"
                .formatted(download, created, resourceFile.getId()));

        return "redirect:/resources/%s".formatted(resourceFile.getId());
    }
}