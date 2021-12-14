package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.dtos.out.SimpleResourceDto;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.repositories.AccountRepository;
import com.imjustdoom.pluginsite.repositories.ResourceRepository;
import com.imjustdoom.pluginsite.repositories.UpdateRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class ProfileController {

    private final ResourceRepository resourceRepository;
    private final AccountRepository accountRepository;
    private final UpdateRepository updateRepository;

    @GetMapping("/profile/{id}")
    public String profile(@RequestParam(name = "sort", required = false, defaultValue = "updated") String sort, @RequestParam(name = "page", required = false, defaultValue = "1") String page, @RequestParam(name = "field", required = false, defaultValue = "") String field, @PathVariable("id") int id, Model model, Account user) {
        model.addAttribute("account", user);
        model.addAttribute("page", Integer.parseInt(page));

        Optional<Account> optionalAccount = accountRepository.findById(id);

        if (optionalAccount.isEmpty()) return "error/404";

        Account account = accountRepository.getById(id);

        int totalDownloads = 0;
        for (Resource resource : account.getResources()) {
            totalDownloads += updateRepository.getTotalDownloads(resource.getId()) == null ? 0 : updateRepository.getTotalDownloads(resource.getId());
        }
        model.addAttribute("totalDownloads", totalDownloads);

        switch (field.toLowerCase()) {
            case "resources":
                if (Integer.parseInt(page) < 1) return "redirect:/profile/1?page=1";

                Sort sort1 = Sort.by(sort).ascending();
                Pageable pageable = PageRequest.of(Integer.parseInt(page) - 1, 25, sort1);
                List<Resource> resources = resourceRepository.findAllByAuthorId(id, pageable);

                List<SimpleResourceDto> data = new ArrayList<>();
                int total = resources.size() / 25;
                int remainder = resources.size() % 25;
                if (remainder > 1) total++;

                for (Resource resource : resources) {
                    Integer downloads = updateRepository.getTotalDownloads(resource.getId());
                    data.add(SimpleResourceDto.create(resource, downloads == null ? 0 : downloads));
                }

                model.addAttribute("total", total);
                model.addAttribute("files", data);
                model.addAttribute("account", account);
                return "profile/resources";
            default:
                model.addAttribute("account", account);
                return "profile/profile";
        }
    }
}