package com.designer.board.mapper;

import com.designer.board.dto.CommentDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    void insert(CommentDto dto);

    List<CommentDto> findByPostId(Long postId);

    void update(CommentDto dto);

    void delete(Long commentId);

    CommentDto findById(Long id);
}