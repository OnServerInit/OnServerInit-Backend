package com.imjustdoom.pluginsite.service;

import com.imjustdoom.pluginsite.config.custom.SiteConfig;
import com.imjustdoom.pluginsite.config.exception.RestErrorCode;
import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.dtos.in.CreateUpdateRequest;
import com.imjustdoom.pluginsite.dtos.in.resource.EditResourceUpdateRequest;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.model.Update;
import com.imjustdoom.pluginsite.repositories.ResourceRepository;
import com.imjustdoom.pluginsite.repositories.UpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceUpdateService {
    private static final Path BASE_PATH = Path.of("./resources/plugins/");

    private final UpdateRepository updateRepository;
    private final ResourceRepository resourceRepository;
    private final SiteConfig siteConfig;

    @PostConstruct
    public void setup() {
        if (Files.notExists(BASE_PATH))
            BASE_PATH.toFile().mkdirs();
    }

    public Update changeStatus(Account account, int updateId, String status) throws RestException {
        Update update = this.updateRepository.findById(updateId).orElseThrow(() -> new RestException(RestErrorCode.RESOURCE_UPDATE_NOT_FOUND));
        if (update.getResource().getAuthor().getId() != account.getId()) throw new RestException(RestErrorCode.FORBIDDEN);
        update.setStatus(status);
        return this.updateRepository.save(update);
    }

    public Update editUpdate(Account account, int updateId, EditResourceUpdateRequest request) throws RestException {
        Update update = this.updateRepository.findById(updateId).orElseThrow(() -> new RestException(RestErrorCode.RESOURCE_UPDATE_NOT_FOUND));
        if (update.getResource().getAuthor().getId() != account.getId()) throw new RestException(RestErrorCode.FORBIDDEN);

        String name = request.getName();
        if (name != null && !name.isEmpty())
            update.setName(name);

        String version = request.getVersion();
        if (version != null && !version.isEmpty())
            update.setVersion(version);

        String description = request.getDescription();
        if (description != null && !description.isEmpty())
            update.setDescription(description);

        return this.updateRepository.save(update);
    }

    public void createUpdate(Account account, List<String> softwareCheckbox, List<String> versionCheckbox, int resourceId,
                             MultipartFile file, CreateUpdateRequest request) throws RestException {

        if (this.updateRepository.getUpdatesCreateLastHour(account.getId()) > this.siteConfig.getMaxUpdatesPerHour()) throw new RestException(RestErrorCode.TOO_MANY_RESOURCE_UPDATES);

        if ((file.isEmpty() || file.getSize() > this.siteConfig.getMaxUploadSize().toBytes()) && (request.getExternalLink() == null || request.getExternalLink().isEmpty())) throw new RestException(RestErrorCode.REQUIRED_ARGUMENTS_MISSING, "Missing File");
        if ((!file.getOriginalFilename().endsWith(".jar") && !file.getOriginalFilename().endsWith(".zip")) && request.getExternalLink().equals("")) throw new RestException(RestErrorCode.WRONG_FILE_TYPE);
        if (request.isMissingRequirements()) throw new RestException(RestErrorCode.REQUIRED_ARGUMENTS_MISSING, "Missing name, version or description.");

        Resource resource = this.resourceRepository.findById(resourceId).orElseThrow(() -> new RestException(RestErrorCode.RESOURCE_NOT_FOUND));
        Update update;
        if (file.isEmpty()) {
            update = new Update(request.getDescription(), null, request.getVersion(), request.getExternalLink(), request.getName(), versionCheckbox, softwareCheckbox, resource);
        } else {
            update = new Update(request.getDescription(), file.getOriginalFilename(), request.getVersion(), null, request.getName(), versionCheckbox, softwareCheckbox, resource);
            Path resourcePath = BASE_PATH.resolve(update.getId() + ".jar");
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, resourcePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.updateRepository.save(update);
    }

    public FileReturn getDownload(int updateId) throws RestException {
        Path path = BASE_PATH.resolve(updateId + ".jar");
        if (!Files.exists(path)) throw new RestException(RestErrorCode.DOWNLOAD_NOT_FOUND, "File not found");
        Update update = this.updateRepository.findById(updateId).orElseThrow(() -> new RestException(RestErrorCode.RESOURCE_UPDATE_NOT_FOUND));
        if (update.getDownloadLink() != null) throw new RestException(RestErrorCode.WRONG_FILE_TYPE, "File is provided via an external URL");

        return new FileReturn(path.toFile(), update.getFilename());
    }

    public record FileReturn(File file, String realName) {}
}
