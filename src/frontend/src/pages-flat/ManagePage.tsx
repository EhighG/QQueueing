"use client";
import { WaitingListType } from "@/entities/waitingList/type";
import { InputForm } from "@/features";
import { useGetWaitingDetail, useDeleteWaiting } from "@/features";
import { usePatchWaiting } from "@/features/manage/query";
import { Button, SectionTitle } from "@/shared";
import { Box } from "@mui/material";
import { SparkLineChart } from "@mui/x-charts";
import { useSearchParams } from "next/navigation";
import React, { useEffect, useState } from "react";

type ManagePageProps = {
  id: string;
};

const ManagePage = ({ id }: ManagePageProps) => {
  // 삭제
  const { mutate: handleDelete } = useDeleteWaiting(id);
  const { data } = useGetWaitingDetail(id);
  const [waitingInfo, setWaitingInfo] = useState<WaitingListType>(
    {} as WaitingListType
  );

  useEffect(() => {
    if (data) setWaitingInfo(data);
  }, [data]);

  const { mutate: handlePatch } = usePatchWaiting(id, waitingInfo);

  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md border">
      <SectionTitle title={`대기열 ID ${id} 상태 관리`} />
      <div className="flex flex-1 flex-col max-2xl:m-5 m-10 p-5 border rounded-md border-slate-300">
        <div className="flex flex-1 gap-5">
          <div className="flex flex-1 flex-col">
            <InputForm waitingInfo={data} setWaitingInfo={setWaitingInfo} />
          </div>
          <div className="flex flex-1">
            <div className="flex flex-1 flex-col">
              <SectionTitle title="모니터링" />
              <div className="flex flex-1 border rounded-md border-slate-300">
                <div className="flex-1 grid grid-rows-2 grid-cols-2 items-center place-items-center">
                  <div className="row-span-1 col-span-2">
                    <Box>
                      <SparkLineChart data={[1, 4, 2, 5, 7, 2, 4, 6]} />
                    </Box>
                  </div>
                  <div className="bg-red-300">모니터링3</div>
                  <div className="bg-blue-300">모니터링4</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div className="flex w-full justify-end mt-2 gap-4">
          <Button edgeType="square" onClick={() => handlePatch()}>
            변경
          </Button>
          <Button
            edgeType="square"
            color="warning"
            onClick={() => handleDelete()}
          >
            삭제
          </Button>
        </div>
      </div>
    </div>
  );
};

export default ManagePage;
