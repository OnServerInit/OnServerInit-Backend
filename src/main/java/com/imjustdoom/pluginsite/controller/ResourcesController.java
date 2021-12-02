package com.imjustdoom.pluginsite.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.imjustdoom.pluginsite.PluginSiteApplication;
import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.model.ResourceFile;
import com.imjustdoom.pluginsite.model.Update;
import com.imjustdoom.pluginsite.util.AccountUtil;
import com.imjustdoom.pluginsite.util.DateUtil;
import com.imjustdoom.pluginsite.util.FileUtil;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Controller
public class ResourcesController {

    @GetMapping("/resources")
    public String resources(Model model, TimeZone timezone, @CookieValue(value = "username", defaultValue = "") String username, @CookieValue(value = "id", defaultValue = "") String userId) {
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        List<Resource> data = new ArrayList<>();
        try {
            ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM resources");
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
        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            //if (PluginSiteApplication.getDB().getStmt() != null) { PluginSiteApplication.getDB().getStmt().close(); }
        }

        model.addAttribute("files", data);

        return "resource/resources";
    }

    @GetMapping("/resources/{id}")
    public String resources(@PathVariable("id") int id, @CookieValue(value = "id", defaultValue = "") String userId, @RequestParam(name = "field", required = false, defaultValue = "") String field, Model model, @CookieValue(value = "id", defaultValue = "") String authorid, @CookieValue(value = "username", defaultValue = "") String username, TimeZone timeZone) throws SQLException {

        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM resources WHERE id=" + id);
        if(!rs.next()) return "resource/404";

        Resource resource = new Resource();

        resource.setId(id);
        resource.setName(rs.getString("name"));
        resource.setDescription(markdownToHtml(rs.getString("description")));
        resource.setBlurb(rs.getString("blurb"));
        resource.setDonation(rs.getString("donation"));
        resource.setSource(rs.getString("source"));
        resource.setAuthorid(rs.getInt("authorid"));
        resource.setDownload(rs.getString("download"));
        resource.setCreated(DateUtil.formatDate(rs.getInt("creation"), timeZone));
        resource.setUpdated(DateUtil.formatDate(rs.getInt("updated"), timeZone));
        resource.setDownloads(rs.getInt("downloads"));
        resource.setAuthor(AccountUtil.getAuthorFromId(resource.getId()));

        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        model.addAttribute("resource", resource);
        model.addAttribute("editUrl", "/resources/edit/" + id);
        model.addAttribute("uploadUrl", "/resources/upload/" + id);
        model.addAttribute("authorid", authorid);

        switch (field.toLowerCase()) {
            case "updates":
                rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM files WHERE id=" + id);
                List<Update> data = new ArrayList<>();
                while (rs.next()) {
                    Update update = new Update();
                    update.setFilename(rs.getString("filename"));
                    update.setDescription(rs.getString("description"));
                    update.setVersions(rs.getString("versions"));
                    update.setFileId(rs.getInt("fileId"));
                    update.setDownload(PluginSiteApplication.config.domain + "/files/" + id + "/download/" + update.getFileId());
                    data.add(update);
                }
                Collections.reverse(data);
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

    @GetMapping("/resources/edit/{id}")
    public String editResource(@PathVariable("id") int id, Model model, @CookieValue(value = "id", defaultValue = "") String authorid, @CookieValue(value = "username", defaultValue = "") String username, @CookieValue(value = "id", defaultValue = "") String userId) throws SQLException {
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);

        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM resources WHERE id=" + id);
        if(!rs.next()) return "resource/404";

        if(Integer.parseInt(authorid) != rs.getInt("authorid")) return "resource/editDeny";

        Resource resource = new Resource();
        resource.setId(id);
        resource.setName(rs.getString("name"));
        resource.setDescription(rs.getString("description"));
        resource.setBlurb(rs.getString("blurb"));
        resource.setDonation(rs.getString("donation"));
        resource.setSource(rs.getString("source"));
        model.addAttribute("resource", resource);
        model.addAttribute("url", "/resources/edit/" + id);
        return "/resource/edit";
    }

    @PostMapping("/resources/edit/{id}")
    public RedirectView editSubmit(@ModelAttribute Resource resource, @PathVariable("id") int id, @CookieValue(value = "id", defaultValue = "") String userId) throws SQLException {

        if(resource.getName().length() < 1 || resource.getDescription().length() < 1 || resource.getBlurb().length() < 1) {
            // TODO: return an error message
            //return "";
        }

        PluginSiteApplication.getDB().getStmt().executeUpdate("UPDATE resources SET name='" + resource.getName() + "', blurb='" + resource.getBlurb() + "', description='" + resource.getDescription() + "', donation='" + resource.getDonation() + "', source='" + resource.getSource() + "'" +
                "WHERE id=" + id + ";");
        return new RedirectView("/resources/" + resource.getId());
    }

    @PostMapping("/resources/create")
    public RedirectView createSubmit(@ModelAttribute Resource resource, @CookieValue(value = "id", defaultValue = "") String authorid, @CookieValue(value = "id", defaultValue = "") String userId) throws SQLException {

        if(resource.getName().length() < 1 || resource.getDescription().length() < 1 || resource.getBlurb().length() < 1) {
            // TODO: return an error message
            //return "";
        }

        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT id FROM resources WHERE id=(SELECT MAX(id) FROM resources) GROUP BY id");

        int id;

        if(!rs.next()) id = 0;
        else id = rs.getInt("id");

        id++;

        resource.setId(id);

        long created = new Date().getTime() / 1000;
        PluginSiteApplication.getDB().getStmt().executeUpdate("INSERT INTO resources (id, name, blurb, description, download, donation, source, creation, updated, downloads, authorid) " +
                "VALUES(" + id + ", '" + resource.getName() + "', '" + resource.getBlurb() + "','" + resource.getDescription() + "', '', '" + resource.getDonation() + "', '" + resource.getSource() + "', " + created + ", " + created + ", 0, " + authorid + ")");
        return new RedirectView("/resources/" + resource.getId());
    }

    @GetMapping("/resources/create")
    public String create(Model model, @CookieValue(value = "username", defaultValue = "") String username, @CookieValue(value = "id", defaultValue = "") String userId) {
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        model.addAttribute("resource", new Resource());
        return "resource/create";
    }

    public String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    @GetMapping("/resources/upload/{id}")
    public String uploadFile(@RequestParam(name = "error", required = false) String error, @CookieValue(value = "id", defaultValue = "") String userId, @PathVariable("id") int id, Model model, @CookieValue(value = "id", defaultValue = "") String authorid, @CookieValue(value = "username", defaultValue = "") String username) throws SQLException {

        model.addAttribute("error", error);
        model.addAttribute("config", PluginSiteApplication.config);
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM resources WHERE id=" + id);
        if(!rs.next()) return "resource/404";

        if(Integer.parseInt(authorid) != rs.getInt("authorid")) return "resource/editDeny";

        ResourceFile file = new ResourceFile();
        file.setId(id);
        model.addAttribute("resourceFile", file);
        model.addAttribute("url", "/resources/upload/" + id);
        return "resource/upload";
    }

    @PostMapping("/resources/upload/{id}")
    public String uploadFilePost(@RequestParam("file") MultipartFile file, @ModelAttribute ResourceFile resourceFile, @CookieValue(value = "id", defaultValue = "") String authorid) throws IOException, SQLException {

        if(file.isEmpty()) {
            return "redirect:/resources/upload/" + resourceFile.getId() + "/?error=filesize";
        }
        System.out.println(file.getSize());

        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT fileId FROM files WHERE fileId=(SELECT MAX(fileId) FROM files) GROUP BY fileId");


        int fileId;

        if(!rs.next()) fileId = 0;
        else fileId = rs.getInt("fileId");

        fileId++;

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

        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        array.add("1.17.1");
        json.add("versions", array);

        long created = new Date().getTime() / 1000;
        PluginSiteApplication.getDB().getStmt().executeUpdate("INSERT INTO files (id, fileId, filename, description, versions, uploaded)" +
                "VALUES(" + resourceFile.getId() + ", " + fileId + ", '" + file.getOriginalFilename() + "', '" + resourceFile.getDescription() + "', '" + json + "', " + created + ")");

        PluginSiteApplication.getDB().getStmt().executeUpdate("UPDATE resources SET download='" + PluginSiteApplication.config.domain + "/files/" + resourceFile.getId() + "/download/" + fileId + "', updated='" + created + "' WHERE id='" + resourceFile.getId() + "'");

        return "redirect:/resources/" + resourceFile.getId();
    }
}