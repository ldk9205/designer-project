package com.designer.board.service.post;

import com.designer.board.dto.PageResponseDto;
import com.designer.board.dto.PostDto;

public interface PostService {

    void createPost(Long designerId, PostDto dto);

    PageResponseDto<PostDto> getPosts(int page, int size);

    PostDto getPost(Long id);

    void updatePost(Long designerId, Long id, PostDto dto);

    void deletePost(Long designerId, Long id);

}