import { EventSourcePolyfill } from "event-source-polyfill";

const sseConnect = () => {
  //TODO: SSE 연결시 각 클라이언트를 구분 해 줄 수 있는 정보 추가 필요
  const eventSource = new EventSourcePolyfill(process.env.baseUrl + `/${1}`, {
    headers: {
      "Content-Type": "text/event-stream; charset=UTF-8",
    },
    heartbeatTimeout: 3600 * 1000,
  });
  return eventSource;
};

export default sseConnect;
