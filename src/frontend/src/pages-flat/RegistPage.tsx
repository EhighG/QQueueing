"use client";
import { WaitingListType } from "@/entities/waitingList/type";
import {
  ImageRegist,
  Performance,
  postWaiting,
  useRegistWaiting,
} from "@/features";
import { InputForm } from "@/features";
import { usePostWaitingImage } from "@/features/manage/query";
import { SectionTitle } from "@/shared";

import React, { useEffect, useState } from "react";
const RegistPage = () => {
  const [waitingInfo, setWaitingInfo] = useState<WaitingListType>(
    {} as WaitingListType
  );
  const [imageFile, setImageFile] = useState<File>({} as File);

  const formData = new FormData();

  useEffect(() => {
    formData.append("image", imageFile);
  }, [imageFile]);

  const { mutate: handleRegistWaiting } = useRegistWaiting(waitingInfo);
  const { mutate: handlePostImage, data: imageData } =
    usePostWaitingImage(formData);

  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md border">
      <SectionTitle title="등록 페이지" />
      <div className="flex flex-1 max-2xl:m-5 m-10 ">
        <InputForm waitingInfo={waitingInfo} setWaitingInfo={setWaitingInfo} />
        <div className="flex flex-1">
          <div className="flex flex-1  flex-col max-2xl:m-5 m-10 gap-10">
            <ImageRegist setImageData={setImageFile} />
            <div className="flex flex-col flex-[1] border rounded-md border-black">
              <SectionTitle title="시스템 성능 대비 예상치" />
              <Performance />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegistPage;
