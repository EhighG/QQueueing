"use client";
import React, { useEffect } from "react";
import Image from "next/image";
import { faker } from "@faker-js/faker";
import { useRouter } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { enterApi } from "@/features";

type CardProps = {
  waiting?: boolean;
};
const Card = ({ waiting }: CardProps) => {
  const [trigger, setTrigger] = React.useState(false);
  const router = useRouter();
  const { data, error } = useQuery({
    queryKey: ["waiting"],
    queryFn: enterApi,
    enabled: trigger,
  });

  useEffect(() => {
    console.log(data);
    if (!!data) {
      console.log("data" + data);

      router.push(`/waiting?id=${data.order}`);
    }
    return;
  }, [data]);

  const handleRouting = () => {
    if (waiting) {
      setTrigger(true);
    } else {
      router.push(`/waiting/1`);
    }
  };
  const [hydrated, setHydrated] = React.useState(false);
  React.useEffect(() => {
    setHydrated(true);
  }, []);
  if (!hydrated) {
    // Returns null on first render, so the client and server match
    return null;
  }

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
            onClick={() => handleRouting()}
          >
            구매하기
          </button>
        </div>
      </div>
    </div>
  );
};

export default Card;
