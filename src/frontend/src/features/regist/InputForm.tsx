"use client";
import { WaitingListType } from "@/entities/waitingList/type";
import { Button, Input } from "@/shared";
import Link from "next/link";
import React, { useEffect, useState } from "react";

type InputFormProps = {
  setWaitingInfo: (data: Omit<WaitingListType, "id" | "queueImageUrl">) => void;
};

const InputForm = ({ setWaitingInfo }: InputFormProps) => {
  const [registrationUrl, setRegistrationUrl] = useState<string>("");
  const [maxCapacity, setMaxCapacity] = useState<number>(0);
  const [processingPerMinute, setProcessingPerMinute] = useState<number>(0);
  const [serviceName, setServiceName] = useState("");

  useEffect(() => {
    setWaitingInfo({
      registrationUrl,
      maxCapacity,
      processingPerMinute,
      serviceName,
    });
  }, [
    registrationUrl,
    maxCapacity,
    processingPerMinute,
    serviceName,
    setWaitingInfo,
  ]);

  return (
    <div className="flex flex-1 flex-col  items-center">
      <div className="flex flex-1 flex-col w-full p-10">
        <div className="flex flex-col flex-1  gap-5">
          <Input
            label="대기열 등록 대상 URL"
            title="대기열 등록 대상 URL"
            value={registrationUrl}
            onChange={(e) => setRegistrationUrl(e.target.value)}
          />
          <Input
            label="최대 수용 인원"
            title="최대 수용 인원"
            type="number"
            value={maxCapacity}
            onChange={(e) => setMaxCapacity(parseInt(e.target.value))}
          />
          <Input
            label="1분 당 처리 인원"
            title=" 1분 당 처리 인원"
            type="number"
            min={0}
            value={processingPerMinute}
            onChange={(e) => setProcessingPerMinute(parseInt(e.target.value))}
          />
          <Input
            label="서비스 명"
            title="서비스 명"
            type="text"
            value={serviceName}
            onChange={(e) => setServiceName(e.target.value)}
          />
          <div className="flex w-full justify-around">
            <Link href="/waiting/1">
              <Button type="button" style="square">
                미리 보기
              </Button>
            </Link>
            <Button style="square">등록</Button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default InputForm;
