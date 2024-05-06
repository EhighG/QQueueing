import { LicensePage } from "@/pages-flat";
import { Metadata } from "next";
import React from "react";

export const metadata: Metadata = {
  title: "License",
  description: "큐잉 License 페이지.",
};

const Page = () => {
  return <LicensePage />;
};

export default Page;
