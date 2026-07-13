/**
 * Cucumber config for the BDD side of the framework (mirrors the Java
 * project's testng-bdd.xml + SmokeRunner/RegressionRunner). Uses tsx's CJS
 * require-hook so step definitions can be authored directly in TypeScript
 * with no separate compile step.
 */
module.exports = {
  default: {
    requireModule: ["tsx/cjs"],
    require: ["step-definitions/**/*.ts"],
    paths: ["features/**/*.feature"],
    format: ["progress-bar"],
    publishQuiet: true,
  },
};
