"use client";
import { Card } from "@/widgets";

const MainTest = () => {
  return (
    <div className="flex flex-col w-full h-full items-center justify-center p-5">
      <div className="grid grid-cols-4 grid-rows-2 gap-5">
        {Array.from({ length: 10 }, (_, index) => (
          <Card key={index + 1} productId={index + 1} />
        ))}
      </div>
    </div>
  );
};

export default MainTest;
