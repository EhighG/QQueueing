"use client";
import { postWaiting } from "@/features";
import { Button, Input, SectionTitle, cats } from "@/shared";
import { useMutation } from "@tanstack/react-query";
import Image from "next/image";
import Link from "next/link";
import React, { useState } from "react";

const RegistPage = () => {
  const [url, setUrl] = useState<string>("");
  const [maxQueue, setMaxQueue] = useState<number>(0);
  const [buffer, setBuffer] = useState<number>(0);
  const [name, setName] = useState("");

  // 대기열 등록
  const mutation = useMutation({
    mutationFn: postWaiting,
    onSuccess: () => {
      alert("success");

      // Invalidate and refetch
      // queryClient.invalidateQueries({ queryKey: ['todos'] })
    },
    onError: () => {
      alert("error occur");
    },
  });

  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md border">
      <SectionTitle title="등록 페이지" />
      <div className="flex flex-1 max-2xl:m-5 m-10 ">
        <div className="flex flex-1 flex-col  items-center">
          <div className="flex flex-1 flex-col w-full p-10">
            <div className="flex flex-col flex-1  gap-5">
              <Input
                label="대기열 등록 대상 URL"
                title="대기열 등록 대상 URL"
                value={url}
                onChange={(e) => setUrl(e.target.value)}
              />
              <Input
                label="최대 수용 인원"
                title="최대 수용 인원"
                type="number"
                value={maxQueue}
                onChange={(e) => setMaxQueue(parseInt(e.target.value))}
              />
              <Input
                label="1분 당 처리 인원"
                title=" 1분 당 처리 인원"
                type="number"
                min={0}
                value={buffer}
                onChange={(e) => setBuffer(parseInt(e.target.value))}
              />
              <Input
                label="서비스 명"
                title="서비스 명"
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
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
        <div className="flex flex-1">
          <div className="flex flex-1  flex-col max-2xl:m-5 m-10 gap-10">
            <div className="flex flex-[2] overflow-auto flex-col border rounded-md border-black">
              <SectionTitle title="대기열 이미지" />
              <div className="flex flex-1">
                <div className="flex flex-[1] justify-center">
                  <Image src={cats} alt="sample" className="" />
                </div>
                <div className="flex flex-[2] overflow-auto flex-col gap-5 p-2">
                  <p className="text-[1.5rem] font-bold">
                    대기열 이미지는 대기열 우측 상단에 표시 됩니다
                  </p>
                  <Button
                    type="button"
                    className="p-2 border rounded-md border-black"
                    onClick={() => {
                      mutation.mutate({
                        url,
                        maxQueue,
                        buffer,
                        name,
                      });
                    }}
                  >
                    등록
                  </Button>
                </div>
              </div>
            </div>
            <div className="flex flex-col flex-[1] border rounded-md border-black">
              <SectionTitle title="시스템 성능 대비 예상치" />

              <div className="flex flex-1">
                <div className="flex flex-1 flex-col items-center justify-center gap-5 border-r border-black">
                  <p className="text-[1.5rem] font-bold">최대 수용 가능 인원</p>
                  <p className="text-[1.5rem] font-bold">200 명</p>
                </div>
                <div className="flex flex-1 flex-col items-center justify-center gap-5">
                  <p className="text-[1.5rem] font-bold">
                    1분 당 처리 가능 인원
                  </p>
                  <p className="text-[1.5rem] font-bold">200 명</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegistPage;
