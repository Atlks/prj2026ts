// import {
//   Column,
//   Entity,
//   PrimaryGeneratedColumn,
// } from 'typeorm';

// @Entity()
// export class Transaction {
//     @PrimaryGeneratedColumn()  // 或 @PrimaryGeneratedColumn("uuid")
//     transactionId: number;

//     @Column()
//     owner: string;

//     @Column("decimal", {
//         transformer: {
//             to: (value: number) => value.toString(),
//             from: (value: string) => parseFloat(value),
//         }
//     })
//     amount: number;
// }
