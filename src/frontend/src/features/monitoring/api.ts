import { WaitingListType } from "@/entities/waitingList/type";
import { monitoringInstance } from "@/shared";
import { AxiosInstance, AxiosResponse } from "axios";
import {
  DiskFreeType,
  DiskTotalType,
  HttpServerRequestsType,
  JvmMemoryMaxType,
  JvmMemoryUsedType,
  ProcessCpuUsageType,
  ServerLogsType,
  SystemCpuUsageType,
  VirtualThreadType,
} from "./type";
import { ResponseType } from "..";

const instance: AxiosInstance = monitoringInstance();

const getServerLogs = async () => {
  return await instance
    .get<ResponseType<ServerLogsType>>("/logs")
    .then(({ data }) => data.result);
};

const getVirtualThread = async (): Promise<VirtualThreadType> => {
  return await instance
    .get<VirtualThreadType>("/monitoring/virtual-thread-metrics")
    .then(({ data }) => data);
};

const getDiskTotal = async (): Promise<DiskTotalType> => {
  return await instance
    .get<DiskTotalType>("/monitoring/metrics/disk.total")
    .then(({ data }) => data);
};
const getDiskFree = async (): Promise<DiskFreeType> => {
  return await instance
    .get<DiskFreeType>("/monitoring/metrics/disk.free")
    .then(({ data }) => data);
};

const getHttpServerRequests = async () => {
  return await instance
    .get<HttpServerRequestsType>("/monitoring/metrics/http.server.requests")
    .then(({ data }) => data);
};

const getJvmMemoryMax = async () => {
  return await instance
    .get<JvmMemoryMaxType>("/monitoring/metrics/jvm.memory.max")
    .then(({ data }) => data);
};

const getJvmMemoryUsed = async () => {
  return await instance
    .get<JvmMemoryUsedType>("/monitoring/metrics/jvm.memory.used")
    .then(({ data }) => data);
};

const getProcessCpuUsage = async () => {
  return await instance
    .get<ProcessCpuUsageType>("/monitoring/metrics/process.cpu.usage")
    .then(({ data }) => data);
};

const getSystemCpuUsage = async () => {
  return await instance
    .get<SystemCpuUsageType>("/monitoring/metrics/system.cpu.usage")
    .then(({ data }) => data);
};

export {
  getVirtualThread,
  getDiskTotal,
  getDiskFree,
  getHttpServerRequests,
  getJvmMemoryMax,
  getJvmMemoryUsed,
  getProcessCpuUsage,
  getSystemCpuUsage,
  getServerLogs,
};
