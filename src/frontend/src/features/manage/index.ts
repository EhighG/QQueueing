export {
  getWaitingList,
  getWaitingDetail,
  postWaiting,
  postWaitingActivate,
  postWaitingDeActivate,
  deleteWaiting,
  getWaitingStatus
} from "./api";

export {
  useRegistWaiting,
  useGetWaitingList,
  useGetWaitingDetail,
  usePostWaitingActivate,
  usePostWaitingDeActivate,
  useDeleteWaiting,
  useGetWaitingImage,
  useGetServiceImage,
  usePostWaitingImage,
  useGetWaitingStatus
} from "./query";

export type { ResponseType } from "./type";

export { ImageRegist, InputForm, TargetPage } from "./component";
