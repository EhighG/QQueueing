"use client";
import { PerformanceCard, SectionTitle } from "@/shared";
import React, { useEffect, useState } from "react";
import { Button } from "@/shared";
import { Gauge, gaugeClasses } from "@mui/x-charts";
import {
  useQueries,
  useQuery,
  useSuspenseQueries,
} from "@tanstack/react-query";
import {
  getCpuUsage,
  getDiskFree,
  getDiskTotal,
  getJvmMemoryMax,
  getJvmMemoryUsed,
} from "@/features";

const ResourcePage = () => {
  const { data: cpuUsage } = useQuery({
    queryKey: ["cpu"],
    queryFn: getCpuUsage,
    gcTime: 0,
    refetchInterval: 5000,
    select: (data) => data.measurements,
  });

  const { data: diskTotal } = useQuery({
    queryKey: ["disk_total"],
    queryFn: getDiskTotal,
    gcTime: 0,
    refetchInterval: 5000,
    select: (data) => data.measurements,
  });

  const { data: diskFree } = useQuery({
    queryKey: ["disk_free"],
    queryFn: getDiskFree,
    gcTime: 0,
    refetchInterval: 5000,
    select: (data) => data.measurements,
  });

  const { data: JvmTotal } = useQuery({
    queryKey: ["jvm_total"],
    queryFn: getJvmMemoryMax,
    gcTime: 0,
    refetchInterval: 5000,
    select: (data) => data.measurements,
  });

  const { data: JvmUsed } = useQuery({
    queryKey: ["jvm_free"],
    queryFn: getJvmMemoryUsed,
    gcTime: 0,
    refetchInterval: 5000,
    select: (data) => data.measurements,
  });

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
              JvmUsed
                ? Math.round((JvmUsed[0].value / 1024 / 1024 / 1024) * 100) /
                  100
                : 0
            }
            valueMax={
              JvmTotal
                ? Math.round((JvmTotal[0].value / 1024 / 1024 / 1024) * 100) /
                  100
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
