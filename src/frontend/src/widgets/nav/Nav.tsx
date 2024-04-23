import React from "react";
import { LinkButton } from "@/shared";
const NavMenu = () => {
  return (
    <nav className="w-[240px] h-full rounded-r-md border-r-2 border-black shadow-sm">
      <div className="flex flex-col h-full ml-[10px] gap-[100px]">
        <dl>
          <dt className="text-[2rem] font-bold">대기열 관리</dt>
          <dd>
            <LinkButton href="/dashboard" title="대시보드" />
          </dd>
          <dd>
            <LinkButton href="/list" title="대기열 리스트" />
          </dd>
          <dd>
            <LinkButton href="/regist" title="등록 하기" />
          </dd>
          <dd>
            <LinkButton href="/manage" title="대기열 상태 관리" />
          </dd>
        </dl>
        <dl>
          <dt className="text-[2rem] font-bold">QQueueing</dt>
          <dd>QQueueing</dd>
          <dd>DOCS</dd>
          <dd>Example</dd>
          <dd>GitHub</dd>
          <dd>License</dd>
          <dd>Contributing</dd>
        </dl>
      </div>
    </nav>
  );
};

export default NavMenu;
