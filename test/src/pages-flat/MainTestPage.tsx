"use client";
import { Card } from "@/widgets";
import React, { useState } from "react";
import { useRouter } from "next/navigation";

const MainTest = () => {
  const router = useRouter();

  return (
    <div className="flex flex-col w-full h-full items-center justify-center">
      <div className="w-[340px]  gap-4">
        <Card />
        <button
          type="button"
          className="bg-blue-300 border p-2"
          onClick={() => router.push("/waiting")}
        >
          구매하기
        </button>
        <button
          type="button"
          className="bg-blue-300 border p-2"
          onClick={() => router.push("/regist")}
        >
          url 등록
        </button>
      </div>
    </div>
  );
};

export default MainTest;
