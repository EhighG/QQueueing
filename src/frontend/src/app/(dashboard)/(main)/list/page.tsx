import ListPage from "@/pages-flat/ListPage";
import { Metadata } from "next";

export const metadata: Metadata = {
  title: "대기열 리스트",
  description: "대기열 리스트 페이지",
};

const Page = () => {
  return <ListPage />;
};

export default Page;
