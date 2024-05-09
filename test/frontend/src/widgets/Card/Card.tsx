"use client";
import React from "react";
import Image from "next/image";
import { faker } from "@faker-js/faker";
import { useRouter } from "next/navigation";

type CardProps = {
  waiting?: boolean;
  productId: number;
};

const Card = ({ waiting, productId }: CardProps) => {
  const router = useRouter();
  const url = faker.image.url();

  return (
    <div className="flex flex-col w-full">
      <div>
        <div>
          <Image src={url} alt={url} width="640" height="480" priority />
        </div>
        <div>
          <div className="flex w-full justify-between">
            <p>{faker.commerce.productName()}</p>
            {waiting && <p>대기열 적용 중</p>}
          </div>
          <p>10개 남음</p>
        </div>
        <div>
          <button
            type="button"
            className="bg-blue-300 border p-2 rounded-md"
            onClick={() => router.push(`/product/${productId}`)}
          >
            구매하기
          </button>
        </div>
      </div>
    </div>
  );
};

export default Card;
