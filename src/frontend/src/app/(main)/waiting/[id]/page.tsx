import { cats, logo } from "@/shared";
import Image from "next/image";
import React from "react";

const WaitingModal = () => {
  return (
    <div className="absolute z-0 flex inset-0 w-full h-full min-w-[800px] bg-slate-400 bg-opacity-50 items-center justify-center">
      <div className="w-[800px] h-[400px] bg-white">
        <div className="flex flex-col justify-between h-full  p-5">
          <div className="flex w-full justify-between  items-center">
            <Image
              src={logo}
              alt="큐잉"
              className="size-[80px] object-contain"
            />
            <h1 className="text-[2rem] font-bold">서비스 접속 대기중</h1>
            <Image
              src={cats}
              alt="product"
              className="size-[80px] object-contain"
            />
          </div>
          <div className="flex w-full justify-between items-center">
            <p>대기시간 : 31 초</p>
            <p>예상 대기 시간: 179 초</p>
          </div>
          <div className="w-full bg-slate-300 rounded-2xl py-2">게이지바</div>
          <div className="w-full h-[80px] rounded-md border border-black">
            안내페이지
          </div>
          <div className="w-full h-[80px] rounded-md border border-black">
            공지영역
          </div>
          <p className="font-bold text-center">powered by QQueueing</p>
        </div>
      </div>
    </div>
  );
};

export default WaitingModal;
