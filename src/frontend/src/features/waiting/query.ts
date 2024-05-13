import { useQuery } from "@tanstack/react-query";
import { getWaitingInfo, getWaitingOut, postEnqueue } from "./api";
import { infoType, statusType } from "./type";
import { AxiosError } from "axios";

const useEnqueue = (target: string) => {
  const { data, isLoading, isError } = useQuery<
    infoType,
    AxiosError,
    infoType,
    [_1: string]
  >({
    queryKey: ["enqueue"],
    queryFn: () => postEnqueue(target),
    enabled: target.length > 0 && typeof window !== "undefined",
  });

  return {
    data,
    isLoading,
    isError,
  };
};
const useGetWaitingInfo = (partitionNo: number, idx: number, idVal: string) => {
  const { data, isLoading, isError } = useQuery<
    statusType,
    AxiosError,
    statusType,
    [_1: string]
  >({
    queryKey: ["waitingInfo"],
    queryFn: () => getWaitingInfo(partitionNo, idx, idVal),
    refetchInterval: 3000,
    enabled: idx > 0 && typeof window !== "undefined",
  });

  return {
    data,
    isLoading,
    isError,
  };
};

const useGetWaitingOut = (partitionNo: number, order: number) => {
  const { data, refetch, isSuccess } = useQuery({
    queryKey: ["waitingOut"],
    queryFn: () => getWaitingOut(partitionNo, order),
    enabled: false && typeof window !== "undefined",
  });

  return { data, refetch, isSuccess };
};

export { useEnqueue, useGetWaitingInfo, useGetWaitingOut };
