package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.PluginSiteApplication;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.model.Update;
import com.imjustdoom.pluginsite.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

@Controller
public class ProfileController {

    @GetMapping("/profile/{id}")
    public String profile(@RequestParam(name = "sort", required = false, defaultValue = "updated") String sort, @RequestParam(name = "page", required = false, defaultValue = "1") String page, @RequestParam(name = "field", required = false, defaultValue = "") String field, @PathVariable("id") int id, Model model, TimeZone timezone, @CookieValue(value = "username", defaultValue = "") String username, @CookieValue(value = "id", defaultValue = "") String userId) throws SQLException {
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        model.addAttribute("page", Integer.parseInt(page));
        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM accounts WHERE id=%s".formatted(id));
        if(!rs.next()) return "resource/404";

        Account account = new Account();
        account.setJoined(DateUtil.formatDate(rs.getInt("joined"), timezone));
        account.setId(id);
        account.setUsername(rs.getString("username"));

        System.out.println(1);

        switch (field.toLowerCase()) {
            case "resources":
                System.out.println(2);
                if (Integer.parseInt(page) < 1) return "redirect:/profile/1?page=1";
                account.setTotalDownloads(0);

                System.out.println(3);

                List<Resource> data = new ArrayList<>();
                try {
                    rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT COUNT(id) FROM resources WHERE authorid=%s".formatted(id));

                    rs.next();
                    int resources = rs.getInt(1);

                    System.out.println(4);

                    int startRow = Integer.parseInt(page) * 25 - 25;
                    int endRow = startRow + 25;
                    int total = resources / 25;
                    int remainder = resources % 25;
                    if (remainder > 1) total++;

                    System.out.println(5);

                    model.addAttribute("total", total);

                    String orderBy = switch (sort) {
                        case "created" -> "ORDER BY creation DESC";
                        case "updated" -> "ORDER BY updated DESC";
                        case "downloads" -> "ORDER BY downloads DESC";
                        case "alphabetical" -> "ORDER BY name ASC";
                        default -> "";
                    };

                    System.out.println(6);
                    rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM resources WHERE authorid=%s %s LIMIT %s,25".formatted(id, orderBy, startRow));
                    while (rs.next()) {
                        System.out.println(7);
                        Resource resource = new Resource();
                        resource.setName(rs.getString("name"));
                        resource.setBlurb(rs.getString("blurb"));
                        resource.setId(rs.getInt("id"));
                        resource.setDownloads(rs.getInt("downloads"));
                        resource.setCreated(DateUtil.formatDate(rs.getInt("creation"), timezone));
                        resource.setUpdated(DateUtil.formatDate(rs.getInt("updated"), timezone));
                        account.setTotalDownloads(account.getTotalDownloads() + resource.getDownloads());
                        data.add(resource);
                    }
                } catch (SQLException e ) {
                    e.printStackTrace();
                }
                model.addAttribute("files", data);
                model.addAttribute("account", account);
                return "profile/resources";
            default:
                model.addAttribute("account", account);
                return "profile/profile";
        }
    }
}