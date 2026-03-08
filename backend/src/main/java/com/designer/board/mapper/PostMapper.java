package com.designer.board.mapper;

import com.designer.board.dto.PostDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {

    void insert(PostDto dto);

    List<PostDto> findAll();

    PostDto findById(Long id);

    void update(PostDto dto);

    void delete(Long id);
}