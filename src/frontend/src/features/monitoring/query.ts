import { UseQueryOptions, useQueries, useQuery } from "@tanstack/react-query";
import { AxiosError, AxiosResponse } from "axios";
import {
  DiskFreeType,
  DiskTotalType,
  JvmMemoryMaxType,
  JvmMemoryUsedType,
  Measurement,
  ProcessCpuUsageType,
  ServerLogsType,
  SystemCpuUsageType,
  requestCountType,
} from "./type";
import {
  getProcessCpuUsage,
  getDiskFree,
  getDiskTotal,
  getJvmMemoryMax,
  getJvmMemoryUsed,
  getServerLogs,
  getSystemCpuUsage,
  getRequestCount,
} from "./api";

const queryOptions = {
  gcTime: 0,
  refetchInterval: 1000,
};

const useGetServerLogs = (interval?: number) => {
  const { data, isFetching } = useQuery<
    ServerLogsType,
    AxiosError,
    ServerLogsType,
    [_1: string]
  >({
    queryKey: ["serverLogs"],
    queryFn: getServerLogs,
    ...queryOptions,
    refetchInterval: interval ? interval : 1000,
  });

  return { data, isFetching };
};

const useGetProcessCpuUsage = (interval?: number) => {
  const { data, isLoading, isError } = useQuery<
    ProcessCpuUsageType,
    AxiosError,
    Measurement,
    [_1: string]
  >({
    queryKey: ["cpu"],
    queryFn: getProcessCpuUsage,
    ...queryOptions,
    refetchInterval: interval ? interval : 1000,

    select: (data) => data.measurements[0],
  });

  return { data, isLoading, isError };
};

const useGetSystemCpuUsage = (interval?: number) => {
  const { data, isLoading, isError, isFetching } = useQuery<
    SystemCpuUsageType,
    AxiosError,
    Measurement,
    [_1: string]
  >({
    queryKey: ["cpu"],
    queryFn: getSystemCpuUsage,
    ...queryOptions,
    refetchInterval: interval ? interval : 1000,
    select: (data) => data.measurements[0],
  });

  return { data, isLoading, isError, isFetching };
};
const useGetDiskTotal = (interval?: number) => {
  const { data, isLoading, isError } = useQuery<
    DiskTotalType,
    AxiosError,
    Measurement,
    [_1: string]
  >({
    queryKey: ["disk_total"],
    queryFn: getDiskTotal,
    ...queryOptions,
    refetchInterval: interval ? interval : 1000,
    select: (data) => data.measurements[0],
  });

  return { data, isLoading, isError };
};

const useGetDiskFree = () => {
  const { data, isLoading, isError } = useQuery<
    DiskFreeType,
    AxiosError,
    Measurement,
    [_1: string]
  >({
    queryKey: ["getDiskFree"],
    queryFn: getDiskFree,
    ...queryOptions,
    select: (data) => data.measurements[0],
  });

  return { data, isLoading, isError };
};

const useGetJvmMemoryMax = (interval?: number) => {
  const { data, isLoading, isError } = useQuery<
    JvmMemoryMaxType,
    AxiosError,
    Measurement,
    [_1: string]
  >({
    queryKey: ["jvm_max"],
    queryFn: getJvmMemoryMax,
    ...queryOptions,
    refetchInterval: interval ? interval : 1000,
    select: (data) => data.measurements[0],
  });

  return { data, isLoading, isError };
};

const useGetJvmMemoryUsed = (interval?: number) => {
  const { data, isLoading, isError, isFetching } = useQuery<
    JvmMemoryUsedType,
    AxiosError,
    Measurement,
    [_1: string]
  >({
    queryKey: ["jvm_used"],
    queryFn: getJvmMemoryUsed,
    ...queryOptions,
    refetchInterval: interval ? interval : 1000,
    select: (data) => data.measurements[0],
  });

  return { data, isLoading, isError, isFetching };
};

const useGetRequestCount = (interval?: number) => {
  const { data, isLoading, isError, isSuccess, isFetching } = useQuery<
    requestCountType,
    AxiosError,
    requestCountType,
    [_1: string]
  >({
    queryKey: ["get_request_count"],
    queryFn: getRequestCount,
    ...queryOptions,
    refetchInterval: interval ? interval : 1000,
  });

  return { data, isLoading, isError, isSuccess, isFetching };
};

export {
  useGetProcessCpuUsage,
  useGetSystemCpuUsage,
  useGetDiskTotal,
  useGetDiskFree,
  useGetJvmMemoryMax,
  useGetJvmMemoryUsed,
  useGetServerLogs,
  useGetRequestCount,
};
