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
    public String profile(@RequestParam(name = "field", required = false, defaultValue = "") String field, @PathVariable("id") int id, Model model, TimeZone timezone, @CookieValue(value = "username", defaultValue = "") String username, @CookieValue(value = "id", defaultValue = "") String userId) throws SQLException {
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM accounts WHERE id=" + id);
        if(!rs.next()) return "resource/404";

        Account account = new Account();
        account.setId(id);
        account.setUsername(rs.getString("username"));

        switch (field.toLowerCase()) {
            case "resources":
                account.setTotalDownloads(0);

                List<Resource> data = new ArrayList<>();
                try {
                    rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM resources WHERE authorid=" + id);
                    while (rs.next()) {
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
                model.addAttribute("resources", data);
                model.addAttribute("account", account);
                return "profile/resources";
            default:
                model.addAttribute("account", account);
                return "profile/profile";
        }
    }
}