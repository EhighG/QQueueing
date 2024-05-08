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

  useEffect(() => {
    // 데이터 패칭 로딩
    if (serverLogs) setData((prev) => [...prev, serverLogs]);
    console.log(serverLogs);
  }, [serverLogs]);

  const formatBytesToGB = (bytes: number) =>
    (bytes / 1024 / 1024 / 1024).toFixed(2) + " GB";
  const formatTime = (second: number) => `${second}초`;

  return (
    <>
      {/* 현재 CPU 사용량 */}
      <ResponsiveContainer width="100%" height="100%">
        <LineChart width={500} height={500} data={data}>
          <YAxis dataKey="cpuUsageRate" />
          <XAxis tickFormatter={(value, index) => `${(index + 1) * 5}s`} />
          <Tooltip />
          <Legend />
          <CartesianGrid strokeDasharray="3 3" />
          <Line type="monotone" dataKey="cpuUsageRate" />
        </LineChart>
      </ResponsiveContainer>
      {/* 메모리 사용 가능량 */}
      <ResponsiveContainer width="100%" height="100%">
        <LineChart width={500} height={500} data={data}>
          <YAxis
            dataKey="nodeMemoryMemAvailableBytes"
            tickFormatter={formatBytesToGB}
          />
          <XAxis tickFormatter={(value, index) => `${(index + 1) * 5}s`} />
          <Tooltip />
          <Legend />
          <CartesianGrid strokeDasharray="3 3" />
          <Line type="monotone" dataKey="nodeMemoryMemAvailableBytes" />
        </LineChart>
      </ResponsiveContainer>
      {/* 디스크 사용 가능량 */}
      <ResponsiveContainer width="100%" height="100%">
        <LineChart width={500} height={500} data={data}>
          <YAxis dataKey="diskAvailableBytes" tickFormatter={formatBytesToGB} />
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
