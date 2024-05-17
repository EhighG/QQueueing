"use client";
import { ImageRegist, useRegistWaiting } from "@/features";
import { InputForm } from "@/features";
import { usePostWaitingImage } from "@/features";
import { Button, SectionTitle, logo, useDebounce } from "@/shared";
import { LinearProgress } from "@mui/material";
import Image from "next/image";

import React, { useEffect, useState } from "react";
import TargetPage from "@/features/manage/component/TargetPage";
import { WaitingListType } from "@/entities/waitingList/type";
const RegistPage = () => {
  const [waitingInfo, setWaitingInfo] = useState<WaitingListType>(
    {} as WaitingListType
  );
  const [imageFile, setImageFile] = useState<File>({} as File);

  const [thumbnail, setThumbnail] = useState<string>("");

  const { mutate: handleRegistWaiting } = useRegistWaiting();

  const {
    mutate: handlePostImage,
    isSuccess,
    data: imageResponse,
  } = usePostWaitingImage(imageFile);

  useEffect(() => {
    if (isSuccess) {
      setWaitingInfo((prev) => ({
        ...prev,
        queueImageUrl: imageResponse.result,
      }));
      handleRegistWaiting({
        ...waitingInfo,
        queueImageUrl: imageResponse.result,
      });
    }
  }, [isSuccess]);

  const pageUrl = useDebounce(waitingInfo.targetUrl, 1000);

  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md border">
      <SectionTitle title="등록 페이지" />
      {/* Section Container*/}
      <div className="flex flex-1 flex-col max-2xl:m-5 m-10 p-5 border rounded-md border-slate-300">
        {/* Section */}
        <div className="flex flex-1 gap-5">
          {/* Left Section */}
          <div className="flex flex-1 flex-col gap-10">
            {/*Left Top*/}
            <div className="flex flex-col">
              <InputForm setWaitingInfo={setWaitingInfo} />
            </div>
            {/*Left Middle*/}
            <div className="flex border border-md shadow-sm border-black rounded-md p-4">
              <ol className="font-bold text-[1.5rem]">
                <li>1. 대기열을 등록할 URL을 입력해주세요</li>
                <li>2. 타겟 페이지가 적용할 페이지가 맞는지 확인하세요</li>
                <li>3. 상품(서비스) 명을 입력해주세요</li>
                <li>4. 대표 이미지가 있다면 업로드 해주세요</li>
                <li>5. 등록 버튼을 누르면 대기열이 적용됩니다.</li>
              </ol>
            </div>
            {/*Left Bottom*/}
            <div className="flex flex-1 flex-col gap-4">
              <ImageRegist
                setThumbnail={setThumbnail}
                setImageData={setImageFile}
              />
            </div>
          </div>

          {/* Right Section */}
          <div className="flex flex-1 flex-col gap-4">
            {/* Right Top*/}
            <div className="flex flex-1 min-h-[600px]  flex-col  gap-10">
              <div className="flex flex-col flex-1 border rounded-md border-black">
                <SectionTitle title="타겟 페이지" />
                <TargetPage targetUrl={pageUrl} />
              </div>
            </div>

            {/* Right Bottom */}
            <div className="flex flex-col gap-10">
              <div className="flex flex-col  border rounded-md border-black">
                <SectionTitle title="미리보기" />
                <div className="flex flex-col justify-between h-full  p-4">
                  <div className="flex w-full justify-between  items-center">
                    <Image
                      src={logo}
                      alt="큐잉"
                      className="size-[80px] object-contain"
                    />
                    <h1 className="text-[2rem] font-bold">접속 대기 중</h1>
                    <Image
                      // 최신이미지 url로 대체 필요
                      src={thumbnail ? thumbnail : logo}
                      alt="product"
                      width={500}
                      height={500}
                      className="size-[80px]"
                    />
                  </div>
                  <div className="flex w-full justify-between items-center">
                    <p className="font-bold">대기한 시간 : 0 초</p>
                    <p className="font-bold">
                      나의 대기 순번 :<span className="text-[2rem]">0</span>
                    </p>
                    <p className="font-bold">예상 시간: 약 0 초</p>
                  </div>
                  <LinearProgress
                    className="h-[20px] rounded-full"
                    variant="determinate"
                    value={75}
                  />
                  <div className="w-full p-2 h-[120px] rounded-md border border-black">
                    <p className="text-[1.5rem] font-bold">
                      현재 접속자가 많아 대기 중입니다!
                    </p>
                    <p className="font-bold">
                      대기 순서에 따라 자동 접속되니 조금만 기다려주세요.
                    </p>
                    <div className="flex items-center gap-20">
                      <p className="font-bold">
                        앞에
                        <span className="text-q-blue animate-blink">0</span>
                        명, 뒤에
                        <span className="text-q-blue animate-blink">0</span>
                        명의 대기자가 있습니다.
                      </p>
                    </div>
                  </div>
                  <p className="font-bold text-center">powered by QQueueing</p>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div className="flex w-full justify-end mt-2 gap-4">
          <Button
            edgeType="square"
            onClick={() => {
              imageFile?.name
                ? handlePostImage()
                : handleRegistWaiting(waitingInfo);
            }}
          >
            등록
          </Button>
        </div>
      </div>
    </div>
  );
};

export default RegistPage;
