package com.imjustdoom.pluginsite.dtos.in;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CreateUpdateRequest {

    private String name;
    private String version;
    private String externalLink;
    private String description;

    public boolean isMissingRequirements() {
        return this.name == null || this.name.isEmpty()
            || this.version == null || this.version.isEmpty()
            || this.externalLink == null || this.externalLink.isEmpty()
            || this.description == null || this.description.isEmpty();
    }
}