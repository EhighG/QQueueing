export default function Layout({
  monitoring,
  resource,
}: {
  monitoring: React.ReactNode;
  resource: React.ReactNode;
}) {
  return (
    <>
      <div className="flex flex-1 border border-slate-300 rounded-md shadow-xl">
        {monitoring}
      </div>
      <div className="flex flex-1 border border-slate-300 rounded-md shadow-xl">
        {resource}
      </div>
    </>
  );
}
