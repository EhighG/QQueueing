import { Metadata } from "next";
import React from "react";

export const metadata: Metadata = {
  title: "404 Not Found",
  description: "페이지 없음",
};
const NotFound = () => {
  return <div>Sorry, This Page is Not Available</div>;
};

export default NotFound;
