// Predefined test flows the dashboard can launch. Each maps to a Cucumber tag
// expression understood by the Maven run in test-pipeline.yml
// (-Dcucumber.filter.tags=...). Edit the tag expressions here as the suite grows.
export interface FlowDef {
  key: string;
  name: string;
  description: string;
  tags: string;
}

export const FLOWS: FlowDef[] = [
  {
    key: 'login',
    name: 'Login Flow',
    description: 'Phone/OTP login and authentication smoke checks.',
    tags: '@Login',
  },
  {
    key: 'checkout',
    name: 'Checkout Flow',
    description: 'Cart addition through domestic/international checkout init.',
    tags: '@Checkout',
  },
  {
    key: 'e2e',
    name: 'Complete End-to-End Flow',
    description: 'Full smoke + regression journey across the application.',
    tags: '@Smoke or @Regression',
  },
];

export const findFlow = (key: string): FlowDef | undefined =>
  FLOWS.find((f) => f.key === key);

export const SUPPORTED_BROWSERS = ['chrome', 'firefox', 'edge', 'brave'] as const;
export const SUPPORTED_ENVS = ['dev', 'staging', 'prod'] as const;