"use client";
import { cats, companyData } from "@/shared";
import Image from "next/image";
import { useRouter } from "next/navigation";
import React from "react";

type ProductProps = {
  id: number;
};
const Product = ({ id }: ProductProps) => {
  const router = useRouter();
  return (
    <div className="flex flex-1 items-center justify-center">
      <div className="flex flex-col items-center gap-10">
        <h1 className="text-[2rem] font-bold">
          삼성 소프트웨어 아카데미 특별 선착순 전형
        </h1>
        <div className="mb-5">
          <Image
            src={companyData[id].src}
            alt="상품이미지"
            className="size-[200px]"
            priority
          />
        </div>
        <h1 className="text-[1.5rem]">{companyData[id].name}</h1>
        <button
          type="button"
          className="border p-2 rounded-md bg-red-300"
          onClick={() => {
            router.push(`/success/${id}`);
          }}
        >
          선착순 지원하기
        </button>
      </div>
    </div>
  );
};

export default Product;
