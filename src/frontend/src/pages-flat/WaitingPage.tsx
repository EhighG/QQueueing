"use client";
import {
  useEnqueue,
  useGetWaitingInfo,
  useGetWaitingOut,
  useGetServiceImage,
} from "@/features";
import { Button, logo } from "@/shared";
import { LinearProgress } from "@mui/material";
import Image from "next/image";
import { useRouter, useSearchParams } from "next/navigation";
import React, { useEffect, useState } from "react";

const WaitingPage = () => {
  const router = useRouter();
  const params = useSearchParams();
  const targetUrl = params.get("Target-URL") ?? "";
  const [idx, setIdx] = useState<number>(-1);
  const [idVal, setIdVal] = useState<string>("");
  const [partitionNo, setPartitionNo] = useState<number>(-1);
  const [estimateTime, setEstimateTime] = useState<number>(0);
  const [waitingTime, setWaitingTime] = useState<number>(0);
  const { data: enqueueInfo } = useEnqueue(targetUrl);
  const { data: waitingInfo } = useGetWaitingInfo(partitionNo, idx, idVal);
  const { refetch: handleButton, isSuccess } = useGetWaitingOut(
    partitionNo,
    idx
  );
  // estimate 시간 추정 쿼리

  const { data: imageData } = useGetServiceImage(targetUrl);

  // 대기 순번을 받은 순간 부터 timer 시작
  useEffect(() => {
    // enqueueInfo가 있을 때만 인터벌 설정
    if (!enqueueInfo) return;
    const interval = setInterval(() => {
      setWaitingTime((prev) => prev + 1);
    }, 1000);

    // 컴포넌트가 언마운트되거나 enqueueInfo가 변경되면 인터벌 정리
    return () => clearInterval(interval);
  }, [enqueueInfo]);

  useEffect(() => {
    if (enqueueInfo) {
      setPartitionNo(enqueueInfo?.partitionNo ?? 1);
      setIdx(enqueueInfo.order);
      setIdVal(enqueueInfo.idVal);
    }
  }, [enqueueInfo]);

  useEffect(() => {
    if (waitingInfo?.token) {
      window.location.href = `${process.env.NEXT_PUBLIC_BASE_URL}waiting/page-req?token=${waitingInfo.token}`;
    }
  }, [waitingInfo]);

  useEffect(() => {
    if (isSuccess) {
      router.back();
    }
  }, [router, isSuccess]);

  //  estimate 추정 로직
  useEffect(() => {
    if (waitingInfo) {
      setEstimateTime((waitingInfo.myOrder / (waitingInfo.enterCnt + 1)) * 3);
    }
  }, [waitingInfo]);

  return (
    <div className="fixed z-0 flex inset-0 w-full h-full min-w-[700px] bg-black bg-opacity-70 items-center justify-center">
      <div className="w-[800px] h-[400px] bg-white rounded-md">
        <div className="flex flex-col justify-between h-full  p-4">
          <div className="flex w-full justify-between  items-center">
            <Image
              src={logo}
              alt="큐잉"
              className="size-[80px] object-contain"
            />
            <h1 className="text-[2rem] font-bold">접속 대기 중</h1>
            <Image
              src={imageData ? "data:image/png;base64, " + imageData : logo}
              alt="product"
              width={500}
              height={500}
              className="size-[80px]"
            />
          </div>
          <div className="flex w-full justify-between items-center">
            <p className="font-bold">대기한 시간 : {waitingTime} 초</p>
            <p className="font-bold">
              나의 대기 순번 :
              <span className="text-[2rem]">{waitingInfo?.myOrder ?? 0}</span>
            </p>
            <p className="font-bold">예상 시간: 약 {estimateTime} 초</p>
          </div>
          <LinearProgress
            className="h-[20px] rounded-full"
            variant="determinate"
            value={
              (waitingInfo?.totalQueueSize && waitingInfo?.myOrder
                ? 1 -
                  (waitingInfo.myOrder - 1) / (waitingInfo.totalQueueSize + 1)
                : 0) * 100
            }
            color="primary"
          />
          <div className="w-full p-2 h-[120px] rounded-md border border-black">
            <p className="text-[1.5rem] font-bold">
              현재 접속자가 많아 대기 중입니다!
            </p>
            <p className="font-bold">
              대기 순서에 따라 자동 접속되니 조금만 기다려주세요.
            </p>
            <div className="flex items-center gap-2">
              <p className="font-bold">
                앞에
                <span className="text-q-blue animate-blink">
                  {waitingInfo?.myOrder ? waitingInfo.myOrder - 1 : 0}
                </span>
                명, 뒤에
                <span className="text-q-blue animate-blink">
                  {waitingInfo
                    ? waitingInfo.totalQueueSize + 1 - waitingInfo.myOrder >= 0
                      ? waitingInfo.totalQueueSize + 1 - waitingInfo.myOrder
                      : 0
                    : 0}
                </span>
                명의 대기자가 있습니다.
              </p>
              <Button
                onClick={() => handleButton()}
                className="h-[30px] border rounded-md border-black bg-red-600 px-4 text-white text-center"
              >
                나가기
              </Button>
            </div>
          </div>
          <p className="font-bold text-center">powered by QQueueing</p>
        </div>
      </div>
    </div>
  );
};

export default WaitingPage;
