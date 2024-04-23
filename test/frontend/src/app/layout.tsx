import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { RQProvider, cats } from "@/shared";
import { Footer, Header } from "@/widgets";

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
      <body className="flex flex-1 w-screen h-screen min-h-screen justify-center">
        <RQProvider>
          <main className="flex flex-col flex-1 w-full max-w-screen-xl h-full">
            <Header />
            {children}
            <Footer />
          </main>
        </RQProvider>
      </body>
    </html>
  );
}
