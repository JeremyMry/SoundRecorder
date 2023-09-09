module.exports = {
    transform: { "^.+\\.ts?$": "ts-jest" },
    testEnvironment: "node",
    testRegex: "/tests/.*\\.(test|spec)?\\.(ts|tsx)$",
    testTimeout: 30000,
    moduleFileExtensions: ["ts", "tsx", "js", "jsx", "json", "node"],
};
