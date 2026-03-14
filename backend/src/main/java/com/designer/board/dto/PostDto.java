package com.designer.board.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {

    private Long id;

    private Long designerId;

    private String designerName;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}