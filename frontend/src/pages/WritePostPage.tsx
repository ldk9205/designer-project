import { useState } from "react";
import { createPost } from "../api/boardApi";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import "../styles/WritePostPage.css";

export default function WritePostPage() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");

  const navigate = useNavigate();

  const handleSubmit = async () => {
    if (!title.trim() || !content.trim()) {
      alert("제목과 내용을 입력하세요.");
      return;
    }

    await createPost({
      title: title,
      content: content,
    });

    alert("게시글 작성 완료");

    navigate("/community");
  };

  return (
    <div className="write-wrapper">
      <Header />
      
      <div className="write-card">
        <div className="write-title">게시글 작성</div>

        <div className="write-form">
          <input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="제목을 입력하세요"
          />

          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="내용을 입력하세요"
            onKeyDown={(e) => {
              if (e.ctrlKey && e.key === "Enter") {
                handleSubmit();
              }
            }}
          />

          <div className="write-actions">
            <button
              className="cancel-btn"
              onClick={() => navigate("/community")}
            >
              취소
            </button>

            <button className="submit-btn" onClick={handleSubmit}>
              작성
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
