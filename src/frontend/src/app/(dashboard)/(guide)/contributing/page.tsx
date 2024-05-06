import { ContributingPage } from "@/pages-flat";
import { Metadata } from "next";
import React from "react";

export const metadata: Metadata = {
  title: "Contribution",
  description: "큐잉 Contribution 페이지.",
};

const Page = () => {
  return <ContributingPage />;
};

export default Page;
