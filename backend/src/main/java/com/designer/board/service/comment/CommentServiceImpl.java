package com.designer.board.service.comment;

import com.designer.board.dto.CommentDto;
import com.designer.board.mapper.CommentMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    /**
     * 댓글 작성
     */
    @Override
    public void createComment(Long designerId, Long postId, CommentDto dto) {

        dto.setDesignerId(designerId);
        dto.setPostId(postId);

        commentMapper.insert(dto);
    }

    /**
     * 댓글 조회
     */
    @Override
    public List<CommentDto> getComments(Long postId) {

        List<CommentDto> comments = commentMapper.findByPostId(postId);

        if (comments == null) {
            return Collections.emptyList();
        }

        return comments;
    }

    /**
     * 댓글 수정
     */
    @Override
    public void updateComment(Long designerId, Long commentId, CommentDto dto) {

        CommentDto comment = commentMapper.findById(commentId);

        if (!comment.getDesignerId().equals(designerId)) {
            throw new RuntimeException("작성자만 수정할 수 있습니다.");
        }

        dto.setId(commentId);

        commentMapper.update(dto);
    }

    /**
     * 댓글 삭제
     */
    @Override
    public void deleteComment(Long designerId, Long commentId) {

        CommentDto comment = commentMapper.findById(commentId);

        if (!comment.getDesignerId().equals(designerId)) {
            throw new RuntimeException("작성자만 삭제할 수 있습니다.");
        }

        commentMapper.delete(commentId);
    }
}