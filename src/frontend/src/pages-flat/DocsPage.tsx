import { SectionTitle } from "@/shared";
import { q_docs2 } from "@/shared";
import Image from "next/image";
import React from "react";

const DocsPage = () => {
  return (
    <div className="flex flex-col flex-1 bg-white rounded-md border">
      <SectionTitle title="Docs" />
      <div className="flex flex-1 flex-col w-full h-full p-3">
        <h1 className="text-[1.5rem] font-bold">Getting Started</h1>
        <hr className="my-2" />
        <h2 className="text-[1.2rem] font-bold">구동 환경</h2>
        <ul className="list-disc ml-5 p-2 font-bold">
          <li>도커, 도커 컴포즈(2.17이상), 파이썬3(3.8이상)</li>
          <li>nginx 리버스 프록시 서버를 띄우고 있는 상황에서 동작합니다.</li>
          <li>nginx 서버는 컨테이너 런타임을 통해 실행되고 있어야 합니다.</li>
          <li>
            기본 웹 통신인 80, 443 포트를 사용하고 있는 상황에서 동작합니다.
          </li>
          <li>추후 개발을 통해 확장성을 갖출 예정입니다.</li>
        </ul>
        <hr className="my-2" />
        <h1 className="text-[1.5rem] font-bold">Terminal Settings</h1>
        <hr className="my-2" />
        <ol className="flex flex-col gap-5 list-decimal ml-5 p-2 font-bold">
          <li>
            레포지토리를 클론 받은 후, 해당 레포지토리로 이동합니다. <br />
            <code className="p-2">
              git clone https://lab.ssafy.com/s10-final/S10P31A401.git cd
              S10P31A401
            </code>
          </li>
          <li>`qqueueing.sh`를 통해 서비스를 조작할 수 있습니다.</li>
          <Image src={q_docs2} alt="docs2" width={500} height={500} />
          <li>
            `install`을 통해 대기열 시스템 초기 설정을 진행합니다. <br />
            <code className="p-2">./qqueueing.sh install</code>
            <ul className="list-disc">
              <li>
                이때 입력된 사항은 .env 파일로 저장되며, 이 파일을 직접적으로
                수정할 수도 있습니다.
              </li>
              <li>
                .env 파일을 수정하면 반드시 다시 install을 진행해주십시오.
              </li>
            </ul>
          </li>
          <li>
            설정이 완료되면 `start`를 통해 애플리케이션을 실행합니다.
            <br />
            <code className="p-2">./qqueueing.sh start</code>
            <ul className="list-disc">
              <li>
                .env 파일에 명시된 포트(기본 3001)로 어드민 페이지에 접근하여
                GUI를 통한 설정을 진행할 수 있습니다.
              </li>
            </ul>
          </li>
          <li>
            대기열 시스템을 중지하고 싶을 때는 `stop`을 입력합니다. <br />
            <code className="p-2">./qqueueing.sh stop</code>
          </li>
        </ol>
      </div>
    </div>
  );
};

export default DocsPage;
