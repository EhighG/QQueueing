import { WaitingTable } from "@/entities/waitingList";
import { Button, SectionTitle } from "@/shared";
import Link from "next/link";
import React from "react";
const ListPage = () => {
  return (
    <div className="flex flex-col flex-1 gap-2 bg-q-white rounded-md border">
      <div className="flex flex-col flex-1 gap-2">
        <SectionTitle title="대기열 리스트" />
        <div className="flex flex-1 flex-col max-2xl:m-5 m-10">
          <WaitingTable />
          <div className="flex justify-end items-center gap-[10px] h-[60px] mt-2">
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
