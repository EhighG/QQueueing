"use client";
import { Metadata } from "next";
import React from "react";

const Page = () => {
  return (
    <div className="flex flex-col">
      <label>URL을 등록해주세요</label>
      <input type="text" title="url" className="border border-black" />
      <button
        type="button"
        onClick={() => alert("URL 등록")}
        className="p-2 bg-slate-300"
      >
        등록
      </button>
    </div>
  );
};

export default Page;
