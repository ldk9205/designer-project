import React, { useContext, useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import { getPost, updatePost, type Post } from "../api/boardApi";
import "../styles/EditPostPage.css";

export default function EditPostPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const auth = useContext(AuthContext);

  const postId = Number(id);

  const [post, setPost] = useState<Post | null>(null);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const myDesignerId = auth?.designer?.id ?? null;

  const canEditPost = useMemo(() => {
    if (!post || myDesignerId == null) return false;
    return post.designerId === myDesignerId;
  }, [post, myDesignerId]);

  useEffect(() => {
    if (!id || Number.isNaN(postId)) return;

    const loadPost = async () => {
      try {
        setLoading(true);

        const data = await getPost(postId);
        setPost(data);
        setTitle(data.title ?? "");
        setContent(data.content ?? "");
      } catch (e) {
        console.error(e);
        alert("게시글 정보를 불러오지 못했습니다.");
        navigate("/community");
      } finally {
        setLoading(false);
      }
    };

    void loadPost();
  }, [id, postId, navigate]);

  useEffect(() => {
    if (!loading && post && !canEditPost) {
      alert("작성자만 수정할 수 있습니다.");
      navigate(`/community/${postId}`, { replace: true });
    }
  }, [loading, post, canEditPost, navigate, postId]);

  const handleSubmit = async () => {
    const trimmedTitle = title.trim();
    const trimmedContent = content.trim();

    if (!trimmedTitle || !trimmedContent) {
      alert("제목과 내용을 입력하세요.");
      return;
    }

    try {
      setSaving(true);

      await updatePost({
        id: postId,
        title: trimmedTitle,
        content: trimmedContent,
      });

      alert("게시글 수정 완료");
      navigate(`/community/${postId}`);
    } catch (e: any) {
      console.error(e);
      alert(e?.response?.data?.message || "게시글 수정에 실패했습니다.");
    } finally {
      setSaving(false);
    }
  };

  if (loading || !auth?.isAuthReady) {
    return (
      <div className="edit-post-wrapper">
        <div className="edit-post-card">
          <p>로딩 중...</p>
        </div>
      </div>
    );
  }

  if (!post) {
    return (
      <div className="edit-post-wrapper">
        <div className="edit-post-card">
          <p>게시글이 없습니다.</p>
        </div>
      </div>
    );
  }

  if (!canEditPost) {
    return null;
  }

  return (
    <div className="edit-post-wrapper">
      <div className="edit-post-top-bar">
        <button
          type="button"
          className="edit-post-back-btn"
          onClick={() => navigate(`/community/${postId}`)}
        >
          ← 이전으로
        </button>
      </div>
      
      <div className="edit-post-card">
        <div className="edit-post-title">게시글 수정</div>

        <div className="edit-post-form">
          <input
            type="text"
            className="edit-post-input"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="제목을 입력하세요"
          />

          <textarea
            className="edit-post-textarea"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="내용을 입력하세요"
          />

          <div className="edit-post-actions">
            <button
              type="button"
              className="edit-post-cancel-btn"
              onClick={() => navigate(`/community/${postId}`)}
              disabled={saving}
            >
              취소
            </button>

            <button
              type="button"
              className="edit-post-submit-btn"
              onClick={() => void handleSubmit()}
              disabled={saving}
            >
              {saving ? "수정 중..." : "수정 완료"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}