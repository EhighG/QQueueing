import { DocsPage } from "@/pages-flat";
import { Metadata } from "next";
import React from "react";

export const metadata: Metadata = {
  title: "Docs",
  description: "큐잉 Docs 페이지.",
};

const Page = () => {
  return <DocsPage />;
};

export default Page;
