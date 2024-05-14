"use client";
import { Measurement, ServerLogsType } from "@/features/monitoring";
import {
  useGetDiskFree,
  useGetDiskTotal,
  useGetJvmMemoryMax,
  useGetJvmMemoryUsed,
  useGetServerLogs,
  useGetSystemCpuUsage,
} from "@/features/monitoring/query";
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

const ResourceDetailPage = () => {
  const { data: cpuUsage } = useGetSystemCpuUsage();
  const { data: diskTotal } = useGetDiskTotal();
  const { data: diskFree } = useGetDiskFree();
  const { data: jvmMemoryMax } = useGetJvmMemoryMax();
  const { data: jvmMemoryUsed } = useGetJvmMemoryUsed();

  type logDataProps = {
    value: number;
  };

  const [cpuUsageData, setCpuUsageData] = useState<{ cpuUsage: number }[]>([]);
  const [diskFreeData, setDiskFreeData] = useState<{ diskFree: number }[]>([]);
  const [jvmMemoryUsedData, setJvmMemoryUsedData] = useState<
    { jvmMemoryUsed: number }[]
  >([]);

  const formatBytesToGB = (byte: number) =>
    (byte / 1024 / 1024 / 1024).toFixed(2) + "GB";

  const formatPercents = (percent: number) => percent + "%";

  useEffect(() => {
    if (cpuUsage)
      setCpuUsageData((prev) => [...prev, { cpuUsage: cpuUsage.value * 100 }]);
  }, [cpuUsage]);

  useEffect(() => {
    if (diskFree) {
      setDiskFreeData((prev) => [
        ...prev,
        {
          diskFree: diskFree.value,
        },
      ]);
    }
  }, [diskFree]);

  useEffect(() => {
    if (jvmMemoryUsed) {
      setJvmMemoryUsedData((prev) => [
        ...prev,
        {
          jvmMemoryUsed: jvmMemoryUsed.value,
        },
      ]);
    }
  }, [jvmMemoryUsed]);

  return (
    <>
      {/* 현재 CPU 사용량 */}
      <ResponsiveContainer
        width="100%"
        height="100%"
        className="bg-white p-4 shadow-md rounded-md"
      >
        <LineChart width={500} height={500} data={cpuUsageData}>
          <YAxis
            dataKey="value"
            domain={[0, 100]}
            tickFormatter={formatPercents}
            width={80}
          />
          <XAxis tickFormatter={(value, index) => `${(index + 1) * 5}s`} />
          <Tooltip />
          <Legend />
          <CartesianGrid strokeDasharray="3 3" />
          <Line type="monotone" dataKey="cpuUsage" />
        </LineChart>
      </ResponsiveContainer>
      {/* 메모리 사용 가능량 */}
      <ResponsiveContainer
        width="100%"
        height="100%"
        className="bg-white p-4 shadow-md rounded-md"
      >
        <LineChart width={500} height={500} data={jvmMemoryUsedData}>
          <YAxis
            dataKey="value"
            tickFormatter={formatBytesToGB}
            domain={[0, jvmMemoryMax?.value ?? 0]}
            width={80}
          />
          <XAxis
            dataKey="value"
            tickFormatter={(value, index) => `${(index + 1) * 5}s`}
          />
          <Tooltip />
          <Legend />
          <CartesianGrid strokeDasharray="3 3" />
          <Line type="monotone" dataKey="jvmMemoryUsed" />
        </LineChart>
      </ResponsiveContainer>
      {/* 디스크 사용 가능량 */}
      <ResponsiveContainer
        width="100%"
        height="100%"
        className="bg-white p-4 shadow-md rounded-md"
      >
        <LineChart width={500} height={600} data={diskFreeData}>
          <YAxis
            dataKey="value"
            tickFormatter={formatBytesToGB}
            domain={[0, diskTotal?.value ?? 0]}
            width={80}
          />
          <XAxis tickFormatter={(value, index) => `${(index + 1) * 5}s`} />
          <Tooltip />
          <Legend />
          <CartesianGrid strokeDasharray="3 3" />
          <Line type="monotone" dataKey="diskFree" />
        </LineChart>
      </ResponsiveContainer>
    </>
  );
};

export default ResourceDetailPage;
