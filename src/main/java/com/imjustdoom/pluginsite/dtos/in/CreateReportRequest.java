package com.imjustdoom.pluginsite.dtos.in;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CreateReportRequest {

    private String reportingObject;
    private String reportingId;
    private String report;
    private String reason;
}