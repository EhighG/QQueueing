"use client";
import {
  useEnqueue,
  useGetWaitingInfo,
  useGetWaitingOut,
  useGetServiceImage,
} from "@/features";
import {
  Button,
  cls,
  logo,
  auto_mobile,
  baby_chick,
  dolphin,
  egg,
  flag,
  front_chick,
  hatching_chick,
} from "@/shared";
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
  type ProgressValue = 0 | 10 | 20 | 30 | 40 | 50 | 60 | 70 | 80 | 90 | 100;

  const [progress, setProgress] = useState<ProgressValue>(0);

  // estimate 시간 추정 쿼리
  const processData = {
    "0": "w-[0%]",
    "10": "w-[10%]",
    "20": "w-[20%]",
    "30": "w-[30%]",
    "40": "w-[40%]",
    "50": "w-[50%]",
    "60": "w-[60%]",
    "70": "w-[70%]",
    "80": "w-[80%]",
    "90": "w-[90%]",
    "100": "w-[100%]",
  };

  const opacityData = {
    "0": "h-[100%]",
    "10": "h-[90%]",
    "20": "h-[80%]",
    "30": "h-[70%]",
    "40": "h-[60%]",
    "50": "h-[50%]",
    "60": "h-[40%]",
    "70": "h-[30%]",
    "80": "h-[20%]",
    "90": "h-[10%]",
    "100": "h-[0%]",
  };

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
      setPartitionNo(enqueueInfo.partitionNo);
      setIdx(enqueueInfo.order);
      setIdVal(enqueueInfo.idVal);
    }
  }, [enqueueInfo]);

  useEffect(() => {
    if (waitingInfo?.token) {
      window.location.href = `${process.env.NEXT_PUBLIC_BASE_URL}/waiting/page-req?token=${waitingInfo.token}`;
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
      let prog = Math.round(waitingInfo.myOrder / (waitingInfo.enterCnt + 1));

      setEstimateTime(prog * 3);
      setProgress((Math.round(prog * 10) * 10) as ProgressValue);
    }
  }, [waitingInfo]);

  const handleSrc = (progress: number) => {
    if (!progress || !waitingInfo) return egg;

    if (progress < 30) return egg;

    if (progress < 60) return hatching_chick;

    if (progress < 90) return baby_chick;

    return front_chick;
  };

  const handleTips = (progress: number) => {
    if (!progress || !waitingInfo) return "곧 대기번호가 발급 됩니다.";

    if (progress < 30) return "접속자가 많아 대기 중 이에요.";

    if (progress < 60) return "조금만 더 기다려 주세요.";

    if (progress < 90) return "곧 입장될 예정 입니다.";

    return "입장 준비 완료";
  };

  return (
    <div className="fixed z-0 flex inset-0 w-full h-full bg-black bg-opacity-70 items-center justify-center">
      <div className=" md:hidden flex flex-1 overflow-y-scroll  w-full h-full bg-gradient-to-b from-blue-400 via-blue-700 to-blue-600">
        <div className="relative flex flex-col flex-1 min-w-[360px] py-10">
          <div className="flex flex-col gap-2">
            <h1 className="font-bold max-sm:text-[1.5rem] sm:max-md:text-[2rem] text-center text-white">
              페이지에 진입 하기 위해 <br />{" "}
              <span className="text-amber-400">
                {waitingInfo?.myOrder ? waitingInfo.myOrder : 0}명
              </span>
              이 기다리고 있어요.
            </h1>
            <p className="max-[400px]:text-[1rem] min-[400px]:max-sm:text-[1rem] sm:max-md:text-[1.5rem] text-center text-white">
              조금만 더 기다려 주세요
            </p>
            <p className="max-[400px]:text-[1rem] min-[400px]:max-sm:text-[1rem] sm:max-md:text-[1.5rem] text-center text-white">
              대기 순서에 따라 자동으로 접속됩니다.{" "}
            </p>
          </div>
          <div className="w-full aspect-square self-center">
            <div className="flex w-full h-full items-center justify-center p-10 animate-slowPulse">
              <Image src={logo} alt="logo" width={500} height={500} />
            </div>
          </div>
          <div className="w-full h-[8%] min-h-[60px] ">
            <div className="flex mx-[10%] h-full bg-yellow-300 items-center justify-center rounded-md">
              <p className="text-[1.5rem] text-blue-800 font-bold animate-pulse">
                {handleTips(progress)}
              </p>
            </div>
          </div>
          <div className="flex flex-1 flex-col items-center justify-center">
            <div className="size-[100px]">
              <Image
                src={handleSrc(progress)}
                alt="chick"
                width={500}
                height={500}
                className={cls(
                  handleSrc(progress) === egg
                    ? "animate-shaking"
                    : "animate-fadeIn"
                )}
              />
            </div>
          </div>
        </div>
      </div>
      <div className="max-md:hidden w-[800px] h-[400px] bg-white rounded-md">
        <div className="flex flex-col justify-between h-full  p-4">
          <div className="flex w-full justify-between  items-center">
            <div className="relative size-[80px]">
              <Image src={logo} alt="큐잉" width={500} height={500} />
              <span
                className={cls(
                  "absolute block z-10 bg-white w-full bottom-0 opacity-80 transition-all duration-500",
                  opacityData[progress]
                )}
              ></span>
            </div>
            <div className="flex relative items-center gap-4">
              <h1 className="text-[2rem] font-bold animate-pulse">
                접속 대기 중
              </h1>
              <div className="absolute right-[-50px] bottom-0 size-[50px]">
                <Image src={dolphin} alt="dolphin" width={100} height={100} />
              </div>
            </div>
            <div className="relative size-[80px]">
              <Image
                src={imageData ? "data:image/png;base64, " + imageData : logo}
                alt="product"
                width={500}
                height={500}
              />
              <span
                className={cls(
                  "absolute block z-10 bg-white w-full bottom-0 opacity-80 transition-all duration-500",
                  opacityData[progress]
                )}
              ></span>
            </div>
          </div>
          <div className="flex w-full justify-between items-center">
            <p className="font-bold">대기한 시간 : {waitingTime} 초</p>
            <div className="flex flex-col justify-center items-center font-bold">
              <p>나의 순서 까지</p>
              <p className="text-[1.5rem]  truncate">
                <span className="text-[2rem] font-bold">
                  {waitingInfo?.myOrder ?? 0}번째
                </span>
              </p>
            </div>
            <p className="font-bold">예상 시간: 약 {estimateTime} 초</p>
          </div>
          <div className="flex relative items-center w-full h-[25px] bg-white rounded-lg border">
            <div
              className={cls(
                "relative flex h-full items-center z-0 rounded-lg transition-all duration-700",
                processData[progress],
                "bg-gradient-to-r from-blue-600 via-blue-400 to-blue-600 bg-[length:200px_100%] animate-shine"
              )}
            >
              <span className="block z-10 absolute size-[40px] right-[-30px] bottom-0">
                <Image
                  src={auto_mobile}
                  alt="car"
                  width={500}
                  height={500}
                  style={{ transform: "scaleX(-1)" }}
                />
              </span>
            </div>
            <div className="absolute size-[40px] right-[-25px]">
              <Image src={flag} alt="flag" width={500} height={500} />
            </div>
          </div>
          <div className="flex w-full p-2 h-[120px] rounded-md border border-black">
            <div>
              <p className="text-[1.5rem] font-bold">{handleTips(progress)}</p>
              <p className="font-bold">
                대기 순서에 따라 자동 접속되니 조금만 기다려주세요.
              </p>
              <div className="flex items-center gap-2">
                <p className="font-bold">
                  앞에&nbsp;
                  <span className="text-[1.5rem] text-q-blue animate-blink">
                    {waitingInfo?.myOrder ? waitingInfo.myOrder - 1 : 0}
                  </span>
                  &nbsp;명, 뒤에 &nbsp;
                  <span className="text-[1.5rem] text-q-blue animate-blink">
                    {waitingInfo
                      ? waitingInfo.totalQueueSize + 1 - waitingInfo.myOrder >=
                        0
                        ? waitingInfo.totalQueueSize + 1 - waitingInfo.myOrder
                        : 0
                      : 0}
                  </span>
                  &nbsp;명의 대기자가 있습니다.
                </p>
                <Button
                  onClick={() => handleButton()}
                  className="h-[30px] border rounded-md border-black bg-red-600 px-4 text-white text-center"
                >
                  나가기
                </Button>
              </div>
            </div>
            <div className="flex flex-1 items-center justify-center">
              <div className="size-[80px]">
                <Image
                  src={handleSrc(progress)}
                  alt="chick"
                  width={500}
                  height={500}
                  className={cls(
                    handleSrc(progress) === egg
                      ? "animate-shaking"
                      : "animate-fadeIn"
                  )}
                />
              </div>
            </div>
          </div>
          <p className="font-bold text-center">powered by QQueueing</p>
        </div>
      </div>
    </div>
  );
};

export default WaitingPage;
