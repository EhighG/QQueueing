import {companyData, logo} from "@/shared";
import Image from "next/image";
import React from "react";

type SuccessPageProps = {
  id: number;
};

const SuccessPage = ({ id }: SuccessPageProps) => {
  return (
      <div className="relative flex flex-col justify-center w-full p-10">
        <div className="absolute size-[250px] opacity-20 self-center">
          <Image src={companyData[id].src} alt="logo" width={500} height={500}/>
        </div>
        <div className="relative border rounded-md p-5 gap-10 shadow-md">

          <div className="flex w-[100px]">
            <Image src={companyData[id].src} alt={companyData[id].name}/>
          </div>
          <h1 className="text-center max-md:text-[1.5rem] text-[2rem] font-bold">
            {companyData[id].name}
          </h1>
          <h1 className="text-center max-md:text-[1rem] text-[2rem] font-bold mt-2">
            2024년 <br/> 삼성 소프트웨어 아카데미 <br/> 특별 선착순 전형 결과 발표
          </h1>
          <h2 className="my-10 max-md:text-[1rem] text-[1.5rem] font-bold text-center text-black">
            {companyData[id].name} 기업에 지원하신 <br/> 이싸피 님의 <br/> 전형 결과를 안내
            드립니다.
          </h2>
          <section className="flex max-md:flex-col justify-center gap-1">
            <p className="max-md:text-[1rem] text-[1.5rem] text-center ">
              축하드립니다.
            </p><p className="max-md:text-[1rem] text-[1.5rem] text-center">선착순 전형에 합격하셨습니다.</p></section>
          <section className="flex max-md:flex-col justify-center gap-1"><p
              className="max-md:text-[1rem] text-[1.5rem] text-center">개인별 면접
            일정을</p>
            <p className="max-md:text-[1rem] text-[1.5rem] text-center">아래와 같이 안내드리오니,</p></section>
          <section className="flex max-md:flex-col justify-center gap-1"><p
              className="max-md:text-[1rem] text-[1.5rem] text-center">남은 시간동안 잘
            준비하셔서</p>
          </section>
          <p className="max-md:text-[1rem] text-[1.5rem] text-center"> 좋은 결과가 있으시길
            바랍니다.</p>

          <div className="flex relative flex-col max-md:items-center mt-4">
            <div className="absolute size-[80px] opacity-40 left-0">
              <Image src={companyData[id].src} alt="logo"/>
            </div>
            <p className="text-end">[수험 번호 : SSAFY-A401]</p>
            <p className="text-end">면접 일정 : 2024-05-21</p>
          </div>
        </div>
      </div>
  );
};

export default SuccessPage;
