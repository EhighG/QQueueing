import { ManagePage } from "@/pages-flat";
import { Metadata } from "next";
import React from "react";

export const metadata: Metadata = {
  title: "대기열 관리",
  description: "대기열이 적용된 링크 관리 페이지",
};

type PageProps = {
  params: {
    id: string;
  };
};

const Page = ({ params }: PageProps) => {
  return <ManagePage id={params.id} />;
};

export default Page;
