"use client";
import { Card } from "@/widgets";

const MainTest = () => {
  return (
    <div className="flex flex-col w-full h-full items-center justify-center p-5">
      <div className="grid grid-cols-4 grid-rows-2 gap-5">
        <Card />
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
