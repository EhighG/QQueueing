import React, { ReactElement } from "react";

type PerformanceCardProps = {
  title: string;
  perform: number;
};

const PerformanceCard = ({ title, perform }: PerformanceCardProps) => {
  return (
    <div className="flex flex-col items-center justify-center gap-5 size-[240px] bg-slate-200">
      <h1 className="text-[2rem] font-bold">{title}</h1>
      <p className="text-[2rem] font-bold">{perform}</p>
    </div>
  );
};

export default PerformanceCard;
