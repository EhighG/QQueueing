import type { Metadata } from "next";
import localFont from "next/font/local";
import "./globals.css";
import { RQProvider } from "@/shared";
import { Header, NavMenu } from "@/widgets";

//pretendard font 설정
const pretendard = localFont({
  src: "./PretendardVariable.woff2",
  display: "swap",
  weight: "45 920",
});

export const metadata: Metadata = {
  title: {
    template: "%s | 큐잉",
    default: "큐잉",
  },
  icons: {
    icon: "/favicon.png",
  },
  description: "편리한 오픈 소스 대기열 시스템 큐잉",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={pretendard.className}>
        <Header />
        <RQProvider>
          <main>
            <NavMenu />
            <div className="flex flex-col flex-1">
              <div className="flex flex-col flex-1 max-2xl:m-5 m-10 gap-10">
                {children}
              </div>
            </div>
          </main>
        </RQProvider>
      </body>
    </html>
  );
}
