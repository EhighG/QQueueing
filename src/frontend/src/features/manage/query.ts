import { useMutation, useQuery } from "@tanstack/react-query";
import {
  getWaitingList,
  postWaiting,
  postWaitingActivate,
  postWaitingDeActivate,
} from "@/features";
import { WaitingListType } from "@/entities/waitingList/type";
import { AxiosError } from "axios";

// 대기열 등록
const useRegistWaiting = (
  waitingInfo: Omit<WaitingListType, "id" | "queueImageUrl">
) => {
  const { data } = useMutation({
    mutationFn: () => postWaiting(waitingInfo),
    onSuccess: (response) => {
      alert("success");
      console.log(response);
    },
    onError: () => {
      alert("error occur");
    },
  });

  return { data };
};

const useGetWaitingList = () => {
  const { data } = useQuery<
    WaitingListType[],
    AxiosError,
    WaitingListType[],
    [_1: string]
  >({
    queryKey: ["waitingList"],
    queryFn: getWaitingList,
  });

  return {
    data,
  };
};

// 대기열 활성화
const usePostWaitingActivate = (partitionNo: number) => {
  const { data } = useMutation({
    mutationFn: () => postWaitingActivate(partitionNo),
  });

  return { data };
};

// 대기열 비활성화
const usePostWaitingDeActivate = (partitionNo: number) => {
  const { data } = useMutation({
    mutationFn: () => postWaitingDeActivate(partitionNo),
  });

  return { data };
};

export {
  useRegistWaiting,
  useGetWaitingList,
  usePostWaitingActivate,
  usePostWaitingDeActivate,
};
