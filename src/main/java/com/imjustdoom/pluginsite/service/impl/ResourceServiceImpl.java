package com.imjustdoom.pluginsite.service.impl;

import com.imjustdoom.pluginsite.config.custom.SiteConfig;
import com.imjustdoom.pluginsite.dtos.in.CreateResourceRequest;
import com.imjustdoom.pluginsite.dtos.out.SimpleResourceDto;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.repositories.ResourceRepository;
import com.imjustdoom.pluginsite.repositories.UpdateRepository;
import com.imjustdoom.pluginsite.service.ResourceService;
import lombok.AllArgsConstructor;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final UpdateRepository updateRepository;
    private final ResourceRepository resourceRepository;

    @Override
    public String postCreateResource(CreateResourceRequest resourceRequest, Account account, SiteConfig siteConfig,
                                     RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("resourceRequest", resourceRequest);
        if (resourceRepository.getResourcesCreateLastHour(account.getId()) > siteConfig.getMaxCreationsPerHour())
            return "redirect:/resources/create?error=createlimit";

        if (resourceRepository.existsByNameEqualsIgnoreCase(resourceRequest.getName()))
            return "redirect:/resources/create?error=nametaken";

        if (resourceRequest.getName().equalsIgnoreCase("")
                || resourceRequest.getBlurb().equalsIgnoreCase("")
                || resourceRequest.getDescription().equalsIgnoreCase(""))
            return "redirect:/resources/create?error=invalidinput";

        Resource resource = new Resource(resourceRequest.getName(), resourceRequest.getDescription(),
                resourceRequest.getBlurb(), resourceRequest.getDonation(), resourceRequest.getSource(),
                "", account, resourceRequest.getSupport(), resourceRequest.getCategory());

        resourceRepository.save(resource);

        return "redirect:/resources/%s".formatted(resource.getId());
    }

    @Override
    public List<SimpleResourceDto> searchResources(String search, String sortBy, String page) {
        List<BoundExtractedResult<Resource>> searchResults;
        List<SimpleResourceDto> data = new ArrayList<>();

        Sort sort = Sort.by(sortBy).descending();
        if(sortBy.equalsIgnoreCase("name")) sort = sort.ascending();
        Pageable pageable = PageRequest.of(Integer.parseInt(page) - 1, 25, sort);

        searchResults = FuzzySearch.extractSorted(search, resourceRepository.findAllByStatusEqualsIgnoreCase("public", pageable), Resource::getName);

        for (BoundExtractedResult<Resource> extractedResult : searchResults) {
            if (extractedResult.getScore() < 40) continue;

            //TODO: use getReferent
            Optional<Resource> optionalResource = resourceRepository.findByNameEqualsIgnoreCase(extractedResult.getString());
            Resource resource = optionalResource.get();

            Integer downloads = updateRepository.getTotalDownloads(resource.getId());
            data.add(SimpleResourceDto.create(resource, downloads == null ? 0 : downloads));

        }

        return data;
    }

    @Override
    public List<SimpleResourceDto> getResourcesWithCategory(String sortBy, String page, String category) {
        List<SimpleResourceDto> data = new ArrayList<>();
        Sort sort = Sort.by(sortBy).descending();
        if(sortBy.equalsIgnoreCase("name")) sort = sort.ascending();
        Pageable pageable = PageRequest.of(Integer.parseInt(page) - 1, 25, sort);

        for (Resource resource : resourceRepository.findAllByCategoryEqualsAndStatusEquals(category, "public", pageable)) {
            Integer downloads = updateRepository.getTotalDownloads(resource.getId());
            data.add(SimpleResourceDto.create(resource, downloads == null ? 0 : downloads));
        }

        return data;
    }

    @Override
    public List<SimpleResourceDto> getResources(String sortBy, String page) {
        List<SimpleResourceDto> data = new ArrayList<>();
        Sort sort = Sort.by(sortBy).descending();
        if(sortBy.equalsIgnoreCase("name")) sort = sort.ascending();
        Pageable pageable = PageRequest.of(Integer.parseInt(page) - 1, 25, sort);

        for (Resource resource : resourceRepository.findAllByStatusEqualsIgnoreCase("public", pageable)) {
            Integer downloads = updateRepository.getTotalDownloads(resource.getId());
            data.add(SimpleResourceDto.create(resource, downloads == null ? 0 : downloads));
        }

        return data;
    }
}
