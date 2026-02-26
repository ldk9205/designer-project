import { useEffect, useState } from "react";
import { fetchCommunityList } from "../api/fileApi";
import { useNavigate } from "react-router-dom";
import "../styles/CommunityPage.css";

interface CommunityItem {
  imageId: number;
  customerName: string;
  treatmentDate: string;
  category: string;
  styleName: string;
  presignedUrl: string;
}

export default function CommunityPage() {
  const [posts, setPosts] = useState<CommunityItem[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const loadCommunity = async () => {
      try {
        const data = await fetchCommunityList();
        setPosts(data);
      } catch (error) {
        console.error(error);
        alert("커뮤니티 목록을 불러오지 못했습니다.");
      } finally {
        setLoading(false);
      }
    };

    loadCommunity();
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
        <h2>💬 커뮤니티 룸</h2>
        <button onClick={() => navigate("/home")}>홈으로</button>
      </div>

      <div className="community-grid">
        {posts.map((post) => (
          <div key={post.imageId} className="community-card">
            <img src={post.presignedUrl} alt={post.styleName} />

            <div className="community-info">
              <div className="customer-name">{post.customerName} 고객님</div>

              <div className="treatment-info">
                {post.treatmentDate} · {post.category}
              </div>

              {post.styleName && (
                <div className="style-name">{post.styleName}</div>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
