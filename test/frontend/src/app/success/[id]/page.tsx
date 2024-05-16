import { SuccessPage } from "@/pages-flat";
import { Metadata } from "next";
import React from "react";
import { companyData } from "@/shared";

export async function generateMetadata({ params }: successPageParams) {
  return {
    title: companyData[params.id].name + " 결과",
    description:
      companyData[params.id].name + " " + companyData[params.id].title,
  };
}
type successPageParams = {
  params: {
    id: number;
  };
};

const page = ({ params }: successPageParams) => {
  return <SuccessPage id={params.id} />;
};

export default page;
