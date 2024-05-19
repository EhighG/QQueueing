import { SectionTitle, logo } from "@/shared";
import Image from "next/image";
import React from "react";

const AboutPage = () => {
  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md border">
      <SectionTitle title="About" />

      <div className="flex flex-1 flex-col gap-10 w-full h-full p-3">
        <div className="flex items-center">
          <div className="size-[300px]">
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
        <div className="flex flex-1 flex-col gap-5">
          <h1 className="text-[2rem] font-bold self-center">
            서비스 주요 기능
          </h1>
          <div className="flex-1 grid grid-cols-3 place-items-center  gap-5">
            <div className="flex flex-col size-[300px]   p-5 justify-center ">
              <div className="size-[200px] self-center">
                <Image
                  src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Inbox%20Tray.png"
                  alt="Inbox Tray"
                  width={500}
                  height={500}
                />
              </div>
              <p className="text-center font-bold text-[1.2rem]">
                간편 다운로드, 설치
              </p>
            </div>
            <div className="flex flex-col size-[300px]   p-5 justify-center ">
              <div className="size-[200px] self-center">
                <Image
                  src={logo}
                  alt="logo"
                  width={500}
                  height={500}
                  className="animate-shaking"
                />
              </div>
              <p className="text-center font-bold text-[1.2rem]">
                바로 적용 가능한 대기열
              </p>
            </div>
            <div className="flex flex-col size-[300px]   p-5">
              <div className="size-[200px] self-center">
                <Image
                  src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Control%20Knobs.png"
                  alt="Control Knobs"
                  width={500}
                  height={500}
                />
              </div>
              <p className="text-center font-bold text-[1.2rem]">
                대기열 타깃 url 설정 <br /> 활성/비활성화 기능
              </p>
            </div>
            <div className="flex flex-col size-[300px]   p-5">
              <div className="size-[200px] self-center">
                <Image
                  src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Desktop%20Computer.png"
                  alt="Desktop Computer"
                  width={500}
                  height={500}
                />
              </div>
              <p className="text-center font-bold text-[1.2rem]">
                타깃 url 별 <br />
                대기 및 통과 현황 모니터링
              </p>
            </div>
            <div className="flex flex-col size-[300px] p-5">
              <div className="size-[200px] self-center">
                <Image
                  src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Bar%20Chart.png"
                  alt="Bar Chart"
                  width={500}
                  height={500}
                />
              </div>
              <p className="text-center font-bold text-[1.2rem]">
                운영자 컴퓨팅 자원 <br /> 대기열 서버 모니터링 제공
              </p>
            </div>
            <div className="flex flex-col   size-[300px] p-5">
              <div className="size-[200px] self-center">
                <Image
                  src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Mobile%20Phone.png"
                  alt="Mobile Phone"
                  width={500}
                  height={500}
                />
              </div>
              <p className="text-center font-bold text-[1.2rem]">
                데스크톱, 모바일 <br /> 대기열 페이지 지원
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AboutPage;
