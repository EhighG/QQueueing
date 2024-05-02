"use client";
import { Button, SectionTitle, cats } from "@/shared";
import Image from "next/image";
import React, { useEffect, useRef, useState } from "react";

type ImageRegistProps = {
  setImageData: (data: string) => void;
};

const ImageRegist = ({ setImageData }: ImageRegistProps) => {
  const [queueImageUrl, setQueueImageUrl] = useState<string>("");
  const ref = useRef<HTMLInputElement>(null);

  useEffect(() => {
    setImageData(queueImageUrl);
  }, [queueImageUrl, setImageData]);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files ? event.target.files[0] : null;
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        setQueueImageUrl(e.target?.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  return (
    <div className="flex flex-[2] overflow-auto flex-col border rounded-md border-black">
      <SectionTitle title="대기열 이미지" />
      <div className="flex flex-1">
        <div className="flex flex-[1] justify-center">
          <Image
            src={queueImageUrl ? queueImageUrl : cats}
            alt="sample"
            width={500}
            height={500}
          />
        </div>
        <div className="flex flex-[2] overflow-auto flex-col gap-5 p-2">
          <p className="text-center font-bold">
            대기열 이미지는 대기열 우측 상단에 표시 됩니다
          </p>
          <Button
            className="p-2 border rounded-md border-black"
            onClick={() => {
              ref?.current?.click();
            }}
          >
            등록
          </Button>
          <input
            ref={ref}
            type="file"
            id="file"
            onChange={handleFileChange}
            className="hidden"
            title="file"
          />
        </div>
      </div>
    </div>
  );
};

export default ImageRegist;
