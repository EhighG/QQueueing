import { axiosInstance } from "@/shared";
import { AxiosInstance } from "axios";
import { infoType, statusType } from "./type";
import { ResponseType } from "..";

const instance: AxiosInstance = axiosInstance();

// 줄 서기
const postEnqueue = async (target: string): Promise<infoType> => {
  return await instance
    .post<ResponseType<infoType>>(
      "/waiting",
      {},
      {
        headers: {
          "Content-Type": "application/json; charset=utf8",
          "Target-URL": target,
        },
      }
    )
    .then(({ data }) => data.result);
};

// 현재 나의 순번 조회
const getWaitingInfo = async (queueId: string, waiterId: string) => {
  return await instance
    .post<statusType>(`/waiting/order`, {
      queueId,
      waiterId,
    })
    .then(({ data }) => data);
};

// 이탈. 창을 닫거나 다른 페이지로 떠나는 중에도 요청이 나가도록 sendBeacon으로 보낸다.
// sendBeacon은 POST만 보내고 응답을 기다리지 않는다. 본문은 form(application/x-www-form-urlencoded)이라
// CORS preflight 없이 나가고, 서버(POST /waiting/out)는 요청 파라미터로 받는다.
// 브라우저가 요청을 전송 대기열에 넣었으면 true를 돌려준다.
const sendLeaveBeacon = (queueId: string, waiterId: string): boolean => {
  return navigator.sendBeacon(
    `${process.env.NEXT_PUBLIC_BASE_URL}/waiting/out`,
    new URLSearchParams({ queueId, waiterId })
  );
};

export { postEnqueue, getWaitingInfo, sendLeaveBeacon };
