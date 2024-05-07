"use client";
import React, { Dispatch, SetStateAction } from "react";

type SelectBoxProps = {
  label: string;
  step: number;
  onChange: Dispatch<SetStateAction<number>>;
  value: number;
};

const SelectBox = ({ label, step, onChange, value }: SelectBoxProps) => {
  return (
    <div className="flex w-full flex-col">
      <label className="text-[1.5rem] font-bold">{label}</label>
      <select
        title="box"
        className="w-full h-[50px] rounded-lg border border-black text-[1.5rem] p-1"
        onChange={(e) => onChange(parseInt(e.currentTarget.value))}
        value={value}
      >
        {Array.from({ length: 11 }, (_, i) => i * step).map((value) => (
          <option key={value} value={value}>
            {value}
          </option>
        ))}
      </select>
    </div>
  );
};

export default SelectBox;
