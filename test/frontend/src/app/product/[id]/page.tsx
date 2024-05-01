import { Product } from "@/pages-flat";
import { Metadata } from "next";
import React from "react";

export const metadata: Metadata = {
  title: "상품 페이지",
  description: "큐잉 상품 페이지",
};

const ProductPage = () => {
  return <Product />;
};

export default ProductPage;
