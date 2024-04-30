"use client";

import React from "react";
import { WaitingListType } from "./type";

type WaitingTableProps = {
  data?: WaitingListType[] | null;
};

const WaitingTable = ({ data }: WaitingTableProps) => {
  return (
    <table className="flex flex-col flex-1 rounded-md shadow-lg border">
      <thead>
        <tr className="flex items-center w-full text-center border-b border-slate-300 text-[1.5rem] font-bold h-[60px]">
          <th className="w-[12.5%]">ID</th>
          <th className="w-[25%]">등록 URL</th>
          <th className="w-[25%]">최대 수용 인원</th>
          <th className="w-[25%]">예상 수용</th>
          <th className="w-[12.5%]">활성 여부</th>
        </tr>
      </thead>
      <tbody>
        {data ? (
          data.map((item) => (
            <tr
              key={item.id}
              className="flex items-center w-full text-center border-b border-black h-[60px]"
            >
              <td className="w-[12.5%]">{item.id}</td>
              <td className="w-[25%]">{item.registrationUrl}</td>
              <td className="w-[25%]">{item.maxCapacity}</td>
              <td className="w-[25%]">{item.processingPerMinute}</td>
              <td className="w-[12.5%]">활성 중</td>
            </tr>
          ))
        ) : (
          <tr className="flex w-full h-full items-center justify-center text-center self-center">
            <td>
              <p>등록한 데이터가 없습니다</p>
            </td>
          </tr>
        )}
      </tbody>
    </table>
  );
};

export default WaitingTable;
