"use client";
import { Card } from "@/widgets";
import React, { useState } from "react";
import { useRouter } from "next/navigation";

const MainTest = () => {
  return (
    <div className="flex flex-col w-full h-full items-center justify-center">
      <div className="grid grid-cols-4 grid-rows-2 gap-5">
        <Card waiting={true} />
        <Card />
        <Card />
        <Card />
        <Card />
        <Card />
        <Card />
        <Card />
      </div>
    </div>
  );
};

export default MainTest;
