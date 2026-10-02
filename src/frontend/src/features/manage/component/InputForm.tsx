"use client";
import { WaitingListType } from "@/entities/waitingList/type";
import { Button, Input, SelectBox } from "@/shared";
import Link from "next/link";
import React, { Dispatch, SetStateAction, useEffect, useState } from "react";

// 분당 입장 인원 입력칸을 비우면 쓰는 값. main의 기본값(Registration.DEFAULT_PROCESSING_PER_MINUTE)과 같다.
const DEFAULT_PROCESSING_PER_MINUTE = 6000;

// 저장된 입장 속도를 입력칸 글자로 바꾼다. 값이 없거나 0 이하(입장 속도 검증 전에 등록한 대기열)면 칸을 비운다.
const toRateInput = (value?: number | null) =>
  value != null && value >= 1 ? String(value) : "";

// 입력칸 글자를 요청에 넣을 값으로 바꾼다. 비우면 기본값을 쓴다. 1 이상의 정수인지는 main이 검사한다.
const toProcessingPerMinute = (input: string) =>
  input.trim() === "" ? DEFAULT_PROCESSING_PER_MINUTE : Number(input);

type InputFormProps = {
  waitingDetail?: WaitingListType;
  setWaitingInfo: Dispatch<SetStateAction<WaitingListType>>;
};
const InputForm = ({ waitingDetail, setWaitingInfo }: InputFormProps) => {
  const [targetUrl, setTargetUrl] = useState<string>(
    process.env.NEXT_PUBLIC_TARGET_URL ?? ""
  );
  const [maxCapacity, setMaxCapacity] = useState<number>(0);
  // 분당 입장 인원 입력칸의 글자. 비운 칸을 나타내려고 숫자가 아니라 글자로 둔다.
  const [rateInput, setRateInput] = useState<string>("");
  const [serviceName, setServiceName] = useState("");

  useEffect(() => {
    if (waitingDetail) {
      setTargetUrl(waitingDetail.targetUrl);
      setMaxCapacity(waitingDetail.maxCapacity);
      setRateInput(toRateInput(waitingDetail.processingPerMinute));
      setServiceName(waitingDetail.serviceName);
    }
  }, [waitingDetail]);

  useEffect(() => {
    setWaitingInfo((prev) => ({
      ...prev,
      targetUrl,
      maxCapacity,
      processingPerMinute: toProcessingPerMinute(rateInput),
      serviceName,
    }));
  }, [targetUrl, maxCapacity, rateInput, serviceName]);

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
          <Input
            label="서비스 명"
            title="서비스 명"
            type="text"
            value={serviceName}
            onChange={(e) => setServiceName(e.target.value)}
          />
          <Input
            label="분당 입장 인원"
            title="분당 입장 인원"
            type="number"
            min={1}
            step={1}
            placeholder={`비우면 ${DEFAULT_PROCESSING_PER_MINUTE}(1초에 100명)`}
            value={rateInput}
            onChange={(e) => setRateInput(e.target.value)}
          />
        </div>
      </div>
    </div>
  );
};

export default InputForm;
