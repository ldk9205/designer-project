// src/api/boardApi.ts

import api from "./axios";

/**
 * ===============================
 * 타입 정의
 * ===============================
 */

export interface Post {
  id: number;
  designerId: number;
  designerName?: string;
  title: string;
  content: string;
  createdAt: string;
  updatedAt?: string;
}

export interface Comment {
  id: number;
  postId: number;
  designerId: number;
  designerName?: string;
  content: string;
  createdAt: string;
  updatedAt?: string;
}

/**
 * ===============================
 * 페이징 응답 타입
 * ===============================
 */

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
}

/**
 * ===============================
 * 게시글 목록 조회 (페이징)
 * GET /board/posts?page=&size=
 * ===============================
 */

export const getPosts = async ({
  page,
  size,
}: {
  page: number;
  size: number;
}): Promise<PageResponse<Post>> => {
  const res = await api.get<PageResponse<Post>>("/board/posts", {
    params: {
      page,
      size,
    },
  });

  return res.data;
};

/**
 * ===============================
 * 게시글 상세 조회
 * GET /board/posts/{id}
 * ===============================
 */

export const getPost = async (id: number): Promise<Post> => {
  const res = await api.get<Post>(`/board/posts/${id}`);
  return res.data;
};

/**
 * ===============================
 * 게시글 작성
 * POST /board/posts
 * ===============================
 */

export const createPost = async ({
  title,
  content,
}: {
  title: string;
  content: string;
}): Promise<void> => {
  await api.post("/board/posts", {
    title,
    content,
  });
};

/**
 * ===============================
 * 게시글 수정
 * PUT /board/posts/{id}
 * ===============================
 */

export const updatePost = async ({
  id,
  title,
  content,
}: {
  id: number;
  title: string;
  content: string;
}): Promise<void> => {
  await api.put(`/board/posts/${id}`, {
    title,
    content,
  });
};

/**
 * ===============================
 * 게시글 삭제
 * DELETE /board/posts/{id}
 * ===============================
 */

export const deletePost = async (id: number): Promise<void> => {
  await api.delete(`/board/posts/${id}`);
};

/**
 * ===============================
 * 댓글 목록 조회
 * GET /board/posts/{postId}/comments
 * ===============================
 */

export const getComments = async (postId: number): Promise<Comment[]> => {
  const res = await api.get<Comment[]>(`/board/posts/${postId}/comments`);
  return res.data;
};

/**
 * ===============================
 * 댓글 작성
 * POST /board/posts/{postId}/comments
 * ===============================
 */

export const createComment = async ({
  postId,
  content,
}: {
  postId: number;
  content: string;
}): Promise<void> => {
  await api.post(`/board/posts/${postId}/comments`, {
    content,
  });
};

/**
 * ===============================
 * 댓글 수정
 * PUT /board/comments/{commentId}
 * ===============================
 */

export const updateComment = async ({
  id,
  content,
}: {
  id: number;
  content: string;
}): Promise<void> => {
  await api.put(`/board/comments/${id}`, {
    content,
  });
};

/**
 * ===============================
 * 댓글 삭제
 * DELETE /board/comments/{commentId}
 * ===============================
 */

export const deleteComment = async (id: number): Promise<void> => {
  await api.delete(`/board/comments/${id}`);
};
