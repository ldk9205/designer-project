package com.designer.board.controller;

import com.designer.board.dto.CommentDto;
import com.designer.board.dto.PostDto;
import com.designer.board.dto.PageResponseDto;
import com.designer.board.service.BoardService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    /**
     * ===============================
     * 게시글 작성
     * POST /board/posts
     * ===============================
     */
    @PostMapping("/posts")
    public void createPost(
            @RequestAttribute Long designerId,
            @RequestBody PostDto dto
    ) {
        boardService.createPost(designerId, dto);
    }

    /**
     * ===============================
     * 게시글 목록 조회 (페이징)
     * GET /board/posts?page=0&size=5
     * ===============================
     */
    @GetMapping("/posts")
    public PageResponseDto<PostDto> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return boardService.getPosts(page, size);
    }

    /**
     * ===============================
     * 게시글 단일 조회
     * GET /board/posts/{id}
     * ===============================
     */
    @GetMapping("/posts/{id}")
    public PostDto getPost(@PathVariable Long id) {
        return boardService.getPost(id);
    }

    /**
     * ===============================
     * 게시글 수정 (작성자만 가능)
     * PUT /board/posts/{id}
     * ===============================
     */
    @PutMapping("/posts/{id}")
    public void updatePost(
            @RequestAttribute Long designerId,
            @PathVariable Long id,
            @RequestBody PostDto dto
    ) {
        boardService.updatePost(designerId, id, dto);
    }

    /**
     * ===============================
     * 게시글 삭제 (작성자만 가능)
     * DELETE /board/posts/{id}
     * ===============================
     */
    @DeleteMapping("/posts/{id}")
    public void deletePost(
            @RequestAttribute Long designerId,
            @PathVariable Long id
    ) {
        boardService.deletePost(designerId, id);
    }

    /**
     * ===============================
     * 댓글 작성
     * POST /board/posts/{postId}/comments
     * ===============================
     */
    @PostMapping("/posts/{postId}/comments")
    public void createComment(
            @RequestAttribute Long designerId,
            @PathVariable Long postId,
            @RequestBody CommentDto dto
    ) {
        boardService.createComment(designerId, postId, dto);
    }

    /**
     * ===============================
     * 댓글 목록 조회
     * GET /board/posts/{postId}/comments
     * ===============================
     */
    @GetMapping("/posts/{postId}/comments")
    public List<CommentDto> getComments(@PathVariable Long postId) {
        return boardService.getComments(postId);
    }

    /**
     * ===============================
     * 댓글 수정 (작성자만 가능)
     * PUT /board/comments/{commentId}
     * ===============================
     */
    @PutMapping("/comments/{commentId}")
    public void updateComment(
            @RequestAttribute Long designerId,
            @PathVariable Long commentId,
            @RequestBody CommentDto dto
    ) {
        boardService.updateComment(designerId, commentId, dto);
    }

    /**
     * ===============================
     * 댓글 삭제 (작성자만 가능)
     * DELETE /board/comments/{commentId}
     * ===============================
     */
    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(
            @RequestAttribute Long designerId,
            @PathVariable Long commentId
    ) {
        boardService.deleteComment(designerId, commentId);
    }
}