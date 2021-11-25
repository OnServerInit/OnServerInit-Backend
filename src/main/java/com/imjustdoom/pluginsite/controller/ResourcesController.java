package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.PluginSiteApplication;
import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

@Controller
public class ResourcesController {

    @GetMapping("/resources")
    public String resources(Model model, TimeZone timezone) {
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

        return "resources/resources";
    }

    @GetMapping("/resources/{id}")
    public String resources(@PathVariable("id") int id, Model model) throws SQLException {
        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM resources WHERE id=" + id);
        if(!rs.next()) return "resources/404";

        Resource resource = new Resource();
        resource.setId(id);
        resource.setName(rs.getString("name"));
        resource.setDescription(markdownToHtml(rs.getString("description")));
        resource.setBlurb(rs.getString("blurb"));
        resource.setDonation(rs.getString("donation"));
        resource.setSource(rs.getString("source"));
        model.addAttribute("resource", resource);
        model.addAttribute("url", "/resources/edit/" + id);
        return "/resources/resource";
    }

    @GetMapping("/resources/edit/{id}")
    public String editResource(@PathVariable("id") int id, Model model) throws SQLException {
        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM resources WHERE id=" + id);
        if(!rs.next()) return "resources/404";

        Resource resource = new Resource();
        resource.setId(id);
        resource.setName(rs.getString("name"));
        resource.setDescription(rs.getString("description"));
        resource.setBlurb(rs.getString("blurb"));
        resource.setDonation(rs.getString("donation"));
        resource.setSource(rs.getString("source"));
        model.addAttribute("resource", resource);
        model.addAttribute("url", "/resources/edit/" + id);
        return "/resources/edit";
    }

    @PostMapping("/resources/edit/{id}")
    public RedirectView editSubmit(@ModelAttribute Resource resource, @PathVariable("id") int id) throws SQLException {

        if(resource.getName().length() < 1 || resource.getDescription().length() < 1 || resource.getBlurb().length() < 1) {
            // TODO: return an error message
            //return "";
        }

        PluginSiteApplication.getDB().getStmt().executeUpdate("UPDATE resources SET name='" + resource.getName() + "', blurb='" + resource.getBlurb() + "', description='" + resource.getDescription() + "', donation='" + resource.getDonation() + "', source='" + resource.getSource() + "'" +
                "WHERE id=" + id + ";");
        return new RedirectView("/resources/" + resource.getId());
    }

    @PostMapping("/resources/create")
    public RedirectView createSubmit(@ModelAttribute Resource resource) throws SQLException {

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
        PluginSiteApplication.getDB().getStmt().executeUpdate("INSERT INTO resources (id, name, blurb, description, donation, source, creation, updated, downloads, authorid) " +
                "VALUES(" + id + ", '" + resource.getName() + "', '" + resource.getBlurb() + "','" + resource.getDescription() + "', '" + resource.getDonation() + "', '" + resource.getSource() + "', " + created + ", " + created + ", 0, 0)");
        return new RedirectView("/resources/" + resource.getId());
    }

    @GetMapping("/resources/create")
    public String create(Model model) {
        model.addAttribute("resource", new Resource());
        return "resources/create";
    }

    public String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}