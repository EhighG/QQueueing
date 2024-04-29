"use client";
import { WaitingListType } from "@/entities/waitingList/type";
import { ImageRegist, Performance, postWaiting } from "@/features";
import { InputForm } from "@/features";
import { Button, Input, SectionTitle, cats } from "@/shared";
import { useMutation, useQueryClient } from "@tanstack/react-query";

import React, { useState } from "react";
const RegistPage = () => {
  const [waitingInfo, setWaitingInfo] =
    useState<Omit<WaitingListType, "id" | "queueImageUrl">>();
  const [imageData, setImageData] = useState<string>("");
  const queryClient = useQueryClient();
  // 대기열 등록
  const mutation = useMutation({
    mutationFn: postWaiting,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["waitingLists"] });
    },
    onError: () => {
      alert("error occur");
    },
  });

  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md border">
      <SectionTitle title="등록 페이지" />
      <div className="flex flex-1 max-2xl:m-5 m-10 ">
        <InputForm setWaitingInfo={setWaitingInfo} />
        <div className="flex flex-1">
          <div className="flex flex-1  flex-col max-2xl:m-5 m-10 gap-10">
            <ImageRegist setImageData={setImageData} />
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
