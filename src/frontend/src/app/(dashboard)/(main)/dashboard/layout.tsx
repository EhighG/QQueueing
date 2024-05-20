import { Loading } from "@/shared";
import { Suspense } from "react";

export default function Layout({
  monitoring,
  resource,
}: {
  monitoring: React.ReactNode;
  resource: React.ReactNode;
}) {
  return (
    <>
      <div className="flex flex-1 border border-slate-300 rounded-md shadow-xl">
        <Suspense fallback={<Loading />}>{monitoring}</Suspense>
      </div>
      <div className="flex flex-1 border border-slate-300 rounded-md shadow-xl">
        <Suspense fallback={<Loading />}>{resource}</Suspense>
      </div>
    </>
  );
}
