package com.imjustdoom.pluginsite.service.impl;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final UpdateRepository updateRepository;
    private final ResourceRepository resourceRepository;

    @Override
    public Resource createResource(CreateResourceRequest resourceRequest, Account account) {
        Resource resource = new Resource(resourceRequest.getName(), resourceRequest.getDescription(),
                resourceRequest.getBlurb(), resourceRequest.getDonationLink(), resourceRequest.getSourceCodeLink(),
                "", account, resourceRequest.getSupportLink(), resourceRequest.getCategory());

        resourceRepository.save(resource);

        return resource;
    }

    @Override
    public List<SimpleResourceDto> searchResources(String search, String page) {
        List<BoundExtractedResult<Resource>> searchResults;
        List<SimpleResourceDto> data = new ArrayList<>();
        searchResults = FuzzySearch.extractSorted(search, resourceRepository.findAll(), Resource::getName);

        for (BoundExtractedResult<Resource> extractedResult : searchResults) {
            if (extractedResult.getScore() < 40) continue;

            Optional<Resource> optionalResource = resourceRepository.findByNameEqualsIgnoreCase(extractedResult.getString());
            Resource resource = optionalResource.get();

            Integer downloads = updateRepository.getTotalDownloads(resource.getId());
            data.add(SimpleResourceDto.create(resource, downloads == null ? 0 : downloads));

        }

        return data;
    }

    @Override
    public List<SimpleResourceDto> getResources(String sort, String page) {
        List<SimpleResourceDto> data = new ArrayList<>();
        Sort sort1 = Sort.by(sort).descending();
        if(sort.equalsIgnoreCase("name")) sort1 = sort1.ascending();
        Pageable pageable = PageRequest.of(Integer.parseInt(page) - 1, 25, sort1);

        for (Resource resource : resourceRepository.findAll(pageable)) {
            Integer downloads = updateRepository.getTotalDownloads(resource.getId());
            data.add(SimpleResourceDto.create(resource, downloads == null ? 0 : downloads));
        }

        return data;
    }
}
