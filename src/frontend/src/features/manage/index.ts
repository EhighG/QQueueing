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
  useGetWaitingImage,
  useGetServiceImage,
  usePostWaitingImage,
} from "./query";

export type { ResponseType } from "./type";

export { ImageRegist, InputForm, TargetPage } from "./component";
