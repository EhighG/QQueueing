import { useQuery } from "@tanstack/react-query";
import { getWaitingInfo, postEnqueue } from "./api";
import { infoType, statusType } from "./type";
import { AxiosError } from "axios";

export const useEnqueue = () => {
  const { data, isLoading, isError } = useQuery<
    infoType,
    AxiosError,
    infoType,
    [_1: string]
  >({
    queryKey: ["enqueue"],
    queryFn: postEnqueue,
  });

  return {
    data,
    isLoading,
    isError,
  };
};

export const useGetWaitingInfo = (idx: number) => {
  const { data, isLoading, isError } = useQuery<
    statusType,
    AxiosError,
    statusType,
    [_1: string]
  >({
    queryKey: ["enqueue"],
    queryFn: () => getWaitingInfo(idx),
    refetchInterval: 3000,
    enabled: idx > 0,
  });

  return {
    data,
    isLoading,
    isError,
  };
};
