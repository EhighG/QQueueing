import { AboutPage } from "@/pages-flat";
import { Metadata } from "next";
import React from "react";

export const metadata: Metadata = {
  title: "About",
  description: "큐잉 About 페이지.",
};

const Page = () => {
  return <AboutPage />;
};

export default Page;
