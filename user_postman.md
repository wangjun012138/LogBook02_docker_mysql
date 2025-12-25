

# 不可篡改文本存证平台 - Postman 测试指南

## 🛠️ 前置准备

* **服务器地址**: `http://localhost:8080`
* **Content-Type**: 大部分 POST 请求的 Body 类型请选择 `raw` -> `JSON`。
* **Cookie 处理**: Postman 默认会自动保存服务器返回的 `JSESSIONID` Cookie。**注意**：在切换用户（如从用户A切换到用户B）时，请务必调用“登出接口”或手动在 Postman 的 Cookies 管理器中清除 Cookie，否则会串号。

---

## 🟢 第一阶段：用户 A 操作流程

### 1. 发送验证码 (User A)

* **功能**: 向邮箱发送 6 位验证码。
* **Method**: `POST`
* **URL**: `http://localhost:8080/api/auth/code`
* **Params** (Query Params):
* `email`: `user_a@example.com`


* **说明**: 发送后请查看后端控制台（Console）输出，找到类似 `>>> 邮件发送至 [user_a@example.com] 验证码: 123456` 的日志。

### 2. 注册用户 (User A)

* **功能**: 完成账号注册。
* **Method**: `POST`
* **URL**: `http://localhost:8080/api/auth/register`
* **Body** (JSON):
```json
{
    "email": "user_a@example.com",
    "username": "user_a",
    "password": "password123",
    "code": "123456"  // 填写控制台看到的验证码
}

```



### 3. 登录 (User A)

* **功能**: 登录并获取 Session。
* **Method**: `POST`
* **URL**: `http://localhost:8080/api/auth/login`
* **Body** (JSON):
```json
{
    "account": "user_a", // 用户名或邮箱均可
    "password": "password123"
}

```


* **响应**: 成功后 Postman 会自动保存 `JSESSIONID`。

### 4. 存储文本 (User A)

* **功能**: 上传一段文本进行存证。
* **Method**: `POST`
* **URL**: `http://localhost:8080/api/proof/upload`
* **Body** (JSON):
```json
{
    "subject": "A的重要合同",
    "content": "这是用户A所有的第一份重要合同内容，不可篡改。"
}

```


* **响应**: 记录下返回数据中的 `"id": 1`（假设 ID 为 1），稍后分享要用。

### 5. 查看我的存证列表 (User A)

* **功能**: 确认刚才上传成功。
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/proof/list`

📝 Postman 测试流程 (新功能)
1. 分享给用户 (带时效)
URL: POST /api/share/to-user

Body:

JSON

{
    "proofId": 1,
    "targetUsername": "user_b",
    "validMinutes": 5  // 5分钟有效，传 null 为永久
}
2. 生成分享链接 (带时效)
URL: POST /api/share/create-link

Body:

JSON

{
    "proofId": 1,
    "validMinutes": 60 // 1小时有效
}
Response: data 字段会返回一个 Token，例如 abc12345...

3. 访问链接 (User B 或 游客)
URL: GET /api/share/view-link?token=abc12345...

预期:

时间如在 60 分钟内：返回存证内容。

时间超过 60 分钟：返回 "链接已过期"。

4. 撤销/后悔了 (User A)
URL: POST /api/share/revoke/link?token=abc12345...

预期: 再次访问上面的 view-link 接口，会提示 "链接已失效（被撤销）"。

### 7. 登出 (User A)

* **功能**: 清除 Session，准备切换用户。
* **Method**: `POST`
* **URL**: `http://localhost:8080/api/auth/logout`
* **说明**: 调用成功后 Session 失效。

---

## 🔵 第二阶段：用户 B 操作流程

### 1. 注册与登录 (User B)

* **注册**: 参考用户 A 的步骤 1 & 2，将邮箱改为 `user_b@example.com`，用户名改为 `user_b`。
* **登录**: 参考用户 A 的步骤 3，使用 `user_b` 登录。

### 2. 查看分享列表 (User B)

* **功能**: 查看谁给我分享了文件。
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/share/list`
* **预期响应**:
```json
{
    "code": 200,
    "message": "获取分享列表成功",
    "data": [
        {
            "proof": {
                "id": 1,
                "subject": "A的重要合同",
                "content": "这是用户A所有的第一份重要合同内容，不可篡改。",
                "username": "user_a",
                ...
            },
            "sharedBy": "user_a",
            "sharedAt": "2023-XX-XX..."
        }
    ]
}

```



### 3. 查看分享详情 (User B)

* **功能**: 根据 ID 查看具体内容详情。
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/share/1`  (这里的 `1` 是存证 ID)

---

## 📝 接口速查表

| 功能 | 方法 | URL | Body 参数 (JSON) | 备注 |
| --- | --- | --- | --- | --- |
| **验证码** | POST | `/api/auth/code?email=xxx` | 无 | Email 在 Query 参数 |
| **注册** | POST | `/api/auth/register` | `email`, `username`, `password`, `code` |  |
| **登录** | POST | `/api/auth/login` | `account`, `password` | **自动设置 Cookie** |
| **登出** | POST | `/api/auth/logout` | 无 | 清除 Cookie |
| **存证上传** | POST | `/api/proof/upload` | `subject`, `content` | 需登录 |
| **存证列表** | GET | `/api/proof/list` | 无 | 查看自己的 |
| **存证详情** | GET | `/api/proof/{id}` | 无 | 查看自己的 |
| **分享给他人** | POST | `/api/share/to-user` | `proofId`, `targetUsername` | 需登录 |
| **被分享列表** | GET | `/api/share/list` | 无 | 需登录 |
| **被分享详情** | GET | `/api/share/{id}` | 无 | 需登录 |
