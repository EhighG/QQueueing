"use client";
import React, { useState } from "react";

const Regist = () => {
  const [url, setUrl] = useState("");
  return (
    <div className="flex flex-col w-full h-full items-center justify-center">
      <div className="w-[340px] flex flex-col text-center">
        <label>URL을 등록해주세요</label>
        <input
          type="text"
          title="url"
          className="border border-black"
          onChange={(e) => setUrl(e.target.value)}
        />
        <button
          type="button"
          onClick={() => alert(`URL 등록 : ${url}`)}
          className="p-2 bg-slate-300"
        >
          등록
        </button>
      </div>
    </div>
  );
};

export default Regist;
