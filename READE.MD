# message

公共字段 createAt: 创建时间, id: [uuid] message_id

## 消息内容类型


### 文本消息 

contentType 1

```json
{
  ...,
  "contentType": 1,
  "body": {
    "content": ""
  }
}
```

### 图片消息

contentType 2

```json
{
  ...,
  "contentType": 2,
  "body": {
    "pic": "",
    "describe": {
      "width": "",
      "height": ""
    }
  },
  ...,
}
```

### 语音消息

contentType 3

```json
{
  ...,
  "contentType": 2,
  "body": {
    "voice": "",
    "describe": {
      "amount": 1000
    }
  }
}
```

### 外链消息

### markdown 消息

## 消息类型

### 单聊

messageType 1

```json
{
  "messageType": 1,
  ...,
  "from": {
    "userId": "",
    "avatar": "",
    "nickName": ""
  }
}
```

### 群聊

messageType 2

```json
{
  "messageType": 2,
  ...,
  "from": {
    "groupId": "",
    "groupName": "",
    "groupLogo": "",
    "userId": "",
    "avatar": "",
    "nickName": ""
  },
  "at": {
    "atMobiles": [],
    "atUserIds": [],
    "isAtAll": false
  }
}
```

### 系统消息

messageType 3

### COMMAND消息

messageType 4

### ACK消息


## 消息存储

message 表

| -字段- | -描述- | -类型- |
|  ----  | ----  | ----  |
|-id-|-message id(uuid)-|-string-|
|--|--|--|
|-im_account-|-im账号-|-string-|
|-owner-|-所属用户im_account-|-string-|
|-op-|-send/arrived-|-string-|
|-status-|-1(成功),0(失败)-|-int-|
|-topic_id-|-topic id-|-string-|
|-client_id-|-client id-|-string-|


topic 表

| -字段- | -描述- | -类型- |
|  ----  | ----  | ----  |
|-id-|-topic id(uuid)-|-string-|
|-topic-|-topic name-|-string-|
|-below_type-|-user/group/sys/command...-|-string-|
|-below_id-|-below_type对应id-|-int-|
|-owner-|-所属用户im_account-|-string-|

imUser 表

| -字段- | -描述- | -类型- |
|  ----  | ----  | ----  |
|-im_account-|-im账号-|-string-|
|--|--|--|
|-type-|-1(好有)，0(陌生人)...-|-int-|
|-owner-|-所属用户im_account-|-string-|

group 表

| -字段- | -描述- | -类型- |
|  ----  | ----  | ----  |
|--|--|--|
|-type-|-1(小组)，2(群组)...-|-int-|
|-owner-|-所属用户im_account-|-string-|

