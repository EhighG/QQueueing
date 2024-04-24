import React from "react";
import { LinkButton } from "@/shared";
const NavMenu = () => {
  return (
    <nav className="max-2xl:w-[240px] w-[300px] h-full rounded-r-md border-r border-black shadow-sm">
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
          <dd>
            <LinkButton href="/about" title="about"></LinkButton>
          </dd>
          <dd>
            <LinkButton href="/docs" title="docs"></LinkButton>
          </dd>
          <dd>
            <LinkButton href="/example" title="example"></LinkButton>
          </dd>
          <dd>
            <LinkButton href="/github" title="github"></LinkButton>
          </dd>
          <dd>
            <LinkButton href="/license" title="License"></LinkButton>
          </dd>
          <dd>
            <LinkButton href="/contributing" title="Contributing"></LinkButton>
          </dd>
        </dl>
      </div>
    </nav>
  );
};

export default NavMenu;
