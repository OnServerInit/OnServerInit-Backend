package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.PluginSiteApplication;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.util.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class AccountController {

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("account", new Account());
        return "account/signup";
    }

    @GetMapping("/logout")
    public RedirectView logout(HttpServletResponse response, @CookieValue(value = "username", defaultValue = "") String username, Model model) {
        model.addAttribute("username", username);
        Cookie cookie = new Cookie("username", null);
        response.addCookie(cookie);
        cookie = new Cookie("id", null);
        response.addCookie(cookie);
        return new RedirectView("/");
    }

    @PostMapping("/signup")
    public RedirectView signupSubmit(@ModelAttribute Account account, HttpServletResponse response) throws SQLException {

        String emailAddress = account.getEmail();
        String regexPattern = "^(.+)@(\\S+)$";
        boolean validEmail = StringUtil.patternMatches(emailAddress, regexPattern);

        if (!validEmail) {
            System.out.println("invalid email");
            return new RedirectView("/");
        }

        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT username FROM accounts WHERE username='" + account.getUsername() + "'");

        if (rs.next()) {
            System.out.println("Already acc with email");
            return new RedirectView("");
        }

        rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT id FROM accounts WHERE id=(SELECT MAX(id) FROM accounts) GROUP BY id");
        int id;
        if (!rs.next()) id = 0;
        else id = rs.getInt("id");

        id++;

        account.setId(id);

        PluginSiteApplication.getDB().getStmt().executeUpdate("INSERT INTO accounts (id, username, email, password, provider)" +
                "VALUES(" + id + ", '" + account.getUsername() + "', '" + account.getEmail() + "', '" + account.getPassword() + "', 'LOCAL');");

        Cookie ck = new Cookie("username", account.getUsername());
        response.addCookie(ck);
        ck = new Cookie("id", String.valueOf(account.getId()));
        response.addCookie(ck);

        return new RedirectView("/profile/" + account.getId());
    }

    @GetMapping("/login")
    public String login(Model model, @CookieValue(value = "username", defaultValue = "") String username) {
        model.addAttribute("account", new Account());
        model.addAttribute("username", username);
        return "account/login";
    }

    @PostMapping("/login")
    public RedirectView loginSubmit(@ModelAttribute Account account, HttpServletResponse response) throws SQLException {

        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT id FROM accounts WHERE username='" + account.getUsername() + "' AND password='" + account.getPassword() + "'");

        if (!rs.next()) {
            System.out.println("No account found");
            return new RedirectView("");
        }

        Cookie ck = new Cookie("username", account.getUsername());
        response.addCookie(ck);
        ck = new Cookie("id", String.valueOf(rs.getInt("id")));
        response.addCookie(ck);

        return new RedirectView("/profile/" + rs.getInt("id"));
    }
}