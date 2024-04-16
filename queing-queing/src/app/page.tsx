"use client";
import Card from "@/widgets/Card";
import Link from "next/link";
import { useState } from "react";
export default function Home() {
  const [url, setUrl] = useState("");
  return (
    <main className="flex flex-1 w-full min-h-screen flex-col justify-center items-center">
      <div className="flex flex-col w-[340px] gap-4">
        <Card />
        <Link href={`/waiting`}>
          <button type="button" className="bg-blue-300 border p-2">
            구매
          </button>
        </Link>
        <Link href="/regist">
          <button type="button" className="bg-blue-300 border p-2">
            url 등록
          </button>
        </Link>
      </div>
    </main>
  );
}
