"use client";
import { cats } from "@/shared";
import Image from "next/image";
import { useRouter, useSearchParams } from "next/navigation";
import React from "react";
import { useQuery } from "@tanstack/react-query";
import { getInfoApi } from "@/features";

const WaitingPage = () => {
  const router = useRouter();
  const params = useSearchParams();
  const idx = params.get("id") as string;

  const { data } = useQuery({
    queryKey: ["waiting"],
    queryFn: () => getInfoApi(parseInt(idx)),
    refetchInterval: 3000,
  });

  return (
    <div className="flex w-full h-full items-center justify-center">
      <div className="flex flex-col items-center">
        <div className="mb-5">
          <Image src={cats} alt="상품이미지" className="w-[300px] h-[400px]" />
        </div>
        <h1 className="text-[1.5rem]">나의 대기 순서</h1>
        <p className="font-bold text-center">
          전체 {data?.totalQueueSize ?? 0}명 중
        </p>
        <h1 className="text-[4rem] font-bold">{data?.myOrder ?? 0}번</h1>
        <input
          type="range"
          min={0}
          max={data?.totalQueueSize ?? 0}
          value={data?.myOrder ?? 0}
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
