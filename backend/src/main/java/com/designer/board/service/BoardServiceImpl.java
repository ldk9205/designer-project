package com.designer.board.service;

import com.designer.board.dto.CommentDto;
import com.designer.board.dto.PageResponseDto;
import com.designer.board.dto.PostDto;
import com.designer.board.mapper.CommentMapper;
import com.designer.board.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    @Override
    public void createPost(Long designerId, PostDto dto) {
        dto.setDesignerId(designerId);
        postMapper.insert(dto);
    }

    @Override
    public PageResponseDto<PostDto> getPosts(int page, int size) {

        int offset = page * size;

        List<PostDto> posts = boardMapper.findPosts(offset, size);

        long total = boardMapper.countPosts();

        PageResponseDto<PostDto> response = new PageResponseDto<>();

        response.setContent(posts);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(total);
        response.setTotalPages((int) Math.ceil((double) total / size));

        return response;
    }

    @Override
    public PostDto getPost(Long id) {
        return postMapper.findById(id);
    }

    @Override
    public void updatePost(Long designerId, Long id, PostDto dto) {

        PostDto post = postMapper.findById(id);

        if (!post.getDesignerId().equals(designerId)) {
            throw new RuntimeException("작성자만 수정 가능");
        }

        dto.setId(id);
        postMapper.update(dto);
    }

    @Override
    public void deletePost(Long designerId, Long id) {

        PostDto post = postMapper.findById(id);

        if (!post.getDesignerId().equals(designerId)) {
            throw new RuntimeException("작성자만 삭제 가능");
        }

        postMapper.delete(id);
    }

    @Override
    public void createComment(Long designerId, Long postId, CommentDto dto) {

        dto.setDesignerId(designerId);
        dto.setPostId(postId);

        commentMapper.insert(dto);
    }

    @Override
    public List<CommentDto> getComments(Long postId) {
        return commentMapper.findByPostId(postId);
    }

    @Override
    public void updateComment(Long designerId, Long commentId, CommentDto dto) {

        CommentDto comment = commentMapper.findById(commentId);

        if (!comment.getDesignerId().equals(designerId)) {
            throw new RuntimeException("작성자만 수정 가능");
        }

        dto.setId(commentId);
        commentMapper.update(dto);
    }

    @Override
    public void deleteComment(Long designerId, Long commentId) {

        CommentDto comment = commentMapper.findById(commentId);

        if (!comment.getDesignerId().equals(designerId)) {
            throw new RuntimeException("작성자만 삭제 가능");
        }

        commentMapper.delete(commentId);
    }
}