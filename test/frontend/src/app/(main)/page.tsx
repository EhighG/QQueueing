import { MainTest } from "@/pages-flat";
import { Metadata } from "next";

export const metadata: Metadata = {
  title: "메인 페이지",
  description: "큐잉 메인 페이지",
};

export default function Home() {
  return <MainTest />;
}
