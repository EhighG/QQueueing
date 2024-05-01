import React from "react";

const Performance = () => {
  return (
    <div className="flex flex-1">
      <div className="flex flex-1 flex-col items-center justify-center gap-5 border-r border-black">
        <p className="text-[1.5rem] font-bold">최대 수용 가능 인원</p>
        <p className="text-[1.5rem] font-bold">200 명</p>
      </div>
      <div className="flex flex-1 flex-col items-center justify-center gap-5">
        <p className="text-[1.5rem] font-bold">1분 당 처리 가능 인원</p>
        <p className="text-[1.5rem] font-bold">200 명</p>
      </div>
    </div>
  );
};

export default Performance;
