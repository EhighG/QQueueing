import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/pages-flat/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/widgets/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/entities/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/shared/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      keyframes: {
        blink: {
          "0%, 100%": { "text-decoration": "underline" },
          "50%": { "text-decoration": "none" },
        },
        shine: {
          "0%": { backgroundPosition: "-200px 0" },
          "100%": { backgroundPosition: "200px 0" },
        },
      },
      animation: {
        blink: "blink 1.5s linear infinite",
        shine: "shine 5s infinite linear",
      },

      backgroundImage: {
        "gradient-radial": "radial-gradient(var(--tw-gradient-stops))",
        "gradient-conic":
          "conic-gradient(from 180deg at 50% 50%, var(--tw-gradient-stops))",
      },
      colors: {
        "q-grey:": "#6E6B7B",
        "q-lightgrey": "#F8F8F8",
        "q-white": "#FFFFFF",
        "q-blue": "#3B82F6",
        "q-green": "#10B981",
      },
    },
  },
  plugins: [],
};
export default config;
