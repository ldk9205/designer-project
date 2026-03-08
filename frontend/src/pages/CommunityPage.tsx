import { useEffect, useState } from "react";
import { getPosts } from "../api/boardApi";
import { useNavigate } from "react-router-dom";
import { formatRelativeTime } from "../utils/time";
import "../styles/CommunityPage.css";

export default function CommunityPage() {
  const [posts, setPosts] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();

  useEffect(() => {
    const loadPosts = async () => {
      try {
        const data = await getPosts();
        setPosts(data);
      } catch (e) {
        console.error(e);
        alert("게시글을 불러오지 못했습니다.");
      } finally {
        setLoading(false);
      }
    };

    loadPosts();
  }, []);

  if (loading) {
    return (
      <div className="community-wrapper">
        <p className="loading-text">로딩 중...</p>
      </div>
    );
  }

  return (
    <div className="community-wrapper">
      <div className="community-header">
        <h2>💬 커뮤니티</h2>

        <button onClick={() => navigate("/community/write")}>글쓰기</button>
      </div>

      {posts.length === 0 && (
        <div className="empty-post">올라온 게시물이 없습니다.</div>
      )}

      <div className="community-list">
        {posts.map((post) => (
          <div
            key={post.id}
            className="community-card"
            onClick={() => navigate(`/community/${post.id}`)}
          >
            <div className="post-title">{post.title}</div>

            <div className="post-meta">
              {post.designerName} · {formatRelativeTime(post.createdAt)}
            </div>

            <div className="post-preview">
              {post.content.length > 80
                ? post.content.slice(0, 80) + "..."
                : post.content}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
