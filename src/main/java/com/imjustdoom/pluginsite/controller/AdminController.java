package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Report;
import com.imjustdoom.pluginsite.repositories.AccountRepository;
import com.imjustdoom.pluginsite.repositories.ReportRepository;
import com.imjustdoom.pluginsite.util.UrlUtil;
import lombok.AllArgsConstructor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@AllArgsConstructor
public class AdminController {

    private final AccountRepository accountRepository;
    private final ReportRepository reportRepository;

    @GetMapping("/admin")
    public String admin(Model model, Account account) {
        model.addAttribute("account", account);
        return "admin/admin";
    }

    @GetMapping("/admin/reports")
    public String reports(Model model, Account account) {
        model.addAttribute("account", account);
        model.addAttribute("reports", reportRepository.findAll());
        return "admin/reports";
    }

    @GetMapping("/admin/report/{id}")
    public String report(Model model, Account account, @PathVariable("id") int id) {

        Report report = reportRepository.findById(id).get();
        String description = UrlUtil.encode(report.getReport());

        description.replaceAll("script", "error style=\"display:none;\"");
        Parser parser = Parser.builder().build();
        Node document = parser.parse(description);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);

        report.setReport(html);

        model.addAttribute("account", account);
        model.addAttribute("report", report);
        return "admin/report";
    }

    @PostMapping("/admin/report/{id}")
    public String report(@PathVariable("id") int id, @RequestParam String action) {
        reportRepository.updateActionTakenById(id, action);
        return "redirect:/admin/reports";
    }

    @GetMapping("/admin/roles")
    public String roles(Model model, Account account) {
        model.addAttribute("account", account);
        return "admin/account/roles";
    }

    @PostMapping("/admin/roles")
    public void setRole(@RequestParam String username, @RequestParam String role, Account account, Model model) {
        model.addAttribute("account", account);

        Optional<Account> optionalAccount = accountRepository.findByUsernameEqualsIgnoreCase(username);
        if (optionalAccount.isEmpty()) return;
        role = role.toUpperCase();
        optionalAccount.get().setRole("ROLE_" + role);
        accountRepository.setRoleById(optionalAccount.get().getId(), "ROLE_" + role);
    }
}