import { Metadata } from "next";
import React from "react";

export const metadata: Metadata = {
  title: "404페이지",
  description: "페이지 없음",
};

const NotFound = () => {
  return <div>not-found</div>;
};

export default NotFound;
