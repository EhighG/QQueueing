import { ExamplePage } from "@/pages-flat";
import { Metadata } from "next";
import React from "react";

export const metadata: Metadata = {
  title: "Example",
  description: "큐잉 Example 페이지.",
};

const Page = () => {
  return <ExamplePage />;
};

export default Page;
