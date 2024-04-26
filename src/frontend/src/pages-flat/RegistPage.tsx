"use client";
import { registWaiting } from "@/features";
import { cats } from "@/shared";
import { useMutation } from "@tanstack/react-query";
import Image from "next/image";
import Link from "next/link";
import React, { useState } from "react";

const RegistPage = () => {
  const [url, setUrl] = useState<string>("");
  const [maxQueue, setMaxQueue] = useState<number>(0);
  const [buffer, setBuffer] = useState<number>(0);
  const [name, setName] = useState("");

  const mutation = useMutation({
    mutationFn: registWaiting,
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
    <div className="flex flex-1 flex-col border border-slate-300 rounded-md shadow-xl">
      <div className="flex w-full items-center border-b border-black h-[60px] p-3">
        <p className="text-[1.5rem] font-bold">등록 페이지</p>{" "}
      </div>
      <div className="flex flex-1 max-2xl:m-5 m-10 ">
        <div className="flex flex-1 flex-col  items-center">
          <div className="flex flex-1 flex-col w-full p-10">
            <div className="flex flex-col flex-1  gap-5">
              <label className="text-[1.5rem] font-bold">
                대기열 등록 대상 URL
              </label>
              <input
                type="text"
                title="대기열 등록 대상 URL"
                className="w-full h-[50px] rounded-lg border border-black text-[1.5rem] p-1"
                value={url}
                onChange={(e) => setUrl(e.target.value)}
              />
              <label className="text-[1.5rem] font-bold">최대 수용 인원</label>
              <input
                type="number"
                title="최대 수용 인원"
                min={0}
                className="w-full h-[50px] rounded-lg border border-black text-[1.5rem] p-1"
                value={maxQueue}
                onChange={(e) => setMaxQueue(parseInt(e.target.value))}
              />
              <label className="text-[1.5rem] font-bold">
                1분 당 처리 인원
              </label>
              <input
                type="number"
                title="1분 당 처리 인원"
                className="w-full h-[50px] rounded-lg border border-black text-[1.5rem] p-1"
                min={0}
                value={buffer}
                onChange={(e) => setBuffer(parseInt(e.target.value))}
              />
              <label className="text-[1.5rem] font-bold">서비스 명</label>
              <input
                type="text"
                title="서비스 명"
                className="w-full h-[50px] rounded-lg border border-black text-[1.5rem] p-1"
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
              <div className="flex w-full justify-around">
                <Link href="/waiting/1">
                  <button
                    type="button"
                    className="w-[146px] h-[50px] text-[1.5rem] font-bold bg-gray-400 border border-black rounded-md"
                  >
                    미리 보기
                  </button>
                </Link>
                <button
                  type="button"
                  className="w-[146px] h-[50px] text-[1.5rem] font-bold bg-gray-400 border border-black rounded-md"
                >
                  등록
                </button>
              </div>
            </div>
          </div>
        </div>
        <div className="flex flex-1">
          <div className="flex flex-1  flex-col max-2xl:m-5 m-10 gap-10">
            <div className="flex flex-[2] overflow-auto flex-col border rounded-md border-black">
              <div className="flex w-full items-center border-b border-black h-[60px] p-3">
                <p className="text-[1.5rem] font-bold">대기열 이미지</p>
              </div>
              <div className="flex flex-1">
                <div className="flex flex-[1] justify-center">
                  <Image src={cats} alt="sample" className="" />
                </div>
                <div className="flex flex-[2] overflow-auto flex-col gap-5 p-2">
                  <p className="text-[1.5rem] font-bold">
                    대기열 이미지는 대기열 우측 상단에 표시 됩니다
                  </p>
                  <button
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
                  </button>
                </div>
              </div>
            </div>
            <div className="flex flex-col flex-[1] border rounded-md border-black">
              <div className="flex w-full items-center border-b border-black h-[60px] p-3">
                <p className="text-[1.5rem] font-bold">
                  시스템 성능 대비 예상치
                </p>
              </div>
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
