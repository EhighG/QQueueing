import React, { ChangeEvent, ChangeEventHandler } from "react";

type InputProps = {
  label: string;
  type?:
    | "number"
    | "text"
    | "range"
    | "date"
    | "time"
    | "color"
    | "email"
    | "password"
    | "tel"
    | "image"
    | "url";
  title?: string;
  min?: number;
  max?: number;
  value?: number | string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
};

const Input = ({
  label,
  type = "text",
  min,
  max,
  value,
  title,
  onChange,
}: InputProps) => {
  return (
    <>
      <label className="text-[1.5rem] font-bold">{label}</label>
      <input
        type={type}
        title={title}
        min={min}
        className="w-full h-[50px] rounded-lg border border-black text-[1.5rem] p-1"
        value={value}
        onChange={onChange}
      />
    </>
  );
};

export default Input;
