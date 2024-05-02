"use client";
import { useEnqueue, useGetWaitingInfo } from "@/features";
import { useGetWaitingOut } from "@/features/waiting";
import { Button, cats, logo } from "@/shared";
import { LinearProgress } from "@mui/material";
import Image from "next/image";
import { useRouter } from "next/navigation";
import React, { useEffect, useState } from "react";
import { useSearchParams } from "next/navigation";

const WaitingPage = () => {
  const router = useRouter();
  const params = useSearchParams();
  const targetUrl = params.get("target-url") ?? "";
  const [idx, setIdx] = useState<number>(-1);
  const [idVal, setIdVal] = useState<string>("");
  const [partitionNo, setPartitionNo] = useState<number>(-1);
  const { data: enqueueInfo } = useEnqueue("targetUrl");
  const { data: waitingInfo } = useGetWaitingInfo(partitionNo, idx, idVal);
  const { data: waitingOut, refetch: handleButton } = useGetWaitingOut(
    partitionNo,
    idx
  );

  useEffect(() => {
    console.log(waitingOut);
  }, [waitingOut]);

  useEffect(() => {
    if (enqueueInfo) {
      console.log("enqueueInfo", enqueueInfo);
      setPartitionNo(enqueueInfo?.partitionNo ?? 1);
      setIdx(enqueueInfo.order);
      setIdVal(enqueueInfo.idVal);
    }
  }, [enqueueInfo]);

  useEffect(() => {
    if (waitingInfo?.redirectUrl) {
      router.push(waitingInfo?.redirectUrl);
    }
  }, [waitingInfo]);

  return (
    <div className="absolute z-0 flex inset-0 w-full h-full min-w-[800px] bg-black bg-opacity-70 items-center justify-center">
      <div className="w-[800px] h-[450px] bg-white rounded-md">
        <div className="flex flex-col justify-between h-full  p-4">
          <div className="flex w-full justify-between  items-center">
            <Image
              src={logo}
              alt="큐잉"
              className="size-[80px] object-contain"
            />
            <h1 className="text-[2rem] font-bold">접속 대기 중</h1>
            <Image
              src={cats}
              alt="product"
              className="size-[80px] object-contain"
            />
          </div>
          <div className="flex w-full justify-between items-center">
            <p className="font-bold">대기한 시간 : 31 초</p>
            <p className="font-bold">
              나의 대기 순번 :
              <span className="text-[2rem]">{waitingInfo?.myOrder ?? 0}</span>
            </p>
            <p className="font-bold">예상 시간: 약 179 초</p>
          </div>
          <LinearProgress
            className="h-[20px] rounded-full"
            variant="determinate"
            value={75}
          />
          <div className="w-full p-2 h-[100px] rounded-md border border-black">
            <p className="text-[1.5rem] font-bold">
              현재 접속자가 많아 대기 중입니다!
            </p>
            <p className="font-bold">
              대기 순서에 따라 자동 접속되니 조금만 기다려주세요.
            </p>
            <div className="flex items-center gap-20">
              <p className="font-bold">
                앞에
                <span className="text-q-blue animate-blink">
                  {waitingInfo?.totalQueueSize ?? 0}
                </span>
                명, 뒤에
                <span className="text-q-blue animate-blink">
                  {(waitingInfo &&
                    waitingInfo.totalQueueSize - waitingInfo.myOrder) ??
                    0}
                </span>
                명의 대기자가 있습니다.
              </p>
              <Button
                onClick={() => {
                  handleButton();
                  //router.back();
                }}
                className="h-[30px] border rounded-md border-black bg-red-600 px-4 text-white text-center"
              >
                중지
              </Button>
            </div>
          </div>
          <div className="w-full h-[100px] px-2 rounded-md border border-black overflow-auto">
            <p className="text-center font-bold"> 실시간 공지</p>
            <p className="font-bold">어드민 : 상품이 1개 나갔어요</p>
            <p className="font-bold">어드민 : 상품이 1개 나갔어요</p>
            <p className="font-bold">어드민 : 상품이 1개 나갔어요</p>
          </div>
          <p className="font-bold text-center">powered by QQueueing</p>
        </div>
      </div>
    </div>
  );
};

export default WaitingPage;
