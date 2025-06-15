// npm i --save-dev @types/spel2js
import { SpelExpressionEvaluator } from 'spel2js';

const expression = '#user.age > 18';
const context = { user: { age: 25 } };

console.log(SpelExpressionEvaluator.eval(expression, context)); // true