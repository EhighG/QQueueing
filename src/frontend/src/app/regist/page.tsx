import { Metadata } from "next";
import React from "react";

export const metadata: Metadata = {
  title: "대기열 등록",
  description: "대기열을 적용할 링크 등록",
};

const Page = () => {
  return (
    <div className="flex flex-1 border border-slate-300 rounded-md shadow-xl">
      등록 페이지
    </div>
  );
};

export default Page;
