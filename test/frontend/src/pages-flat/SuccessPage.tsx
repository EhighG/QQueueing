import { companyData } from "@/shared";
import Image from "next/image";
import React from "react";

type SuccessPageProps = {
  id: number;
};

const SuccessPage = ({ id }: SuccessPageProps) => {
  return (
    <div className="flex flex-col justify-center w-full">
      <div className="border border-black rounded-md p-10">
        <div className="flex w-[100px]">
          <Image src={companyData[id].src} alt={companyData[id].name} />
        </div>
        <h1 className="text-center text-[2rem] font-bold">
          {companyData[id].name}
        </h1>
        <h1 className="text-center text-[2rem] font-bold">
          2024년 삼성 소프트웨어 아카데미 특별 선착순 전형 결과 발표
        </h1>
        <h2 className="my-10 text-[1.5rem] font-bold text-center">
          {companyData[id].name} 에 지원하신 <br /> 이싸피 님의 전형 결과를 안내
          드립니다.
        </h2>
        <p className="text-[1.5rem] text-center">
          축하드립니다, 선착순 전형에 합격하셨습니다. <br />
          개인별 면접 일정을 아래와 같이 안내드리오니, <br />
          남은 시간동안 잘 준비하셔서 면접전형에서 좋은 결과가 있으시길
          바랍니다.
        </p>
        <p className="text-end">[수험 번호 : SSAFY-A401]</p>
        <p className="text-end">면접 일정 : 2024-05-21</p>
      </div>
    </div>
  );
};

export default SuccessPage;
