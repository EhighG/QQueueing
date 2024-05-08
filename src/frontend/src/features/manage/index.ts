export {
  getWaitingList,
  getWaitingDetail,
  postWaiting,
  postWaitingActivate,
  postWaitingDeActivate,
  deleteWaiting,
} from "./api";

export {
  useRegistWaiting,
  useGetWaitingList,
  useGetWaitingDetail,
  usePostWaitingActivate,
  usePostWaitingDeActivate,
  useDeleteWaiting,
} from "./query";

export type { ResponseType } from "./type";

export { ImageRegist, InputForm, Performance } from "./component";
