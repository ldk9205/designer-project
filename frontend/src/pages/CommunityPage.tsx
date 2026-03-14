import { useEffect, useState } from "react";
import { getPosts, Post } from "../api/boardApi";
import { useNavigate } from "react-router-dom";
import { formatRelativeTime } from "../utils/time";
import "../styles/CommunityPage.css";

export default function CommunityPage() {
  const [posts, setPosts] = useState<Post[]>([]);
  const [loading, setLoading] = useState(true);

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const size = 5;

  const navigate = useNavigate();

  useEffect(() => {
    const loadPosts = async () => {
      try {
        setLoading(true);

        const data = await getPosts({
          page,
          size,
        });

        setPosts(data.content);
        setTotalPages(data.totalPages);
      } catch (e) {
        console.error(e);
        alert("게시글을 불러오지 못했습니다.");
      } finally {
        setLoading(false);
      }
    };

    loadPosts();
  }, [page]);

  if (loading) {
    return (
      <div className="community-wrapper">
        <p className="loading-text">로딩 중...</p>
      </div>
    );
  }

  return (
    <div className="community-wrapper">
      {/* 헤더 */}
      <div className="community-header">
        <h2>💬 커뮤니티</h2>

        <button onClick={() => navigate("/community/write")}>글쓰기</button>
      </div>

      {/* 게시글 없음 */}
      {posts.length === 0 && (
        <div className="empty-post">올라온 게시물이 없습니다.</div>
      )}

      {/* 게시글 리스트 */}
      <div className="community-list">
        {posts.map((post) => (
          <div
            key={post.id}
            className="community-card"
            onClick={() => navigate(`/community/${post.id}`)}
          >
            <div className="post-title">{post.title}</div>

            <div className="post-meta">
              {post.designerName ?? "익명"} ·{" "}
              {formatRelativeTime(post.createdAt)}
            </div>

            <div className="post-preview">
              {post.content.length > 80
                ? post.content.slice(0, 80) + "..."
                : post.content}
            </div>
          </div>
        ))}
      </div>

      {/* 페이징 */}
      <div className="pagination">
        {/* 이전 */}
        <button disabled={page === 0} onClick={() => setPage(page - 1)}>
          이전
        </button>

        {/* 페이지 번호 */}
        {[...Array(totalPages)].map((_, i) => (
          <button
            key={i}
            onClick={() => setPage(i)}
            className={page === i ? "active-page" : ""}
          >
            {i + 1}
          </button>
        ))}

        {/* 다음 */}
        <button
          disabled={page === totalPages - 1}
          onClick={() => setPage(page + 1)}
        >
          다음
        </button>
      </div>
    </div>
  );
}
