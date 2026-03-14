package com.designer.board.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Long id;

    private Long postId;

    private Long designerId;

    private String designerName;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}