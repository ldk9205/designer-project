package com.designer.board.mapper;

import com.designer.board.dto.CommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 댓글 생성
     */
    void insert(CommentDto dto);

    /**
     * 게시글의 댓글 목록 조회
     */
    List<CommentDto> findByPostId(@Param("postId") Long postId);

    /**
     * 댓글 단일 조회
     */
    CommentDto findById(Long id);

    /**
     * 댓글 수정
     */
    void update(CommentDto dto);

    /**
     * 댓글 삭제
     */
    void delete(Long id);
}