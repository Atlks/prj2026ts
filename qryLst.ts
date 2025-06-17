
// import { Parser } from '@node-sql-parser/sql-parser';
// const sql = "SELECT * FROM users WHERE uname='aaa' AND age>5";
// const parser = new Parser();
// const ast = parser.astify(sql, { database: 'mysql' });
// if ('where' in ast && ast.where) {
//     const conditions = ast.where;
//     function extractConditions(expression: any, conditionList: string[] = []) {
//         if (expression.type === 'binary_expr') {
//             conditionList.push(`${expression.left.column} ${expression.operator} ${expression.right.value}`);
//         }
//         if (expression.left) extractConditions(expression.left, conditionList);
//         if (expression.right) extractConditions(expression.right, conditionList);
//         return conditionList;
//     }
//     const conditionList = extractConditions(conditions);
//     conditionList.forEach((condition, index) => console.log(`条件${index + 1}: ${condition}`));
// }