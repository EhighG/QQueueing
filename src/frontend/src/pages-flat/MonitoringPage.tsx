import { Button, PerformanceCard, SectionTitle } from "@/shared";
import React from "react";

const MonitoringPage = () => {
  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md">
      <SectionTitle title="모니터링" />
      <div className="flex items-center flex-1 justify-around">
        <PerformanceCard title="활성 중" perform={1} />
        <PerformanceCard title="활성 중" perform={2} />
        <PerformanceCard title="활성 중" perform={3} />
      </div>
      <div className="flex w-full justify-center items-center h-[60px] hover:opacity-80">
        <Button className="text-[1.5rem] border rounded-full px-10 py-2 bg-blue-500 text-white font-bold">
          자세히 보기
        </Button>
      </div>
    </div>
  );
};

export default MonitoringPage;
