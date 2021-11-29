package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.PluginSiteApplication;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Controller
public class ProfileController {

    @GetMapping("/profile/{id}")
    public String profile(@PathVariable("id") int id, Model model, TimeZone timezone, @CookieValue(value = "username", defaultValue = "") String username) throws SQLException {
        model.addAttribute("username", username);
        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM accounts WHERE id=" + id);
        if(!rs.next()) return "resource/404";

        Account account = new Account();
        account.setId(id);
        account.setUsername(rs.getString("username"));
        model.addAttribute("account", account);

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
                data.add(resource);
            }
        } catch (SQLException e ) {
            e.printStackTrace();
        }

        model.addAttribute("resources", data);

        return "profile/profile";
    }
}