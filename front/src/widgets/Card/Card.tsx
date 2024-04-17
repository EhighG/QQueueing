import React from "react";
import Image from "next/image";
import { cats } from "@/shared";

const Card = () => {
  return (
    <div className="flex flex-col w-full">
      <div>
        <div>
          <Image src={cats} alt="cats" />
        </div>
        <div>
          <div className="flex w-full justify-between">
            <p>뮤지컬 캣츠</p>
            <p>품절 임박</p>
          </div>
          <p>재고량 10장!</p>
        </div>
      </div>
    </div>
  );
};

export default Card;
