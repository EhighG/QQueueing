import { companyData } from "@/shared";
import { Banner, Card } from "@/widgets";

const MainTest = () => {
  return (
    <div className="flex flex-col w-full h-full p-5">
      <div className="text-[2rem] font-bold">
        <h1>삼성 소프트웨어 아카데미 특별 선착순 전형</h1>
      </div>
      <div className="grid max-md:grid-cols-1 max-lg:grid-cols-2 grid-cols-4 place-items-center gap-5">
        {companyData.map((company, index) => (
          <Banner key={index} productId={index} />
        ))}
      </div>
    </div>
  );
};

export default MainTest;
