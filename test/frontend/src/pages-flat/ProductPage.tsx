"use client";
import { cats } from "@/shared";
import Image from "next/image";
import React from "react";

const Product = () => {
  return (
    <div className="flex flex-1 w-full h-full items-center justify-center">
      <div className="flex flex-col items-center">
        <div className="mb-5">
          <Image
            src={cats}
            alt="상품이미지"
            className="size-[340px]"
            priority
          />
        </div>
        <h1 className="text-[1.5rem]">뮤지컬 캣츠</h1>

        <button
          type="button"
          className="border p-2 rounded-md bg-red-300"
          onClick={() => {
            alert("구매 완료");
          }}
        >
          구매하기
        </button>
      </div>
    </div>
  );
};

export default Product;
