import type { Metadata } from "next";
import "./globals.css";
import { RQProvider } from "@/shared";
import { Footer, Header } from "@/widgets";
import localFont from "next/font/local";

export const metadata: Metadata = {
  title: {
    template: "%s | 큐잉",
    default: "큐잉",
  },
  description: "누구나 쉽게 사용 가능한 대기열 시스템, 큐잉[QQueueing]",
};

// pretendard font 설정
const pretendard = localFont({
  src: "./PretendardVariable.woff2",
  display: "swap",
  weight: "45 920",
});

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <body
        className={
          "flex flex-1 w-screen h-screen min-h-screen justify-center min-w-[360px]" +
          pretendard.className
        }
      >
        <RQProvider>
          <main className="flex flex-col flex-1 w-full max-w-screen-xl h-full">
            <Header />
            <div className="flex flex-1">{children}</div>
            <Footer />
          </main>
        </RQProvider>
      </body>
    </html>
  );
}
