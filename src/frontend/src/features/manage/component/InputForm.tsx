"use client";
import { WaitingListType } from "@/entities/waitingList/type";
import { Button, Input, SelectBox } from "@/shared";
import Link from "next/link";
import React, { Dispatch, SetStateAction, useEffect, useState } from "react";

type InputFormProps = {
  waitingDetail?: WaitingListType;
  setWaitingInfo: Dispatch<SetStateAction<WaitingListType>>;
};
const InputForm = ({ waitingDetail, setWaitingInfo }: InputFormProps) => {
  const [targetUrl, setTargetUrl] = useState<string>(
    process.env.NEXT_PUBLIC_TARGET_URL ?? ""
  );
  const [maxCapacity, setMaxCapacity] = useState<number>(0);
  const [processingPerMinute, setProcessingPerMinute] = useState<number>(0);
  const [serviceName, setServiceName] = useState("");

  useEffect(() => {
    if (waitingDetail) {
      setTargetUrl(waitingDetail.targetUrl);
      setMaxCapacity(waitingDetail.maxCapacity);
      setProcessingPerMinute(waitingDetail.processingPerMinute);
      setServiceName(waitingDetail.serviceName);
    }
  }, [waitingDetail]);

  useEffect(() => {
    setWaitingInfo((prev) => ({
      ...prev,
      targetUrl,
      maxCapacity,
      processingPerMinute,
      serviceName,
    }));
  }, [targetUrl, maxCapacity, processingPerMinute, serviceName]);

  return (
    <div className="flex w-full h-full flex-col  items-center">
      <div className="flex flex-1 flex-col w-full p-10">
        <div className="flex flex-col flex-1  gap-5">
          <Input
            label="대기열 등록 대상 URL"
            title="대기열 등록 대상 URL"
            value={targetUrl}
            onChange={(e) => setTargetUrl(e.target.value)}
          />
          <div className="flex justify-around gap-2">
            <SelectBox
              label="최대 수용 인원"
              step={100}
              onChange={setMaxCapacity}
              value={maxCapacity}
            />

            <SelectBox
              label="1분 당 처리 가능 인원"
              step={10}
              onChange={setProcessingPerMinute}
              value={processingPerMinute}
            />
          </div>
          <Input
            label="서비스 명"
            title="서비스 명"
            type="text"
            value={serviceName}
            onChange={(e) => setServiceName(e.target.value)}
          />
        </div>
      </div>
    </div>
  );
};

export default InputForm;
