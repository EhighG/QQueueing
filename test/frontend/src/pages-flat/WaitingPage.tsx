"use client";
import { cats } from "@/shared";
import Image from "next/image";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import React from "react";
import { useQuery } from "@tanstack/react-query";

const WaitingPage = () => {
  const router = useRouter();
  const params = useSearchParams();
  const src = params.get("src") as string;
  const handleWaiting = async () => {
    const res = await fetch("https://jsonplaceholder.typicode.com/todos/1");

    if (!res.ok) throw new Error("error");

    return res.json();
  };
  const { data } = useQuery({
    queryKey: ["waiting"],
    queryFn: handleWaiting,
    retry: false,
  });

  return (
    <div className="flex w-full h-full items-center justify-center">
      <div className="flex flex-col items-center">
        <div className="mb-5">
          <Image src={src} alt="상품이미지" width={640} height={480} />
        </div>
        <h1 className="text-[1.5rem]">나의 대기 순서</h1>
        <p className="font-bold text-center">전체 10000명 중</p>
        <h1 className="text-[4rem] font-bold">3445번</h1>
        <input
          type="range"
          min={0}
          max={10000}
          value={3445}
          title="range"
          className="w-full"
          readOnly={true}
        />
        <div className="text-center">
          <p>현재 접속 인원이 많아 대기중 입니다.</p>
          <p>잠시 기다려주시면 예매 페이지로 이동합니다.</p>
        </div>
        <button
          className="font-bold border rounded-lg p-2 border-black"
          onClick={() => router.push("/waiting/1")}
        >
          대기 완료시 이동할 페이지
        </button>
      </div>
    </div>
  );
};

export default WaitingPage;
