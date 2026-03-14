package com.designer.board.service.comment;

import com.designer.board.dto.CommentDto;

import java.util.List;

public interface CommentService {

    void createComment(Long designerId, Long postId, CommentDto dto);

    List<CommentDto> getComments(Long postId);

    void updateComment(Long designerId, Long commentId, CommentDto dto);

    void deleteComment(Long designerId, Long commentId);

}