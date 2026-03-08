import { useEffect, useState } from "react";
import { getPost, getComments, createComment } from "../api/boardApi";
import { useParams } from "react-router-dom";
import { formatRelativeTime } from "../utils/time";
import "../styles/PostDetailPage.css";

export default function PostDetailPage() {
  const { id } = useParams();

  const [post, setPost] = useState<any>(null);
  const [comments, setComments] = useState<any[]>([]);
  const [content, setContent] = useState("");

  useEffect(() => {
    const load = async () => {
      const postData = await getPost(Number(id));
      setPost(postData);

      const commentData = await getComments(Number(id));
      setComments(commentData);
    };

    load();
  }, [id]);

  const handleComment = async () => {
    const text = content.trim();

    if (!text) return;

    setContent("");

    await createComment({
      postId: Number(id),
      content: text,
    });

    const commentData = await getComments(Number(id));
    setComments(commentData);
  };

  if (!post) return <div>로딩 중...</div>;

  return (
    <div className="post-detail-wrapper">
      {/* 게시글 카드 */}
      <div className="post-card">
        <div className="post-title">{post.title}</div>

        <div className="post-meta">
          {post.designerName} · {formatRelativeTime(post.createdAt)}
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
                {c.designerName} · {formatRelativeTime(c.createdAt)}
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
