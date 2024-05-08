"use client";

import { Button, PerformanceCard, SectionTitle } from "@/shared";
import { Gauge, gaugeClasses } from "@mui/x-charts";
import { useRouter } from "next/navigation";
import React from "react";

const MonitoringPage = () => {
  const router = useRouter();
  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md">
      <SectionTitle title="모니터링" />
      <div className="flex items-center flex-1 justify-around">
        <PerformanceCard title="성능">
          <Gauge
            width={240}
            height={240}
            value={60}
            sx={{
              [`& .${gaugeClasses.valueText}`]: {
                fontSize: 40,
                transform: "translate(0px, 0px)",
              },
            }}
            text={({ value, valueMax }) => `${value} / ${valueMax}`}
          />
        </PerformanceCard>

        <PerformanceCard title="성능">
          <Gauge
            width={240}
            height={240}
            value={60}
            sx={{
              [`& .${gaugeClasses.valueText}`]: {
                fontSize: 40,
                transform: "translate(0px, 0px)",
              },
            }}
            text={({ value, valueMax }) => `${value} / ${valueMax}`}
          />
        </PerformanceCard>
        <PerformanceCard title="성능">
          <Gauge
            width={240}
            height={240}
            value={60}
            sx={{
              [`& .${gaugeClasses.valueText}`]: {
                fontSize: 40,
                transform: "translate(0px, 0px)",
              },
            }}
            text={({ value, valueMax }) => `${value} / ${valueMax}`}
          />
        </PerformanceCard>
      </div>
      <div className="flex w-full justify-center items-center h-[60px] hover:opacity-80">
        <Button
          className="text-[1.5rem] border rounded-full px-10 py-2 bg-blue-500 text-white font-bold"
          onClick={() => {
            router.push("/monitoring");
          }}
        >
          자세히 보기
        </Button>
      </div>
    </div>
  );
};

export default MonitoringPage;
