"use client";
import { Button, SectionTitle, logo } from "@/shared";
import Image from "next/image";
import React, { Dispatch, SetStateAction, useRef, useState } from "react";

type ImageRegistProps = {
  setImageData: Dispatch<SetStateAction<File>>;
  setThumbnail: Dispatch<SetStateAction<string>>;
};

const ImageRegist = ({ setImageData, setThumbnail }: ImageRegistProps) => {
  const [queueImageUrl, setQueueImageUrl] = useState<string>("");
  const ref = useRef<HTMLInputElement>(null);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files ? event.target.files[0] : null;

    if (file) {
      setImageData(file);
      const reader = new FileReader();
      reader.onload = (e) => {
        setQueueImageUrl(e.target?.result as string);
        setThumbnail(e.target?.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  return (
    <div className="flex w-full h-full overflow-auto flex-col border rounded-md border-black">
      <SectionTitle title="대기열 이미지" />
      <div className="flex flex-1">
        <div className="flex  size-[400px]">
          <Image
            src={queueImageUrl ? queueImageUrl : logo}
            alt="product"
            width={500}
            height={500}
            className="size-[500px]"
          />
        </div>
        <div className="flex flex-[2] overflow-auto flex-col gap-5 p-2">
          <p className="text-center font-bold">
            대기열 이미지는 대기열 우측 상단에 표시 됩니다
          </p>
          <Button
            edgeType="square"
            onClick={() => {
              ref?.current?.click();
            }}
            color="primary"
          >
            업로드
          </Button>
          <input
            hidden
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
