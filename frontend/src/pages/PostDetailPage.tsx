import { useEffect, useState } from "react";
import {
  getPost,
  getComments,
  createComment,
  Post,
  Comment,
} from "../api/boardApi";
import { useParams } from "react-router-dom";
import { formatRelativeTime } from "../utils/time";
import "../styles/PostDetailPage.css";

export default function PostDetailPage() {
  const { id } = useParams<{ id: string }>();

  const [post, setPost] = useState<Post | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [content, setContent] = useState("");

  const postId = Number(id);

  useEffect(() => {
    if (!id) return;

    const load = async () => {
      const postData = await getPost(postId);
      setPost(postData);

      const commentData = await getComments(postId);
      setComments(commentData);
    };

    load();
  }, [id, postId]);

  const handleComment = async () => {
    const text = content.trim();
    if (!text) return;

    setContent("");

    await createComment({
      postId,
      content: text,
    });

    const commentData = await getComments(postId);
    setComments(commentData);
  };

  if (!post) {
    return (
      <div className="post-detail-wrapper">
        <p>로딩 중...</p>
      </div>
    );
  }

  return (
    <div className="post-detail-wrapper">
      {/* 게시글 카드 */}
      <div className="post-card">
        <div className="post-title">{post.title}</div>

        <div className="post-meta">
          {post.designerName ?? "익명"} · {formatRelativeTime(post.createdAt)}
        </div>

        <div className="post-content">{post.content}</div>
      </div>

      {/* 댓글 영역 */}
      <div className="comment-section">
        <div className="comment-title">댓글 {comments.length}개</div>

        <div className="comment-list">
          {comments.length === 0 && <p>아직 댓글이 없습니다.</p>}

          {comments.map((c) => (
            <div key={c.id} className="comment-item">
              <div className="comment-meta">
                {c.designerName ?? "익명"} · {formatRelativeTime(c.createdAt)}
              </div>

              <div className="comment-content">{c.content}</div>
            </div>
          ))}
        </div>

        {/* 댓글 작성 */}
        <div className="comment-write">
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="댓글을 작성하세요"
          />

          <button onClick={handleComment} disabled={!content.trim()}>
            댓글 작성
          </button>
        </div>
      </div>
    </div>
  );
}
