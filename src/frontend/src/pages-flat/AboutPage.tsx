import { SectionTitle, logo } from "@/shared";
import Image from "next/image";
import React from "react";

const AboutPage = () => {
  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md border">
      <SectionTitle title="About" />

      <div className="flex flex-col gap-10 w-full h-full items-center justify-center p-3">
        <div className="flex items-center">
          <div className="size-[400px]">
            <Image src={logo} alt="logo" width={500} height={500} />
          </div>
          <div className="flex flex-col ">
            <p className="max-[1700px]:text-[1.5rem] text-[2rem] font-bold">
              QQueueing은 서버에 직접 설치해서 사용할 수 있는 무료 대기열
              서비스입니다.
            </p>
            <p className="max-[1700px]:text-[1.5rem] text-[2rem]">
              서버 프로그램이 실행 중인 곳에 설치해서, 추가 비용 없이 대기열
              기능을 사용할 수 있습니다.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AboutPage;
