import { RegistPage } from "@/pages-flat";
import { Metadata } from "next";
import React from "react";

export const metadata: Metadata = {
  title: "대기열 등록",
  description: "대기열을 적용할 링크 등록",
};

const Page = () => {
  return <RegistPage />;
};

export default Page;
