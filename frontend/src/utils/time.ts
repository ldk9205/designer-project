export const formatRelativeTime = (time: string) => {
  if (!time) return "";

  const now = new Date();
  const date = new Date(time);

  const diff = Math.floor((now.getTime() - date.getTime()) / 1000);

  const minutes = Math.floor(diff / 60);
  const hours = Math.floor(diff / 3600);
  const days = Math.floor(diff / 86400);
  const weeks = Math.floor(days / 7);
  const months = Math.floor(days / 30);
  const years = Math.floor(days / 365);

  if (minutes < 1) return "방금 전";

  if (minutes < 10) {
    return `${minutes}분 전`;
  }

  if (minutes < 60) {
    const rounded = Math.floor(minutes / 10) * 10;
    return `${rounded}분 전`;
  }

  if (hours < 24) {
    return `${hours}시간 전`;
  }

  if (days < 7) {
    return `${days}일 전`;
  }

  if (weeks <= 4) {
    return `${weeks}주 전`;
  }

  if (months < 12) {
    return `${months}달 전`;
  }

  return `${years}년 전`;
};
