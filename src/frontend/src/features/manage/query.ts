import { useMutation, useQuery } from "@tanstack/react-query";
import {
  getWaitingList,
  postWaiting,
  postWaitingActivate,
  postWaitingDeActivate,
} from "@/features";
import {
  WaitingListType,
  waitingRegistType,
} from "@/entities/waitingList/type";
import { AxiosError } from "axios";
import { getWaitingDetail } from ".";
import { deleteWaiting, patchWaiting, postWaitingImage } from "./api";
import Swal from "sweetalert2";

// 대기열 등록
const useRegistWaiting = (waitingInfo: waitingRegistType) => {
  const { mutate } = useMutation({
    mutationFn: () => postWaiting(waitingInfo),
    onSuccess: () => {
      Swal.fire({
        title: "등록이 완료되었습니다.",
        icon: "success",
        confirmButtonText: "확인",
      });
    },
    onError: () => {
      Swal.fire({
        title: "등록에 실패하였습니다.",
        icon: "error",
        confirmButtonText: "확인",
      });
    },
  });

  return { mutate };
};

// 대기열 상세 조회
const useGetWaitingDetail = (id: string) => {
  const { data } = useQuery<
    WaitingListType,
    AxiosError,
    WaitingListType,
    [_1: string, _2: string]
  >({
    queryKey: ["waitingDetail", id],
    queryFn: () => getWaitingDetail(id),
    gcTime: 0,
  });

  return { data };
};

const usePostWaitingImage = (formData: FormData) => {
  const { mutate, data } = useMutation({
    mutationFn: () => postWaitingImage(formData),
  });

  return { mutate, data };
};

const usePatchWaiting = (id: string, data: WaitingListType) => {
  const { mutate } = useMutation({
    mutationFn: () => patchWaiting(id, data),
    onSuccess: () => {
      Swal.fire({
        title: "수정 성공",
        icon: "success",
        confirmButtonText: "확인",
      }).then(() => {
        window.history.go(0);
      });
    },
    onError: () => {
      Swal.fire({
        title: "수정에 실패하였습니다.",
        icon: "error",
        confirmButtonText: "확인",
      });
    },
  });

  return { mutate };
};

const useDeleteWaiting = (id: string) => {
  const { mutate } = useMutation({
    mutationFn: () => deleteWaiting(id),
    onSuccess: () => {
      Swal.fire({
        title: "삭제 성공",
        icon: "success",
        confirmButtonText: "확인",
      }).then(() => {
        window.history.back();
      });
    },
    onError: () => {
      Swal.fire({
        title: "삭제에 실패하였습니다.",
        icon: "error",
        confirmButtonText: "확인",
      });
    },
  });

  return { mutate };
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
    onSuccess: () => {
      Swal.fire({
        title: "활성화 성공",
        icon: "success",
        confirmButtonText: "확인",
      }).then(() => {
        window.history.go();
      });
    },
    onError: () => {
      Swal.fire({
        title: "활성화에 실패하였습니다.",
        icon: "error",
        confirmButtonText: "확인",
      });
    },
  });

  return { data };
};

// 대기열 비활성화
const usePostWaitingDeActivate = (partitionNo: number) => {
  const { data } = useMutation({
    mutationFn: () => postWaitingDeActivate(partitionNo),
    onSuccess: () => {
      Swal.fire({
        title: "비활성화 성공",
        icon: "success",
        confirmButtonText: "확인",
      }).then(() => {
        window.history.go();
      });
    },
    onError: () => {
      Swal.fire({
        title: "비활성화에 실패하였습니다.",
        icon: "error",
        confirmButtonText: "확인",
      });
    },
  });

  return { data };
};

export {
  useRegistWaiting,
  useGetWaitingList,
  useGetWaitingDetail,
  usePostWaitingActivate,
  usePostWaitingDeActivate,
  usePostWaitingImage,
  useDeleteWaiting,
  usePatchWaiting,
};
