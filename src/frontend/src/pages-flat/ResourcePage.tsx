import { PerformanceCard, SectionTitle } from "@/shared";
import React from "react";
import { Button } from "@/shared";
const ResourcePage = () => {
  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md">
      <SectionTitle title="리소스" />
      <div className="flex items-center flex-1 justify-around">
        <PerformanceCard title="활성 중" perform={1} />
        <PerformanceCard title="활성 중" perform={2} />
        <PerformanceCard title="활성 중" perform={3} />
      </div>
      <div className="flex w-full justify-center items-center h-[60px]">
        <Button>자세히 보기</Button>
      </div>
    </div>
  );
};

export default ResourcePage;
