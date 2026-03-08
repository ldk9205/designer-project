package com.designer.board.controller;

import com.designer.board.dto.CommentDto;
import com.designer.board.dto.PostDto;
import com.designer.board.service.BoardService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/posts")
    public void createPost(
            @RequestAttribute Long designerId,
            @RequestBody PostDto dto
    ) {
        boardService.createPost(designerId, dto);
    }

    @GetMapping("/posts")
    public List<PostDto> getPosts() {
        return boardService.getPosts();
    }

    @GetMapping("/posts/{id}")
    public PostDto getPost(@PathVariable Long id) {
        return boardService.getPost(id);
    }

    @PutMapping("/posts/{id}")
    public void updatePost(
            @RequestAttribute Long designerId,
            @PathVariable Long id,
            @RequestBody PostDto dto
    ) {
        boardService.updatePost(designerId, id, dto);
    }

    @DeleteMapping("/posts/{id}")
    public void deletePost(
            @RequestAttribute Long designerId,
            @PathVariable Long id
    ) {
        boardService.deletePost(designerId, id);
    }

    @PostMapping("/posts/{postId}/comments")
    public void createComment(
            @RequestAttribute Long designerId,
            @PathVariable Long postId,
            @RequestBody CommentDto dto
    ) {
        boardService.createComment(designerId, postId, dto);
    }

    @GetMapping("/posts/{postId}/comments")
    public List<CommentDto> getComments(@PathVariable Long postId) {
        return boardService.getComments(postId);
    }

    @PutMapping("/comments/{commentId}")
    public void updateComment(
            @RequestAttribute Long designerId,
            @PathVariable Long commentId,
            @RequestBody CommentDto dto
    ) {
        boardService.updateComment(designerId, commentId, dto);
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(
            @RequestAttribute Long designerId,
            @PathVariable Long commentId
    ) {
        boardService.deleteComment(designerId, commentId);
    }
}