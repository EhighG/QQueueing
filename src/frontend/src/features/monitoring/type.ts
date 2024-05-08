type VirtualThreadType = {
  activeThreadCount: number;
  queuedTaskCount: number;
  parallelism: number;
  poolSize: number;
  runningThreadCount: number;
  stealCount: number;
  queuedSubmissionCount: number;
  asyncMode: boolean;
};

type DiskTotalType = {
  name: string;
  description: string;
  baseUnit: string;
  measurements: Measurement[];
  availableTags: AvailableTag[];
};

type DiskFreeType = {
  name: string;
  description: string;
  baseUnit: string;
  measurements: Measurement[];
  availableTags: AvailableTag[];
};

type HttpServerRequestsType = {
  name: string;
  description: null;
  baseUnit: string;
  measurements: Measurement[];
  availableTags: AvailableTag[];
};

type JvmMemoryMaxType = {
  name: string;
  description: string;
  baseUnit: string;
  measurements: Measurement[];
  availableTags: AvailableTag[];
};

type JvmMemoryUsageType = {
  name: string;
  description: string;
  baseUnit: string;
  measurements: Measurement[];
  availableTags: AvailableTag[];
};
type JvmMemoryUsedType = {
  name: string;
  description: string;
  baseUnit: string;
  measurements: Measurement[];
  availableTags: AvailableTag[];
};

type ProcessCpuUsageType = {
  name: string;
  description: string;
  baseUnit: null;
  measurements: Measurement[];
  availableTags: AvailableTag[];
};

type SystemCpuUsageType = {
  name: string;
  description: string;
  baseUnit: null;
  measurements: Measurement[];
  availableTags: AvailableTag[];
};

type AvailableTag = {
  tag: string;
  values: string[];
};

type Measurement = {
  statistic: string;
  value: number;
};

type ServerLogsType = {
  nodeMemoryMemAvailableBytes: string;
  diskAvailableBytes: string;
  cpuUsageRate: string;
};

export type {
  VirtualThreadType,
  DiskTotalType,
  DiskFreeType,
  HttpServerRequestsType,
  JvmMemoryUsageType,
  JvmMemoryUsedType,
  ProcessCpuUsageType,
  SystemCpuUsageType,
  JvmMemoryMaxType,
  Measurement,
  ServerLogsType,
};
