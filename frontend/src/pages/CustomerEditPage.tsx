import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getCustomerApi, updateCustomerApi } from "../api/customer";
import type {
  CustomerResponseDto,
  CustomerUpdateRequestDto,
} from "../types/customer";
import "../styles/CustomerEditPage.css";

export default function CustomerEditPage() {
  const { customerId } = useParams();
  const navigate = useNavigate();

  const [original, setOriginal] = useState<CustomerResponseDto | null>(null);
  const [form, setForm] = useState<CustomerUpdateRequestDto>({
    name: "",
    phone: "",
    memo: "",
  });

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadCustomer = async () => {
      try {
        setLoading(true);
        setError("");

        if (!customerId) {
          throw new Error("customerId가 없습니다.");
        }

        const data = await getCustomerApi(Number(customerId));
        setOriginal(data);
        setForm({
          name: data.name ?? "",
          phone: data.phone ?? "",
          memo: data.memo ?? "",
        });
      } catch (err: any) {
        setError(err?.response?.data?.message || err?.message || "고객 조회 실패");
      } finally {
        setLoading(false);
      }
    };

    void loadCustomer();
  }, [customerId]);

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

    if (!customerId || !original) return;

    try {
      setSaving(true);
      setError("");

      const payload: CustomerUpdateRequestDto = {};

      const formName = form.name?.trim() ?? "";
      const originalName = original.name?.trim() ?? "";

      const formPhone = form.phone?.trim() ?? "";
      const originalPhone = original.phone?.trim() ?? "";

      const formMemo = form.memo ?? "";
      const originalMemo = original.memo ?? "";

      if (formName !== originalName) payload.name = formName;
      if (formPhone !== originalPhone) payload.phone = formPhone;
      if (formMemo !== originalMemo) payload.memo = formMemo;

      const hasNoChanges =
        payload.name === undefined &&
        payload.phone === undefined &&
        payload.memo === undefined;

      if (hasNoChanges) {
        const confirmed = window.confirm(
          "변경된 내용이 없습니다. 그대로 두고 상세 페이지로 돌아가시겠습니까?"
        );

        if (confirmed) {
          navigate(`/customers/${customerId}`);
        }

        return;
      }

      await updateCustomerApi(Number(customerId), payload);
      navigate(`/customers/${customerId}`);
    } catch (err: any) {
      setError(err?.response?.data?.message || err?.message || "고객 수정 실패");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="customer-edit-page">
        <div className="customer-edit-card">
          <div className="customer-edit-loading">불러오는 중...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="customer-edit-page">
      <div className="customer-edit-card">
        <h1 className="customer-edit-title">고객 수정</h1>
        <p className="customer-edit-subtitle">고객 정보를 수정해주세요</p>

        <form className="customer-edit-form" onSubmit={onSubmit}>
          <div className="customer-edit-input-group">
            <label htmlFor="name">이름</label>
            <input
              id="name"
              type="text"
              name="name"
              value={form.name ?? ""}
              onChange={onChange}
              placeholder="고객 이름"
            />
          </div>

          <div className="customer-edit-input-group">
            <label htmlFor="phone">전화번호</label>
            <input
              id="phone"
              type="text"
              name="phone"
              value={form.phone ?? ""}
              onChange={onChange}
              placeholder="전화번호"
            />
          </div>

          <div className="customer-edit-input-group">
            <label htmlFor="memo">메모</label>
            <textarea
              id="memo"
              name="memo"
              value={form.memo ?? ""}
              onChange={onChange}
              placeholder="메모를 입력하세요"
            />
          </div>

          {error && <div className="customer-edit-error">{error}</div>}

          <button type="submit" disabled={saving}>
            {saving ? "저장 중..." : "수정 저장"}
          </button>
        </form>
      </div>
    </div>
  );
}