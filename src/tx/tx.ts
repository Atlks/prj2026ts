import {
  Column,
  DataSource,
  Entity,
  PrimaryGeneratedColumn,
} from 'typeorm';

@Entity()
export class Transaction {
   @PrimaryGeneratedColumn("uuid")
    transactionId: string;

    @Column()
    owner: string;

    @Column("decimal")
    amount: number;

    @Column()
    status: string;

    @Column()
    transactionReference: string;

    @Column()
    creditDebitIndicator: string;

    @Column()
    txCode: string;

    @Column()
    currency: string;
}

const dataSource = new DataSource({
    type: "mysql",
    host: "localhost",
    port: 3306,
    username: "root",
    password: "password",
    database: "springcloudgame",
    synchronize: true,
    entities: [Transaction]
});

export class TransactionManagerUtil {
    static async beginTransactDebitType(userId: string, refid: string, amt: number): Promise<string> {
        const tx = new Transaction();
        tx.transactionId = refid;
        tx.owner = userId;
        tx.amount = amt;
        tx.status = "Pending";
        tx.transactionReference = refid;
        tx.creditDebitIndicator = "Debit";
        tx.txCode = "TRANSFER_out";
        tx.currency = "cny";
        
        const repository = dataSource.getRepository(Transaction);
        await repository.save(tx);
        return refid;
    }

    static async beginTransactCreditType(userId: string, balAmt: number, tx: Transaction): Promise<void> {
        tx.owner = userId;
        tx.amount = balAmt;
        tx.status = "Pending";
        tx.creditDebitIndicator = "Credit";
        tx.txCode = "TRANSFER_in";
        tx.currency = "cny";

        const repository = dataSource.getRepository(Transaction);
        await repository.save(tx);
    }

    static async commit(txid: string): Promise<void> {
        const repository = dataSource.getRepository(Transaction);
        const tx = await repository.findOneBy({ transactionId: txid });
        if (tx) {
            tx.status = "Booked";
            await repository.save(tx);
        }
    }

    static async rollback(txid: string): Promise<void> {
        const repository = dataSource.getRepository(Transaction);
        const tx = await repository.findOneBy({ transactionId: txid });
        if (tx) {
            tx.status = "Aborted";
            await repository.save(tx);
        }
    }
}