import React, { ReactElement } from "react";

type PerformanceCardProps = {
  title: string;
  children: ReactElement;
};

const PerformanceCard = ({ title, children }: PerformanceCardProps) => {
  return (
    <div className="flex flex-col items-center justify-center gap-5 size-[260px]">
      {children}
      <h1 className="text-[2rem] font-bold">{title}</h1>
    </div>
  );
};

export default PerformanceCard;
