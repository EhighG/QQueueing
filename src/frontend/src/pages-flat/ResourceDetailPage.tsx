"use client";
import {
  useGetDiskTotal,
  useGetJvmMemoryMax,
  useGetJvmMemoryUsed,
  useGetRequestCount,
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
  const { data: cpuUsage, isFetching: cpuFetching } = useGetSystemCpuUsage();
  const { data: diskTotal } = useGetDiskTotal();
  const { data: jvmMemoryMax } = useGetJvmMemoryMax();
  const { data: jvmMemoryUsed, isFetching: memoryFetching } =
    useGetJvmMemoryUsed();
  const { data: requestCount, isFetching: requestFetching } =
    useGetRequestCount();

  type logDataProps = {
    value: number;
  };

  const [cpuUsageData, setCpuUsageData] = useState<
    { id: number; cpuUsage: number }[]
  >([]);
  const [jvmMemoryUsedData, setJvmMemoryUsedData] = useState<
    { id: number; jvmMemoryUsed: number }[]
  >([]);
  const [requestCountData, setRequestCountData] = useState<
    { id: number; tomcatRequestCount: number }[]
  >([]);

  const formatBytesToGB = (byte: number) =>
    (byte / 1024 / 1024 / 1024).toFixed(2) + "GB";

  const formatPercents = (percent: number) => percent + "%";

  useEffect(() => {
    if (cpuFetching && cpuUsage)
      setCpuUsageData((prev) => [
        ...prev,
        { id: cpuUsageData.length + 1, cpuUsage: cpuUsage.value * 100 },
      ]);
  }, [cpuFetching, cpuUsage]);

  useEffect(() => {
    if (memoryFetching && jvmMemoryUsed) {
      setJvmMemoryUsedData((prev) => [
        ...prev,
        {
          id: jvmMemoryUsedData.length + 1,
          jvmMemoryUsed: jvmMemoryUsed.value,
        },
      ]);
    }
  }, [memoryFetching, jvmMemoryUsed]);

  useEffect(() => {
    if (requestFetching && requestCount) {
      setRequestCountData((prev) => [
        ...prev,
        {
          id: requestCountData.length + 1,
          tomcatRequestCount: parseInt(requestCount.tomcatRequestCount),
        },
      ]);
    }
  }, [requestFetching, requestCount]);

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
          <XAxis tickFormatter={(id, index) => `${id * 5}s`} />
          <Tooltip />
          <Legend />
          <CartesianGrid strokeDasharray="3 3" />
          <Line type="monotone" dataKey="cpuUsage" name="cpu 사용률" />
        </LineChart>
      </ResponsiveContainer>
      {/* HTTP 요청량 */}
      <ResponsiveContainer
        width="100%"
        height="100%"
        className="bg-white p-4 shadow-md rounded-md"
      >
        <LineChart width={500} height={600} data={requestCountData}>
          <YAxis dataKey="tomcatRequestCount" width={80} />
          <XAxis tickFormatter={(id, index) => `${id * 5}s`} />
          <Tooltip />
          <Legend />
          <CartesianGrid strokeDasharray="3 3" />
          <Line
            type="monotone"
            dataKey="tomcatRequestCount"
            name="5초간 HTTP 호출량"
          />
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
          <XAxis tickFormatter={(id, index) => `${id * 5}s`} />
          <Tooltip />
          <Legend />
          <CartesianGrid strokeDasharray="3 3" />
          <Line
            type="monotone"
            dataKey="jvmMemoryUsed"
            name="jvm 메모리 사용량"
          />
        </LineChart>
      </ResponsiveContainer>
    </>
  );
};

export default ResourceDetailPage;
