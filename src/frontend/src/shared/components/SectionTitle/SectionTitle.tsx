import React from "react";

type SectionTitleProps = {
  title: string;
};

const SectionTitle = ({ title }: SectionTitleProps) => {
  return (
    <div className="flex w-full items-center h-[60px] p-3">
      <p className="text-[1.5rem] font-bold">{title}</p>
    </div>
  );
};

export default SectionTitle;
