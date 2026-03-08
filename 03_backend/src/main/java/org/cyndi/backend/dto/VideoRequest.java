package org.cyndi.backend.dto;

import lombok.Data;

@Data
public class VideoRequest {
    private String title;
    private String description;
    private String url;
    private String thumbnail;
    private Long heroId;
    private Long mapId;
    private String gameType;
}
