import { Product } from "@/pages-flat";
import { companyData } from "@/shared";
import { Metadata } from "next";
import React from "react";

export async function generateMetadata({ params }: ProductParams) {
  return {
    title: companyData[params.id].name + " 채용",
    description:
      companyData[params.id].name + " " + companyData[params.id].title,
  };
}

type ProductParams = {
  params: {
    id: number;
  };
};

const ProductPage = ({ params }: ProductParams) => {
  return <Product id={params.id} />;
};

export default ProductPage;
