import { useMutation, useQuery } from "@tanstack/react-query";
import { getWaitingInfo, postWaitingOut, postEnqueue } from "./api";
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

// queueId나 waiterId가 빈 문자열이면 순번 조회를 멈춘다
const useGetWaitingInfo = (queueId: string, waiterId: string) => {
  const { data, isLoading, isError } = useQuery<
    statusType,
    AxiosError,
    statusType,
    [_1: string]
  >({
    queryKey: ["waitingInfo"],
    queryFn: () => getWaitingInfo(queueId, waiterId),
    refetchInterval: 1000,
    enabled:
      waiterId.length > 0 && queueId.length > 0 && typeof window !== "undefined",
  });

  return {
    data,
    isLoading,
    isError,
  };
};

const usePostWaitingOut = (queueId: string, waiterId: string) => {
  const { mutate, isSuccess } = useMutation({
    mutationFn: () => postWaitingOut(queueId, waiterId),
  });

  return { mutate, isSuccess };
};

export { useEnqueue, useGetWaitingInfo, usePostWaitingOut };
