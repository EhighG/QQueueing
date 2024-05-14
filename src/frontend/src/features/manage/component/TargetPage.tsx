import React from "react";

type targetProps = {
  targetUrl: string;
};
const TargetPage = ({ targetUrl }: targetProps) => {
  return (

    <iframe title="targetPage" src={targetUrl} width="1920" height="1080" className="w-full h-full overflow-auto" />
  );
};

export default TargetPage;
