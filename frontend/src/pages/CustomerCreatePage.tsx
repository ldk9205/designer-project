import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createCustomerApi } from "../api/customer";
import type {
  CustomerCreateRequestDto,
  CustomerResponseDto,
} from "../types/customer";
import "../styles/CustomerCreatePage.css";

export default function CustomerCreatePage() {
  const navigate = useNavigate();

  const [form, setForm] = useState<CustomerCreateRequestDto>({
    name: "",
    phone: "",
    memo: "",
  });

  const [createdCustomer, setCreatedCustomer] =
    useState<CustomerResponseDto | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const onChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setCreatedCustomer(null);

    const trimmedName = form.name.trim();
    const trimmedPhone = form.phone.trim();
    const trimmedMemo = form.memo?.trim() || "";

    if (!trimmedName) {
      setError("이름은 필수입니다.");
      setLoading(false);
      return;
    }

    if (!trimmedPhone) {
      setError("전화번호는 필수입니다.");
      setLoading(false);
      return;
    }

    try {
      const payload: CustomerCreateRequestDto = {
        name: trimmedName,
        phone: trimmedPhone,
        memo: trimmedMemo,
      };

      const created = await createCustomerApi(payload);
      setCreatedCustomer(created);
      navigate(`/customers/${created.id}`);
    } catch (err: any) {
      setError(err?.response?.data?.message || err?.message || "고객 등록 실패");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="customer-create-page">
      <div className="customer-create-card">
        <h1 className="customer-create-title">고객 등록</h1>
        <p className="customer-create-subtitle">새 고객 정보를 입력해주세요</p>

        <form className="customer-create-form" onSubmit={onSubmit}>
          <div className="customer-create-input-group">
            <label htmlFor="name">이름</label>
            <input
              id="name"
              type="text"
              name="name"
              value={form.name}
              onChange={onChange}
              placeholder="고객 이름"
            />
          </div>

          <div className="customer-create-input-group">
            <label htmlFor="phone">전화번호</label>
            <input
              id="phone"
              type="text"
              name="phone"
              value={form.phone}
              onChange={onChange}
              placeholder="전화번호"
            />
          </div>

          <div className="customer-create-input-group">
            <label htmlFor="memo">메모</label>
            <textarea
              id="memo"
              name="memo"
              value={form.memo ?? ""}
              onChange={onChange}
              placeholder="메모를 입력하세요"
            />
          </div>

          {error && <div className="customer-create-error">{error}</div>}

          <button type="submit" disabled={loading}>
            {loading ? "등록 중..." : "고객 등록"}
          </button>
        </form>

        {createdCustomer && (
          <div className="customer-create-success">
            등록 완료: {createdCustomer.name} / ID: {createdCustomer.id}
          </div>
        )}
      </div>
    </div>
  );
}