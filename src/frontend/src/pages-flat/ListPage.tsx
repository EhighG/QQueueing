import { Button, SectionTitle } from "@/shared";
import Link from "next/link";
import React from "react";
const ListPage = () => {
  return (
    <div className="flex flex-col flex-1 gap-2 bg-q-white rounded-md border">
      <div className="flex flex-col flex-1 gap-2">
        <SectionTitle title="대기열 리스트" />
        <div className="flex flex-1 flex-col max-2xl:m-5 m-10">
          <table className="flex flex-col flex-1 border border-black rounded-md">
            <thead>
              <tr className="flex items-center w-full text-center border-b border-black text-[1.5rem] font-bold h-[60px] bg-slate-300">
                <th className="w-[12.5%]">ID</th>
                <th className="w-[25%]">등록 URL</th>
                <th className="w-[25%]">최대 수용 인원</th>
                <th className="w-[25%]">예상 수용</th>
                <th className="w-[12.5%]">활성 여부</th>
              </tr>
            </thead>
            <tbody>
              {/* <p className="flex w-full h-full items-center justify-center self-center">
                등록한 데이터가 없습니다
              </p> */}
              <tr className="flex items-center w-full text-center border-b border-black h-[60px]">
                <td className="w-[12.5%]">1</td>
                <td className="w-[25%]">3333</td>
                <td className="w-[25%]">321</td>
                <td className="w-[25%]">321</td>
                <td className="w-[12.5%]">3213</td>
              </tr>
            </tbody>
          </table>
          <div className="flex justify-end items-center gap-[10px] h-[60px]">
            <Button style="square">변경</Button>
            <Link href="/regist">
              <Button style="square">등록</Button>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ListPage;
