import React from "react";

const ResourcePage = () => {
  return (
    <div className="flex flex-col flex-1 gap-2">
      <div className="flex w-full items-center border-b border-black h-[60px] p-3">
        <p className="text-[1.5rem] font-bold">리소스</p>{" "}
      </div>
      <div className="flex items-center flex-1 justify-around">
        <div className="flex flex-col items-center justify-center gap-5 size-[240px] bg-slate-200">
          <h1 className="text-[2rem] font-bold">활성 중</h1>
          <p className="text-[2rem] font-bold">1</p>
        </div>
        <div className="flex flex-col items-center justify-center gap-5 size-[240px] bg-slate-200">
          <h1 className="text-[2rem] font-bold">활성 중</h1>
          <p className="text-[2rem] font-bold">2</p>
        </div>
        <div className="flex flex-col items-center justify-center gap-5 size-[240px] bg-slate-200">
          <h1 className="text-[2rem] font-bold">활성 중</h1>
          <p className="text-[2rem] font-bold">3</p>
        </div>
      </div>
      <div className="flex w-full h-[60px] border-t border-t-black"></div>
    </div>
  );
};

export default ResourcePage;
