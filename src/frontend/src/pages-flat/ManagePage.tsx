import { SectionTitle } from "@/shared";
import React from "react";

type ManagePageProps = {
  id: string;
};

const ManagePage = ({ id }: any) => {
  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md border">
      <SectionTitle title="대기열 상태 관리 페이지" />
      <div className="flex flex-1 flex-col max-2xl:m-5 m-10 p-5 border rounded-md border-slate-300">
        <div className="flex flex-1 gap-5">
          <div className="flex flex-1 flex-col">
            <div className="flex flex-1 flex-col">
              <SectionTitle title="공지 보내기" />
              <div className="flex flex-1 border rounded-md border-slate-300">
                ㅇ?
              </div>
            </div>
            <div className="flex flex-1 flex-col">
              <SectionTitle title="공지 전송 내역" />
              <div className="flex flex-1 border rounded-md border-slate-300">
                ㅇ?
              </div>
            </div>
          </div>
          <div className="flex flex-1 ">
            <div className="flex flex-1 flex-col">
              <SectionTitle title="모니터링" />
              <div className="flex flex-1 border rounded-md border-slate-300">
                <div className="flex-1 grid grid-rows-2 grid-cols-2 items-center place-items-center">
                  <div>
                    모니터링1
                  </div>
                  <div className="bg-yellow-300">모니터링2</div>
                  <div className="bg-red-300">모니터링3</div>
                  <div className="bg-blue-300">모니터링4</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ManagePage;
