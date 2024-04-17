import { WaitingPage } from "@/pages-flat";
import { Metadata } from "next";
import React from "react";

export const metadata: Metadata = {
  title: "대기 페이지",
  description: "대기 페이지 입니다.",
};

const Page = () => {
  return <WaitingPage />;
};

export default Page;
