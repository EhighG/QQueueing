"use client";
import { PerformanceCard, SectionTitle } from "@/shared";
import React from "react";
import { Button } from "@/shared";
import { Gauge, gaugeClasses } from "@mui/x-charts";
const ResourcePage = () => {
  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md">
      <SectionTitle title="리소스" />
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
      <div className="flex w-full justify-center items-center h-[60px]">
        <Button>자세히 보기</Button>
      </div>
    </div>
  );
};

export default ResourcePage;
