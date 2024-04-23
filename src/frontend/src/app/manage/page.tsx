import { Metadata } from "next";
import React from "react";

export const metadata: Metadata = {
  title: "대기열 관리",
  description: "대기열이 적용된 링크 관리 페이지",
};

const Page = () => {
  return (
    <div className="flex flex-1 border border-slate-300 rounded-md shadow-xl">
      대기열 상태 관리 페이지
    </div>
  );
};

export default Page;
