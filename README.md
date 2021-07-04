# CardCoupon-client

The subsystem of distributed coupon system.

This is the client side subsystem.

## services we need to start:

1. HBase

```
cd /usr/local/opt/hbase/bin/

start-hbase.sh

./Hbase shell
```
Can be visited by localhost:16010

2. mysql
```
Mysql -u root -p
```
   
3. kafka
```
cd /usr/local/Cellar/kafka/2.8.0/libexec

bin/kafka-topics.sh --create --topic merchants-template --bootstrap-server localhost:9092
bin/kafka-topics.sh --describe --topic merchants-template --bootstrap-server localhost:9092
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic merchants-template --from-beginning

```
4. Redis
```
/usr/local/opt/redis/bin/redis-server
```

## Merchants API
1. upload token
```
GET: 127.0.0.1:9528/upload
```

## client API
1. Create User
```
POST: 127.0.0.1:9528/passbook/createuser
```

2. Get Inventory Information of a user
```
GET: 127.0.0.1:9528/passbook/inventoryinfo?userId=[userid]
```
userid is the parameter

3. Gain a coupon

you can choose has_token = true / false

```
POST: 127.0.0.1:9528/passbook/gainpasstemplate
```
```
{
    "userId": userId,
    "passTemplate": {
        "id": id,
        "title": ["title"],
        "hasToken": true
    }
}
```
userid is the parameter

5. userpassinfo
```
GET: 127.0.0.1:9528/passbook/userpassinfo?userId=[userid]
```

6. userusedpassinfo
```
GET: 127.0.0.1:9528/passbook/userusedpassinfo?userId=[userid]
```

7. userusepass
```
   POST: 127.0.0.1:9528/passbook/userusepass
```
```
 {
   "userId": [userId],
   "templateId": ["templateId"]
   }
```

8. create Feedback
```
POST: 127.0.0.1:9528/passbook/createfeedback
```

APP Feedback
```
{
    "userId": userId,
    "type": "app",
    "templateId": -1,
    "comment": "..."
    }
```

Coupon Feedback
```
{
    "userId": userId,
    "type": "pass",
    "templateId": ["templateId"],
    "comment": "..."
}
```

9. 查看评论信息
```
GET: 127.0.0.1:9528/passbook/getfeedback?userId=[userId]
```

