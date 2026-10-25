regarding ip config, The concept is for each client we have to assigne a new IP.

Rules:

1. One Ip Can assigne to only 4 individual brokers [AngelOne, Motilal, Upstox and Dhan]
2. IP already assigned to angelOne broker type can not be asigned to different account with same broker type [account 1 broker type angel and Account 2 broker type is AngelOne, In this scenario not allowed to assign.]
3. while deleting/deactivating account we should unlink IP & port with that account, So that it can be assigned to other accounts

On screen we have api to validate IP whether it is already assigned to same broker type or not and limit to 4 different brokers. but even though, we have to validate IP while adding account . if it is not satisfying conditions then we have to stop saving account
Dont create or update anything in ip_config table. we are saving IP data in master_accounts table. So we can use to validate IP assigned or not/assigned to same broker type or not using this master account table
Can you do deep analysis on these scenarios and apply changes all over