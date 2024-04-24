import Link from "next/link";
import React from "react";

const RegistPage = () => {
  return (
    <div className="flex flex-1 flex-col border border-slate-300 rounded-md shadow-xl">
      <div className="flex w-full items-center border-b border-black h-[60px] p-3">
        <p className="text-[1.5rem] font-bold">등록 페이지</p>{" "}
      </div>
      <div className="flex flex-1 max-2xl:m-5 m-10 ">
        <div className="flex flex-1 flex-col  items-center">
          <div className="flex flex-1 flex-col w-full p-10">
            <div className="flex flex-col flex-1  gap-5">
              <label className="text-[1.5rem] font-bold">
                대기열 등록 대상 URL
              </label>
              <input
                type="text"
                title="대기열 등록 대상 URL"
                className="w-full h-[50px] rounded-lg border border-black text-[1.5rem] p-1"
              />
              <label className="text-[1.5rem] font-bold">최대 수용 인원</label>
              <input
                type="number"
                title="최대 수용 인원"
                className="w-full h-[50px] rounded-lg border border-black"
              />
              <label className="text-[1.5rem] font-bold">
                1분 당 처리 인원
              </label>
              <input
                type="number"
                title="1분 당 처리 인원"
                className="w-full h-[50px] rounded-lg border border-black"
              />
              <label className="text-[1.5rem] font-bold">서비스 명</label>
              <input
                type="text"
                title="서비스 명"
                className="w-full h-[50px] rounded-lg border border-black"
              />
              <div className="flex w-full justify-around">
                <Link href="/waiting/1">
                  <button
                    type="button"
                    className="w-[146px] h-[50px] text-[1.5rem] font-bold bg-gray-400 border border-black rounded-md"
                  >
                    미리 보기
                  </button>
                </Link>
                <button
                  type="button"
                  className="w-[146px] h-[50px] text-[1.5rem] font-bold bg-gray-400 border border-black rounded-md"
                >
                  등록
                </button>
              </div>
            </div>
          </div>
        </div>
        <div className="flex flex-1 bg-green-300">대기열, 예상 치</div>
      </div>
    </div>
  );
};

export default RegistPage;
