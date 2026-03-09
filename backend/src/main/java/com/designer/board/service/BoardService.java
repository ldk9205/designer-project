package com.designer.board.service;

import com.designer.board.dto.PageResponseDto;
import com.designer.board.dto.PostDto;
import com.designer.board.dto.CommentDto;

import java.util.List;

public interface BoardService {

    void createPost(Long designerId, PostDto dto);

    PageResponseDto<PostDto> getPosts(int page, int size);

    PostDto getPost(Long id);

    void updatePost(Long designerId, Long id, PostDto dto);

    void deletePost(Long designerId, Long id);

    void createComment(Long designerId, Long postId, CommentDto dto);

    List<CommentDto> getComments(Long postId);

    void updateComment(Long designerId, Long commentId, CommentDto dto);

    void deleteComment(Long designerId, Long commentId);
}