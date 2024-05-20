import {
  SectionTitle,
  example_2,
  example_3,
  example_4,
  example_5,
  example_6,
  example_7,
  example_8,
  example_9,
} from "@/shared";
import { example_1 } from "@/shared";
import Image from "next/image";
import React from "react";

const ExamplePage = () => {
  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md border">
      <SectionTitle title="Example" />
      <div className="flex flex-1 flex-col w-full h-full p-3">
        <h1 className="text-[1.5rem] font-bold">User&apos;s Guide</h1>
        <hr className="my-2" />
        <ol className="list-decimal ml-10 text-[1.2rem] font-bold">
          <li className="flex flex-col">
            대기열 애플리케이션을 동작 시킵니다
            <br />
            <Image
              src={example_1}
              alt="example"
              width={1000}
              height={1000}
              className="self-center"
            />
          </li>
          <li className="flex flex-col">
            등록하기 버튼을 누르고, 대기열을 적용할 URL, 서비스 명, 대기열 대표
            이미지를 등록합니다. <br />
            <Image
              src={example_2}
              alt="example"
              width={1000}
              height={1000}
              className="self-center"
            />
          </li>
          <li className="flex flex-col">
            대기열 리스트 버튼을 누르고, 대기열이 적용된 모습을 확인합니다.{" "}
            <br />
            <Image
              src={example_3}
              alt="example"
              width={1000}
              height={1000}
              className="self-center"
            />
          </li>
          <li className="flex flex-col">
            등록된 url을 클릭하여 상세 정보를 학인할 수 있고, 활성/비활성화 및
            설정을 변경할 수 있습니다. <br />
            <Image
              src={example_4}
              alt="example"
              width={1000}
              height={1000}
              className="self-center"
            />
          </li>
          <li className="flex flex-col">
            대시보드 버튼을 클릭하여 대기열이 적용된 운영자의 컴퓨팅 자원과,
            대기열 어플리케이션의 상태를 모니터링할 수 있습니다. <br />
            <Image
              src={example_5}
              alt="example"
              width={1000}
              height={1000}
              className="self-center"
            />
          </li>
        </ol>
        <hr className="my-2" />
        <h1 className="text-[1.5rem] font-bold">Run Screen</h1>
        <hr className="my-2" />
        <ol className="flex flex-col gap-5 list-desc">
          <li className="flex flex-col">
            <h1 className="text-[1.5rem] font-bold">PC Version</h1>
            <Image
              src={example_8}
              alt="example"
              width={1000}
              height={500}
              className="self-center"
            />
          </li>
          <li className="flex flex-col ">
            <h1 className="text-[1.5rem] font-bold">Mobile Version</h1>
            <Image
              src={example_9}
              alt="example"
              width={500}
              height={200}
              className="self-center"
            />
          </li>
        </ol>
      </div>
    </div>
  );
};

export default ExamplePage;
