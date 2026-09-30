import { Regist } from "@/pages-flat";
import { Metadata } from "next";
import React from "react";

export const metadata: Metadata = {
  title: "등록 페이지",
  description: "큐잉 서비스 URL 등록 페이지",
};

const Page = () => {
  return <Regist />;
};

export default Page;
