"use client";
import { ServerLogsType } from "@/features/monitoring";
import { useGetServerLogs } from "@/features/monitoring/query";
import React from "react";
import { useEffect, useState } from "react";
import {
  BarChart,
  Bar,
  Line,
  LineChart,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ResponsiveContainer,
  Legend,
} from "recharts";

const LineChartPage = () => {
  const { data: serverLogs } = useGetServerLogs();
  const [data, setData] = useState<ServerLogsType[]>([]);
  const [memoryMax, setMemoryMax] = useState<number>(0);
  const [diskMax, setDiskMax] = useState<number>(0);

  useEffect(() => {
    // 데이터 패칭 로딩
    if (serverLogs) {
      setData((prev) => [...prev, serverLogs]);
      if (!memoryMax || !diskMax) {
        setMemoryMax(parseInt(serverLogs.memoryAllBytes));
        setDiskMax(parseInt(serverLogs.diskAllBytes));
      }
    }
  }, [serverLogs]);

  const formatBytesToGB = (bytes: number) =>
    (bytes / 1024 / 1024 / 1024).toFixed(2) + "GB";

  const formatPercents = (percent: number) => percent + "%";

  return (
    <>
      {/* 현재 CPU 사용량 */}
      <ResponsiveContainer
        width="100%"
        height="100%"
        className="bg-white p-2 shadow-md rounded-md"
      >
        <LineChart width={500} height={500} data={data}>
          <YAxis
            dataKey="cpuUsageRate"
            domain={[0, 100]}
            tickFormatter={formatPercents}
            width={80}
          />
          <XAxis tickFormatter={(value, index) => `${(index + 1) * 5}s`} />
          <Tooltip />
          <Legend />
          <CartesianGrid strokeDasharray="3 3" />
          <Line type="monotone" dataKey="cpuUsageRate" />
        </LineChart>
      </ResponsiveContainer>
      {/* 메모리 사용 가능량 */}
      <ResponsiveContainer
        width="100%"
        height="100%"
        className="bg-white p-4 shadow-md rounded-md"
      >
        <LineChart width={500} height={500} data={data}>
          <YAxis
            dataKey="nodeMemoryMemAvailableBytes"
            tickFormatter={formatBytesToGB}
            domain={[0, memoryMax]}
            width={80}
          />
          <XAxis tickFormatter={(value, index) => `${(index + 1) * 5}s`} />
          <Tooltip />
          <Legend />
          <CartesianGrid strokeDasharray="3 3" />
          <Line type="monotone" dataKey="nodeMemoryMemAvailableBytes" />
        </LineChart>
      </ResponsiveContainer>
      {/* 디스크 사용 가능량 */}
      <ResponsiveContainer
        width="100%"
        height="100%"
        className="bg-white p-4 shadow-md rounded-md"
      >
        <LineChart width={500} height={500} data={data}>
          <YAxis
            dataKey="diskAvailableBytes"
            tickFormatter={formatBytesToGB}
            domain={[0, diskMax]}
            width={80}
          />
          <XAxis tickFormatter={(value, index) => `${(index + 1) * 5}s`} />
          <Tooltip />
          <Legend />
          <CartesianGrid strokeDasharray="3 3" />
          <Line type="monotone" dataKey="diskAvailableBytes" />
        </LineChart>
      </ResponsiveContainer>
    </>
  );
};

export default LineChartPage;
