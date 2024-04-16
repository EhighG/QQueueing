import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { cats } from "@/shared";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: {
    template: "%s | 큐잉",
    default: "큐잉",
  },
  description: "누구나 쉽게 사용 가능한 대기열 시스템, 큐잉[Queueing]",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      {/* <Header /> */}
      <body className="flex flex-1 min-h-screen">{children}</body>
      {/* <Footer /> */}
    </html>
  );
}
