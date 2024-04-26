export default async function getTestData() {
  const res = await fetch("https://jsonplaceholder.typicode.com/todos/1", {
    next: {
      tags: ["list"],
    },
    cache: "no-store",
  });

  if (!res.ok) {
    throw new Error("Fail");
  }

  return res.json();
}
