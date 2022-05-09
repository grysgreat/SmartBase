**平台支持的异构数据格式：**


***非结构化数据（kafka）***

```
{
	"destName": "kafka",  //（数据终端类型）
	"url": "192.168.10.1:3306",  //（数据库地址）
	"sdata": "123123123", //（流式string数据：sdata）
	"topic": "main" //（kafka的topic）
}
```

***结构化数据（mysql）***
```
{
	"destName": "mysql",  //（数据终端类型）
	"url": "192.168.10.1:3306", //（数据库地址）
	"data": ["aaaaaaaaaaaaaa", "1231wqweqwqe"], //（所填数据，与表中字段顺序对应）
	"baseName": "test",   //（数据库库名）
	"tableName": "clicks",   //（数据库表名）
	"passWord": "123456",   //（数据库密码）
	"userName": "root"   //（数据库用户）
}
```