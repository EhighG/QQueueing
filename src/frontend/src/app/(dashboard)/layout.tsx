import { Header, NavMenu } from "@/widgets";

export default function Layout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <>
      <Header />
      <main className="min-w-[1440px]">
        <NavMenu />
        <div className="flex flex-col flex-1">
          <div className="flex flex-col flex-1 max-2xl:m-5 m-10 gap-10">
            {children}
          </div>
        </div>
      </main>
    </>
  );
}
