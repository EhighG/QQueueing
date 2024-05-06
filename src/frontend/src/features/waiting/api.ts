import { axiosInstance } from "@/shared";
import { AxiosInstance } from "axios";
import { infoType, statusType } from "./type";
import { ResponseType } from "..";

const instance: AxiosInstance = axiosInstance();

// 대기열 입장
const postEnqueue = async (target: string): Promise<infoType> => {
  return await instance
    .post<ResponseType<infoType>>(
      "/waiting",
      {},
      {
        headers: {
          "Content-Type": "application/json; charset=utf8",
          "Target-Url": target,
        },
      }
    )
    .then(({ data }) => data.result);
};

// 현재 나의 순번 조회
const getWaitingInfo = async (
  partitionNo: number,
  order: number,
  idVal: string
) => {
  return await instance
    .post<any>(`/waiting/order`, {
      partitionNo,
      order,
      idVal,
    })
    .then(({ data }) => data);
};

// 대기열 나가기
const getWaitingOut = async (partitionNo: number, order: number) => {
  return await instance
    .get(`/waiting/out?partitionNo=${partitionNo}&order=${order}`)
    .then(({ data }) => data);
};

export { postEnqueue, getWaitingInfo, getWaitingOut };
