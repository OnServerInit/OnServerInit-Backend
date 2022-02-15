package com.imjustdoom.pluginsite.dtos.in;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CreateUpdateRequest {

    private String name;
    private String version;
    private String externalLink;
    private String description;
    private List<String> versions;
    private List<String> software;

    public boolean isMissingRequirements() {
        return this.name == null || this.name.isEmpty()
            || this.version == null || this.version.isEmpty()
            || this.externalLink == null || this.externalLink.isEmpty()
            || this.description == null || this.description.isEmpty();
    }
}