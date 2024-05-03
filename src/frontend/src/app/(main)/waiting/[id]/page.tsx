import React from "react";

import { WaitingPage } from "@/pages-flat";
import { Metadata } from "next";


export const metadata:Metadata = {
  title : "대기열 페이지",
  description : "큐잉 대기 페이지",
}

const WaitingModal = () => {
  return <WaitingPage />;
};

export default WaitingModal;
