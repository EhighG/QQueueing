import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { Loading, RQProvider } from "@/shared";
import { Footer, Header, NavMenu } from "@/widgets";
import { Suspense } from "react";

const inter = Inter({ subsets: ["latin"] });

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
      <body className={inter.className}>
        <Header />
        <RQProvider>
          <main>
            <NavMenu />
            <div className="flex flex-col flex-1">
              <div className="flex flex-col flex-1 max-2xl:m-5 m-10 gap-10">
                <Suspense fallback={<Loading />}>{children}</Suspense>
              </div>
            </div>
          </main>
        </RQProvider>
        <Footer />
      </body>
    </html>
  );
}
