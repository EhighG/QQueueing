import React from "react";

const NavMenu = () => {
  return (
    <nav className="w-[20%] max-w-[340px] h-full rounded-r-md border-r-2 border-black shadow-sm">
      <dl className="flex flex-col ml-[10px]">
        <dt className="text-[2rem] font-bold">대기열 관리</dt>
        <dd>대기열 리스트</dd>
        <dd>등록 하기</dd>
        <dd>대기열 상태 관리</dd>
      </dl>
      <dl className="flex flex-col ml-[10px]">
        <dt className="text-[2rem] font-bold">QQueueing</dt>
        <dd>QQueueing</dd>
        <dd>DOCS</dd>
        <dd>Example</dd>
        <dd>GitHub</dd>
        <dd>License</dd>
        <dd>Contributing</dd>
      </dl>
    </nav>
  );
};

export default NavMenu;
