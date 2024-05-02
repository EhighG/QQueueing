"use client";
import { PerformanceCard, SectionTitle } from "@/shared";
import React, { useEffect, useState } from "react";
import { Button } from "@/shared";
import { Gauge, gaugeClasses } from "@mui/x-charts";
import {
  useGetSystemCpuUsage,
  useGetDiskFree,
  useGetDiskTotal,
  useGetJvmMemoryMax,
} from "@/features/monitoring/query";
import { useGetJvmMemoryUsed } from "../features/monitoring/query";

const ResourcePage = () => {
  const { data: cpuUsage } = useGetSystemCpuUsage();
  const { data: diskTotal } = useGetDiskTotal();
  const { data: diskFree } = useGetDiskFree();
  const { data: jvmMemoryMax } = useGetJvmMemoryMax();
  const { data: jvmMemoryUsed } = useGetJvmMemoryUsed();

  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md">
      <SectionTitle title="리소스" />
      <div className="flex items-center flex-1 justify-around">
        <PerformanceCard title="CPU 사용량">
          <Gauge
            width={240}
            height={240}
            value={cpuUsage ? Math.ceil(cpuUsage[0].value * 100) : 0}
            sx={{
              [`& .${gaugeClasses.valueText}`]: {
                fontSize: 40,
                transform: "translate(0px, 0px)",
              },
            }}
            text={({ value, valueMax }) => `${value} / ${valueMax}`}
          />
        </PerformanceCard>

        <PerformanceCard title="디스크">
          <Gauge
            width={240}
            height={240}
            value={
              diskFree && diskTotal
                ? Math.ceil(diskTotal[0].value / 1024 / 1024 / 1024) -
                  Math.ceil(diskFree[0].value / 1024 / 1024 / 1024)
                : 0
            }
            valueMax={
              diskTotal
                ? Math.floor(diskTotal[0].value / 1024 / 1024 / 1024)
                : 0
            }
            sx={{
              [`& .${gaugeClasses.valueText}`]: {
                fontSize: 40,
                transform: "translate(0px, 0px)",
              },
            }}
            text={({ value, valueMax }) => `${value} / ${valueMax}GB`}
          />
        </PerformanceCard>
        <PerformanceCard title="가상 메모리">
          <Gauge
            width={240}
            height={240}
            value={
              jvmMemoryUsed
                ? Math.round(
                    (jvmMemoryUsed[0].value / 1024 / 1024 / 1024) * 100
                  ) / 100
                : 0
            }
            valueMax={
              jvmMemoryMax
                ? Math.round(
                    (jvmMemoryMax[0].value / 1024 / 1024 / 1024) * 100
                  ) / 100
                : 0
            }
            sx={{
              [`& .${gaugeClasses.valueText}`]: {
                fontSize: 40,
                transform: "translate(0px, 0px)",
              },
            }}
            text={({ value, valueMax }) => `${value} / ${valueMax}GB`}
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
