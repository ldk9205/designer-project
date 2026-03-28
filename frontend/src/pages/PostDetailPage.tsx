import React, { useContext, useEffect, useMemo, useState } from "react";
import {
  createComment,
  deleteComment,
  deletePost,
  getComments,
  getPost,
  updateComment,
  type Comment,
  type Post,
} from "../api/boardApi";
import { useNavigate, useParams } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import { formatRelativeTime } from "../utils/time";
import "../styles/PostDetailPage.css";

export default function PostDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const auth = useContext(AuthContext);

  const postId = Number(id);
  const myDesignerId = auth?.designer?.id ?? null;

  const [post, setPost] = useState<Post | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [content, setContent] = useState("");

  const [loading, setLoading] = useState(true);

  const [commentSaving, setCommentSaving] = useState(false);
  const [postDeleting, setPostDeleting] = useState(false);
  const [commentDeletingId, setCommentDeletingId] = useState<number | null>(null);

  const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
  const [editingContent, setEditingContent] = useState("");
  const [editingSaving, setEditingSaving] = useState(false);

  const canEditPost = useMemo(() => {
    if (!post || myDesignerId == null) return false;
    return post.designerId === myDesignerId;
  }, [post, myDesignerId]);

  const loadComments = async () => {
    const commentData = await getComments(postId);
    setComments(commentData);
  };

  const loadPostAndComments = async () => {
    try {
      setLoading(true);

      const postData = await getPost(postId);
      setPost(postData);

      const commentData = await getComments(postId);
      setComments(commentData);
    } catch (e) {
      console.error(e);
      alert("게시글 정보를 불러오지 못했습니다.");
      navigate("/community");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!id || Number.isNaN(postId)) return;
    void loadPostAndComments();
  }, [id, postId]);

  const handleComment = async () => {
    const text = content.trim();
    if (!text) return;

    try {
      setCommentSaving(true);

      await createComment({
        postId,
        content: text,
      });

      setContent("");
      await loadComments();
    } catch (e: any) {
      console.error(e);
      alert(e?.response?.data?.message || "댓글 작성에 실패했습니다.");
    } finally {
      setCommentSaving(false);
    }
  };

  const handleStartEditComment = (comment: Comment) => {
    setEditingCommentId(comment.id);
    setEditingContent(comment.content);
  };

  const handleCancelEditComment = () => {
    setEditingCommentId(null);
    setEditingContent("");
  };

  const handleUpdateComment = async () => {
    const text = editingContent.trim();
    if (!editingCommentId || !text) return;

    try {
      setEditingSaving(true);

      await updateComment({
        id: editingCommentId,
        content: text,
      });

      await loadComments();
      handleCancelEditComment();
    } catch (e: any) {
      console.error(e);
      alert(e?.response?.data?.message || "댓글 수정에 실패했습니다.");
    } finally {
      setEditingSaving(false);
    }
  };

  const handleDeletePost = async () => {
    if (!post) return;

    const ok = window.confirm("정말 이 게시글을 삭제하시겠습니까?");
    if (!ok) return;

    try {
      setPostDeleting(true);
      await deletePost(post.id);
      alert("게시글이 삭제되었습니다.");
      navigate("/community");
    } catch (e: any) {
      console.error(e);
      alert(e?.response?.data?.message || "게시글 삭제에 실패했습니다.");
    } finally {
      setPostDeleting(false);
    }
  };

  const handleDeleteComment = async (commentId: number) => {
    const ok = window.confirm("정말 이 댓글을 삭제하시겠습니까?");
    if (!ok) return;

    try {
      setCommentDeletingId(commentId);

      await deleteComment(commentId);

      if (editingCommentId === commentId) {
        handleCancelEditComment();
      }

      await loadComments();
    } catch (e: any) {
      console.error(e);
      alert(e?.response?.data?.message || "댓글 삭제에 실패했습니다.");
    } finally {
      setCommentDeletingId(null);
    }
  };

  if (loading || !auth?.isAuthReady) {
    return (
      <div className="post-detail-wrapper">
        <p>로딩 중...</p>
      </div>
    );
  }

  if (!post) {
    return (
      <div className="post-detail-wrapper">
        <p>게시글이 없습니다.</p>
      </div>
    );
  }

  return (
    <div className="post-detail-wrapper">
      <div className="post-detail-top-bar">
        <button
          type="button"
          className="post-detail-back-btn"
          onClick={() => navigate("/community")}
        >
          ← 이전으로
        </button>
      </div>
      
      <div className="post-card">
        <div className="post-title">{post.title}</div>

        <div className="post-meta">
          {post.designerName ?? "익명"} · {formatRelativeTime(post.createdAt)}
          {post.updatedAt && post.updatedAt !== post.createdAt ? " · 수정됨" : ""}
        </div>

        <div className="post-content">{post.content}</div>

        {canEditPost && (
          <div className="post-actions">
            <button
              type="button"
              onClick={() => navigate(`/community/edit/${post.id}`)}
              disabled={postDeleting}
            >
              게시글 수정
            </button>

            <button
              type="button"
              onClick={() => void handleDeletePost()}
              disabled={postDeleting}
            >
              {postDeleting ? "삭제 중..." : "게시글 삭제"}
            </button>
          </div>
        )}
      </div>

      <div className="comment-section">
        <div className="comment-title">댓글 {comments.length}개</div>

        <div className="comment-list">
          {comments.length === 0 && <p>아직 댓글이 없습니다.</p>}

          {comments.map((comment) => {
            const canEditComment =
              myDesignerId != null && comment.designerId === myDesignerId;

            const isEditing = editingCommentId === comment.id;
            const isDeleting = commentDeletingId === comment.id;

            return (
              <div key={comment.id} className="comment-item">
                <div className="comment-meta">
                  {comment.designerName ?? "익명"} ·{" "}
                  {formatRelativeTime(comment.createdAt)}
                  {comment.updatedAt && comment.updatedAt !== comment.createdAt
                    ? " · 수정됨"
                    : ""}
                </div>

                {isEditing ? (
                  <div className="comment-edit-box">
                    <textarea
                      value={editingContent}
                      onChange={(e) => setEditingContent(e.target.value)}
                      placeholder="댓글을 수정하세요"
                    />

                    <div className="comment-actions">
                      <button
                        type="button"
                        onClick={() => void handleUpdateComment()}
                        disabled={!editingContent.trim() || editingSaving}
                      >
                        {editingSaving ? "저장 중..." : "저장"}
                      </button>

                      <button
                        type="button"
                        onClick={handleCancelEditComment}
                        disabled={editingSaving}
                      >
                        취소
                      </button>
                    </div>
                  </div>
                ) : (
                  <>
                    <div className="comment-content">{comment.content}</div>

                    {canEditComment && (
                      <div className="comment-actions">
                        <button
                          type="button"
                          onClick={() => handleStartEditComment(comment)}
                          disabled={isDeleting}
                        >
                          댓글 수정
                        </button>

                        <button
                          type="button"
                          onClick={() => void handleDeleteComment(comment.id)}
                          disabled={isDeleting}
                        >
                          {isDeleting ? "삭제 중..." : "댓글 삭제"}
                        </button>
                      </div>
                    )}
                  </>
                )}
              </div>
            );
          })}
        </div>

        <div className="comment-write">
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="댓글을 작성하세요"
          />

          <button
            type="button"
            onClick={() => void handleComment()}
            disabled={!content.trim() || commentSaving}
          >
            {commentSaving ? "작성 중..." : "댓글 작성"}
          </button>
        </div>
      </div>
    </div>
  );
}