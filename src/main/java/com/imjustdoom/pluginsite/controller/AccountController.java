package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.PluginSiteApplication;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class AccountController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/signup")
    public String signup(Model model, @CookieValue(value = "username", defaultValue = "") String username, @RequestParam(name = "error", required = false) String error) {
        model.addAttribute("account", new Account());
        model.addAttribute("username", username);
        model.addAttribute("error", error);
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
    public String signupSubmit(@ModelAttribute Account account, HttpServletResponse response) throws SQLException {

        String emailAddress = account.getEmail();
        String regexPattern = "^(.+)@(\\S+)$";
        boolean validEmail = StringUtil.patternMatches(emailAddress, regexPattern);

        if (!validEmail) return "redirect:/signup?error=invalidemail";

        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT username FROM accounts WHERE username='%s'".formatted(account.getUsername()));

        if (rs.next()) return "redirect:/signup?error=usernametaken";

        rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT email FROM accounts WHERE email='%s'".formatted(account.getEmail()));

        if (rs.next()) return "redirect:/signup?error=emailtaken";

        rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT id FROM accounts WHERE id=(SELECT MAX(id) FROM accounts) GROUP BY id");
        int id;
        if (!rs.next()) id = 0;
        else id = rs.getInt("id");

        id++;

        account.setId(id);

        PluginSiteApplication.getDB().getStmt().executeUpdate("INSERT INTO accounts (id, username, email, password, provider)" +
                "VALUES('%s', '%s', '%s', '%s', 'LOCAL');".formatted(id, account.getUsername(), account.getEmail(), passwordEncoder.encode(account.getPassword())));

        Cookie ck = new Cookie("username", account.getUsername());
        response.addCookie(ck);
        ck = new Cookie("id", String.valueOf(account.getId()));
        response.addCookie(ck);

        return "redirect:/profile/" + account.getId();
    }

    @GetMapping("/login")
    public String login(Model model, @CookieValue(value = "username", defaultValue = "") String username, @RequestParam(name = "error", required = false) String error) {
        model.addAttribute("error", error);
        model.addAttribute("account", new Account());
        model.addAttribute("username", username);
        return "account/login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute Account account, HttpServletResponse response) throws SQLException {

        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT id, password FROM accounts WHERE username='%s'".formatted(account.getUsername()));

        if (rs.next()) {
            if(BCrypt.checkpw(account.getPassword(), rs.getString("password"))) {
                Cookie ck = new Cookie("username", account.getUsername());
                response.addCookie(ck);
                ck = new Cookie("id", String.valueOf(rs.getInt("id")));
                response.addCookie(ck);

                return "redirect:/profile/" + rs.getInt("id");
            }
            return "redirect:/login?error=incorrectpassword";
        }

        return "redirect:/login?error=notfound";
    }
}