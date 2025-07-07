module.exports = {
  root: true,
  env: {
    es6: true,
    node: true,
  },
  extends: [
    "eslint:recommended",
    "@typescript-eslint/recommended",
  ],
  parser: "@typescript-eslint/parser",
  parserOptions: {
    project: ["tsconfig.json", "tsconfig.dev.json"],
    sourceType: "module",
  },
  ignorePatterns: [
    "/lib/**/*", // Ignore built files.
  ],
  plugins: [
    "@typescript-eslint",
  ],
  rules: {
    "quotes": "off",
    "object-curly-spacing": "off",
    "no-trailing-spaces": "off",
    "max-len": "off",
    "require-jsdoc": "off",
    "@typescript-eslint/no-explicit-any": "off",
    "@typescript-eslint/no-non-null-assertion": "off",
    "no-async-promise-executor": "off",
    "comma-dangle": "off",
    "padded-blocks": "off",
    "indent": "off",
    "arrow-parens": "off",
  },
};
