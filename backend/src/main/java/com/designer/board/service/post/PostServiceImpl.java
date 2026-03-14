package com.designer.board.service.post;

import com.designer.board.dto.PageResponseDto;
import com.designer.board.dto.PostDto;
import com.designer.board.mapper.PostMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

    /**
     * 게시글 작성
     */
    @Override
    public void createPost(Long designerId, PostDto dto) {

        dto.setDesignerId(designerId);

        postMapper.insert(dto);
    }

    /**
     * 게시글 목록 조회 (페이징)
     */
    @Override
    public PageResponseDto<PostDto> getPosts(int page, int size) {

        int offset = page * size;

        List<PostDto> posts = postMapper.findPosts(offset, size);

        long total = postMapper.countPosts();

        PageResponseDto<PostDto> response = new PageResponseDto<>();

        response.setContent(posts);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(total);
        response.setTotalPages((int) Math.ceil((double) total / size));

        return response;
    }

    /**
     * 게시글 조회
     */
    @Override
    public PostDto getPost(Long id) {

        return postMapper.findById(id);
    }

    /**
     * 게시글 수정
     */
    @Override
    public void updatePost(Long designerId, Long id, PostDto dto) {

        PostDto post = postMapper.findById(id);

        if (!post.getDesignerId().equals(designerId)) {
            throw new RuntimeException("작성자만 수정할 수 있습니다.");
        }

        dto.setId(id);

        postMapper.update(dto);
    }

    /**
     * 게시글 삭제
     */
    @Override
    public void deletePost(Long designerId, Long id) {

        PostDto post = postMapper.findById(id);

        if (!post.getDesignerId().equals(designerId)) {
            throw new RuntimeException("작성자만 삭제할 수 있습니다.");
        }

        postMapper.delete(id);
    }
}