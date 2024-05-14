"use client";
import { ServerLogsType } from "@/features/monitoring";
import { useGetServerLogs } from "@/features/monitoring/query";
import { Button, PerformanceCard, SectionTitle } from "@/shared";
import { Gauge, gaugeClasses } from "@mui/x-charts";
import { useRouter } from "next/navigation";
import React, { useEffect, useState } from "react";

const MonitoringPage = () => {
  const { data: serverLogs } = useGetServerLogs();
  const [data, setData] = useState<ServerLogsType>();

  useEffect(() => {
    // 데이터 패칭 로딩
    if (serverLogs) setData(serverLogs);
  }, [serverLogs]);

  const router = useRouter();
  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md">
      <SectionTitle title="사용자 서버" />
      <div className="flex items-center flex-1 justify-around">
        <PerformanceCard title="CPU 사용량">
          <Gauge
            width={240}
            height={240}
            value={serverLogs ? parseInt(serverLogs.cpuUsageRate) : 0}
            valueMax={100}
            sx={{
              [`& .${gaugeClasses.valueText}`]: {
                fontSize: 40,
                transform: "translate(0px, 0px)",
              },
            }}
            text={({ value, valueMax }) => `${value} / ${valueMax}%`}
          />
        </PerformanceCard>

        <PerformanceCard title="디스크 공간">
          <Gauge
            width={240}
            height={240}
            valueMax={
              serverLogs
                ? Math.round(
                    parseInt(serverLogs.diskAllBytes) / 1024 / 1024 / 1024
                  )
                : 0
            }
            value={
              serverLogs
                ? Math.round(
                    (parseInt(serverLogs.diskAllBytes) -
                      parseInt(serverLogs.diskAvailableBytes)) /
                      1024 /
                      1024 /
                      1024
                  )
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
        <PerformanceCard title="메모리">
          <Gauge
            width={240}
            height={240}
            valueMax={
              serverLogs
                ? Math.round(
                    (parseInt(serverLogs.memoryAllBytes) / 1024 / 1024 / 1024) *
                      100
                  ) / 100
                : 0
            }
            value={
              serverLogs
                ? Math.round(
                    ((parseInt(serverLogs.memoryAllBytes) -
                      parseInt(serverLogs.nodeMemoryMemAvailableBytes)) /
                      1024 /
                      1024 /
                      1024) *
                      100
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
