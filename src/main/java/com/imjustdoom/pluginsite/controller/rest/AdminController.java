package com.imjustdoom.pluginsite.controller.rest;

import com.imjustdoom.pluginsite.config.exception.RestErrorCode;
import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.model.Report;
import com.imjustdoom.pluginsite.service.rest.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/report")
    public Page<Report> listReports(Pageable pageable) throws RestException {
        if (pageable.getPageSize() > 50) throw new RestException(RestErrorCode.PAGE_SIZE_TOO_LARGE, "Page size too large (%s > %s)", pageable.getPageSize(), 50);
        return this.adminService.getReportPage(pageable);
    }

    @GetMapping("/report/{id}")
    public Report getReport(@PathVariable int id) throws RestException {
        return this.adminService.getReport(id);
    }

    @PatchMapping("/report/{id}")
    public Report updateActionTaken(@PathVariable int id, @RequestParam String actionTaken) {
        return this.adminService.updateActionTaken(id, actionTaken);
    }

    // todo didnt add roles since this system should be reworked. The code below is the old code.
//    @PostMapping("/admin/roles")
//    public void setRole(@RequestParam String username, @RequestParam String role, Account account, Model model) {
//        model.addAttribute("account", account);
//
//        Optional<Account> optionalAccount = accountRepository.findByUsernameEqualsIgnoreCase(username);
//        if (optionalAccount.isEmpty()) return;
//        role = role.toUpperCase();
//        optionalAccount.get().setRole("ROLE_" + role);
//        accountRepository.setRoleById(optionalAccount.get().getId(), "ROLE_" + role);
//    }
}
