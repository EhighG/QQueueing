"use client";
import { WaitingListType } from "@/entities/waitingList/type";
import { ImageRegist, InputForm } from "@/features";
import { useGetWaitingDetail, useDeleteWaiting } from "@/features";
import {
  useGetWaitingImage,
  usePatchWaiting,
  usePostWaitingActivate,
  usePostWaitingDeActivate,
  usePostWaitingImage,
} from "@/features/manage/query";
import { Button, SectionTitle, logo } from "@/shared";
import { LinearProgress } from "@mui/material";
import Image from "next/image";
import React, { useEffect, useState } from "react";

type ManagePageProps = {
  id: string;
};

const ManagePage = ({ id }: ManagePageProps) => {
  // 삭제
  const [imageFile, setImageFile] = useState<File>({} as File);
  const [thumbnail, setThumbnail] = useState<string>("");
  const { mutate: handleDelete } = useDeleteWaiting(id);
  const { data: waitingDetail } = useGetWaitingDetail(id);
  const [waitingInfo, setWaitingInfo] = useState<WaitingListType>(
    {} as WaitingListType
  );
  const [partitionNo, setPartitionNo] = useState<number>(0);

  useEffect(() => {
    if (waitingDetail) {
      setPartitionNo(waitingDetail.partitionNo);
    }
  }, [waitingDetail]);

  const { mutate: activate } = usePostWaitingActivate(partitionNo);
  const { mutate: deActivate } = usePostWaitingDeActivate(partitionNo);
  const { mutate: handlePatch } = usePatchWaiting(id);
  const { data: imageData } = useGetWaitingImage(id);
  const {
    mutate: handlePostImage,
    isSuccess,
    data: imageResponse,
  } = usePostWaitingImage(imageFile);

  const handleButton = () => {
    if (imageFile.name) {
      handlePostImage();
    } else {
      handlePatch(waitingInfo);
    }
  };

  useEffect(() => {
    if (isSuccess) {
      setWaitingInfo((prev) => ({
        ...prev,
        queueImageUrl: imageResponse.result,
      }));
      handlePatch({
        ...waitingInfo,
        queueImageUrl: imageResponse.result,
      });
    }
  }, [isSuccess]);

  useEffect(() => {
    if (imageData) {
      setThumbnail("data:image/png;base64, " + imageData);
    }
  }, [imageData]);

  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md border">
      <SectionTitle
        title={`대기열 ID ${id} ${
          waitingDetail?.isActive ? "활성 중" : "비 활성"
        }`}
      />
      {/* Section Container */}
      <div className="flex flex-1 flex-col max-2xl:m-5 m-10 p-5 border rounded-md border-slate-300">
        {/* Section */}
        <div className="flex flex-1 gap-5">
          {/* Left Section */}
          <div className="flex flex-1 flex-col gap-10">
            {/*Left Top*/}
            <div className="flex">
              <InputForm
                  waitingDetail={waitingDetail}
                  setWaitingInfo={setWaitingInfo}
              />
            </div>
            {/*Left Middle*/}
            <div className="flex border border-md shadow-sm border-black rounded-md p-4">
              <ol className="font-bold text-[1.5rem]">
                <li>1. 타겟 URL, 서비스 명의 변경이 가능합니다.</li>
                <li>2. 대기열 이미지 변경이 가능합니다.</li>
                <li>3. 변경을 눌러야 최종 반영 됩니다.</li>
                <li>4. 대기열을 활성화 하거나 비 활성화 할 수 있습니다</li>
                <li>5. 삭제를 눌러 대기열을 삭제할 수 있습니다</li>
                <li>6. 현재의 대기열 상태를 모니터링 할 수 있습니다</li>
              </ol>
            </div>
            {/*Left Bottom*/}
            <div className="flex flex-1 flex-col gap-4">
              <ImageRegist
                  thumbNail={thumbnail}
                  setThumbnail={setThumbnail}
                  setImageData={setImageFile}
              />
            </div>
          </div>

          {/* Right Section */}
          <div className="flex flex-1 flex-col gap-10">
            {/* Right Top */}
            <div className="flex flex-col border rounded-md border-black">
              <SectionTitle title="미리보기"/>
              <div className="flex flex-col gap-10 p-4 h-[460px]">
                <div className="flex w-full justify-between  items-center">
                  <Image
                      src={logo}
                      alt="큐잉"
                      className="size-[80px] object-contain"
                  />
                  <h1 className="text-[2rem] font-bold">접속 대기 중</h1>
                  <Image
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
            {/* Right Bottom */}
            <div className="flex flex-1 min-h-[450px] flex-col">
              <SectionTitle title="모니터링"/>
              <div className="flex flex-1 border rounded-md border-slate-300">
                <div className="w-full h-full grid grid-cols-2 grid-rows-2 place-items-center">
                  <div>모니터링 지표1</div>
                  <div>모니터링 지표2</div>
                  <div>모니터링 지표3</div>
                  <div>모니터링 지표4</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div className="flex w-full justify-end mt-2 gap-4">
          <Button
              edgeType="square"
              onClick={() => {
                waitingDetail?.isActive ? deActivate() : activate();
            }}
          >
            {waitingDetail?.isActive ? "비 활성" : "활성"}
          </Button>

          <Button edgeType="square" onClick={() => handleButton()}>
            변경
          </Button>
          <Button
            edgeType="square"
            color="warning"
            onClick={() => handleDelete()}
          >
            삭제
          </Button>
        </div>
      </div>
    </div>
  );
};

export default ManagePage;
