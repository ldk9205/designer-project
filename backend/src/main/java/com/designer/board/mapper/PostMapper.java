package com.designer.board.mapper;

import com.designer.board.dto.PostDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    /**
     * 게시글 생성
     */
    void insert(PostDto dto);

    /**
     * 게시글 목록 조회 (페이징)
     */
    List<PostDto> findPosts(
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * 전체 게시글 개수
     */
    long countPosts();

    /**
     * 게시글 단일 조회
     */
    PostDto findById(Long id);

    /**
     * 게시글 수정
     */
    void update(PostDto dto);

    /**
     * 게시글 삭제
     */
    void delete(Long id);
}