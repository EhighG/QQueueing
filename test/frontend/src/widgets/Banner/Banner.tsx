"use client";
import React, { useEffect, useState } from "react";
import Image from "next/image";
import { faker } from "@faker-js/faker";
import { useRouter } from "next/navigation";
import { companyData } from "@/shared";

type CardProps = {
  productId: number;
};

const Card = ({ productId }: CardProps) => {
  const router = useRouter();

  return (
    <div className="flex w-full mx-10 px-4 py-10 bg-white rounded-md border">
      <div className="flex flex-col items-center justify-center gap-10">
        <div className="flex w-full h-[100px]">
          <Image
            src={companyData[productId].src}
            alt="productImage"
            width={500}
            height={500}
          />
        </div>
        <div className="flex flex-col items-center gap-4">
          <p className="text-[1.5rem] font-bold">
            {companyData[productId].name}
          </p>
          <p>{companyData[productId].title}</p>
        </div>
        <div>
          <button
            type="button"
            className="bg-blue-300 border p-2 rounded-md"
            onClick={() => router.push(`/product/${productId}`)}
          >
            지원하기
          </button>
        </div>
      </div>
    </div>
  );
};

export default Card;
