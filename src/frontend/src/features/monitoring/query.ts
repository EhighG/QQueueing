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
} from "./type";
import {
  getProcessCpuUsage,
  getDiskFree,
  getDiskTotal,
  getJvmMemoryMax,
  getJvmMemoryUsed,
  getServerLogs,
} from "./api";

const queryOptions = {
  gcTime: 0,
  refetchInterval: 2000,
};

const useGetServerLogs = () => {
  const { data } = useQuery<
    ServerLogsType,
    AxiosError,
    ServerLogsType,
    [_1: string]
  >({
    queryKey: ["serverLogs"],
    queryFn: getServerLogs,
    ...queryOptions,
    refetchInterval: 5000,
  });

  return { data };
};

const useGetProcessCpuUsage = () => {
  const { data, isLoading, isError } = useQuery<
    ProcessCpuUsageType,
    AxiosError,
    Measurement[],
    [_1: string]
  >({
    queryKey: ["cpu"],
    queryFn: getProcessCpuUsage,
    ...queryOptions,
    select: (data) => data.measurements,
  });

  return { data, isLoading, isError };
};

const useGetSystemCpuUsage = () => {
  const { data, isLoading, isError } = useQuery<
    ProcessCpuUsageType,
    AxiosError,
    Measurement[],
    [_1: string]
  >({
    queryKey: ["cpu"],
    queryFn: getProcessCpuUsage,
    ...queryOptions,
    select: (data) => data.measurements,
  });

  return { data, isLoading, isError };
};
const useGetDiskTotal = () => {
  const { data, isLoading, isError } = useQuery<
    DiskTotalType,
    AxiosError,
    Measurement[],
    [_1: string]
  >({
    queryKey: ["disk_total"],
    queryFn: getDiskTotal,
    ...queryOptions,
    select: (data) => data.measurements,
  });

  return { data, isLoading, isError };
};

const useGetDiskFree = () => {
  const { data, isLoading, isError } = useQuery<
    DiskFreeType,
    AxiosError,
    Measurement[],
    [_1: string]
  >({
    queryKey: ["getDiskFree"],
    queryFn: getDiskFree,
    ...queryOptions,
    select: (data) => data.measurements,
  });

  return { data, isLoading, isError };
};

const useGetJvmMemoryMax = () => {
  const { data, isLoading, isError } = useQuery<
    JvmMemoryMaxType,
    AxiosError,
    Measurement[],
    [_1: string]
  >({
    queryKey: ["jvm_max"],
    queryFn: getJvmMemoryMax,
    ...queryOptions,
    select: (data) => data.measurements,
  });

  return { data, isLoading, isError };
};

const useGetJvmMemoryUsed = () => {
  const { data, isLoading, isError } = useQuery<
    JvmMemoryUsedType,
    AxiosError,
    Measurement[],
    [_1: string]
  >({
    queryKey: ["jvm_used"],
    queryFn: getJvmMemoryUsed,
    ...queryOptions,
    select: (data) => data.measurements,
  });

  return { data, isLoading, isError };
};

export {
  useGetProcessCpuUsage,
  useGetSystemCpuUsage,
  useGetDiskTotal,
  useGetDiskFree,
  useGetJvmMemoryMax,
  useGetJvmMemoryUsed,
  useGetServerLogs,
};
