import { cats } from "@/shared";
import Image from "next/image";
import Link from "next/link";
import React from "react";

const WaitingPage = () => {
  return (
    <div className="flex w-full h-full items-center justify-center">
      <div className="flex flex-col items-center">
        <div className="mb-5">
          <Image src={cats} alt="상품이미지" className="size-[340px]" />
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
        <Link href="waiting/1">
          <button className="font-bold border rounded-lg p-2 border-black">
            대기 완료시 이동할 페이지
          </button>
        </Link>
      </div>
    </div>
  );
};

export default WaitingPage;
