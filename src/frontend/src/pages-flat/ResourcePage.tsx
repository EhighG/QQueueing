"use client";
import { PerformanceCard, SectionTitle, Button } from "@/shared";
import React, { useEffect, useState } from "react";
import { Gauge, gaugeClasses } from "@mui/x-charts";
import {
  useGetSystemCpuUsage,
  useGetJvmMemoryMax,
  useGetProcessCpuUsage,
  useGetJvmMemoryUsed,
  useGetRequestCount,
} from "@/features";
import { useRouter } from "next/navigation";

const ResourcePage = () => {
  const { data: systemCpuUsage } = useGetSystemCpuUsage();
  const { data: processCpuUsage } = useGetProcessCpuUsage();
  const { data: jvmMemoryMax } = useGetJvmMemoryMax();
  const { data: jvmMemoryUsed } = useGetJvmMemoryUsed();
  const { data: requestCount } = useGetRequestCount();

  const router = useRouter();
  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md">
      <SectionTitle title="큐잉 서버" />
      <div className="flex items-center flex-1 justify-around">
        <PerformanceCard title="시스템 CPU">
          <Gauge
            width={240}
            height={240}
            value={systemCpuUsage ? Math.ceil(systemCpuUsage.value * 100) : 0}
            sx={{
              [`& .${gaugeClasses.valueText}`]: {
                fontSize: 40,
                transform: "translate(0px, 0px)",
              },
              [`& .${gaugeClasses.valueArc}`]: {
                fill: "#52b202",
              },
            }}
            text={({ value, valueMax }) => `${value} / ${valueMax}%`}
          />
        </PerformanceCard>

        <PerformanceCard title="5초당 HTTP 요청량">
          <Gauge
            width={240}
            height={240}
            value={
              requestCount?.tomcatRequestCount
                ? parseInt(requestCount.tomcatRequestCount)
                : 0
            }
            sx={{
              [`& .${gaugeClasses.valueText}`]: {
                fontSize: 40,
                transform: "translate(0px, 0px)",
              },
              [`& .${gaugeClasses.valueArc}`]: {
                fill: "#52b202",
              },
            }}
            text={({ value }) => `${value}회`}
          />
        </PerformanceCard>
        <PerformanceCard title="JVM 메모리">
          <Gauge
            width={240}
            height={240}
            value={
              jvmMemoryUsed
                ? Math.round((jvmMemoryUsed.value / 1024 / 1024 / 1024) * 100) /
                  100
                : 0
            }
            valueMax={
              jvmMemoryMax
                ? Math.round((jvmMemoryMax.value / 1024 / 1024 / 1024) * 100) /
                  100
                : 0
            }
            sx={{
              [`& .${gaugeClasses.valueText}`]: {
                fontSize: 40,
                transform: "translate(0px, 0px)",
              },
              [`& .${gaugeClasses.valueArc}`]: {
                fill: "#52b202",
              },
            }}
            text={({ value, valueMax }) => `${value} / ${valueMax}GB`}
          />
        </PerformanceCard>
      </div>
      <div className="flex w-full justify-center items-center h-[60px]">
        <Button
          onClick={() => {
            router.push("/resource");
          }}
        >
          자세히 보기
        </Button>
      </div>
    </div>
  );
};

export default ResourcePage;
