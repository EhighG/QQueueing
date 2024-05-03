import { WaitingListType } from "@/entities/waitingList/type";
import { ResponseType } from "./type";
import { axiosInstance } from "@/shared";
import { AxiosInstance } from "axios";

const instance: AxiosInstance = axiosInstance();

// 대기열 조회
const getWaitingList = async () => {
  return await instance
    .get<ResponseType<WaitingListType[]>>("/queue")
    .then(({ data }) => data.result);
};

// 대기열 등록
const postWaiting = async (
  data: Omit<WaitingListType, "id" | "queueImageUrl">
) => {
  return await instance
    .post<ResponseType<WaitingListType>>("/queue", data)
    .then(({ data }) => data.result);
};

// 대기열 활성화
const postWaitingActivate = async (partitionNo: number) => {
  return await instance
    .post<ResponseType<null>>(`/waiting/${partitionNo}/activate`)
    .then(({ data }) => data);
};

// 대기열 비활성화
const postWaitingDeActivate = async (partitionNo: number) => {
  return await instance
    .post<ResponseType<null>>(`/waiting/${partitionNo}/deactivate`)
    .then(({ data }) => data);
};

export {
  getWaitingList,
  postWaiting,
  postWaitingActivate,
  postWaitingDeActivate,
};
