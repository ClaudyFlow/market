# 前后端状态码统一说明

## 📋 概述

为确保前后端通信的一致性，市场平台客服系统采用**统一的数字状态码系统**（1000-9999），前后端使用相同的状态码定义。

## 🔄 状态码流转

### 消息发送流程

```
用户发送消息
    ↓
前端：1000 (SENDING)
    ↓ (WebSocket/HTTP 发送)
后端：2000 (SENT) ← ChatService.sendMessage() 设置
    ↓ (消息送达)
后端：3000 (DELIVERED)
    ↓ (用户已读)
后端：4000 (READ)
```

### 状态码变更历史

| 时间 | 变更内容 |
|------|---------|
| 2026-03-31 | 前后端统一使用 1000-9999 状态码 |
| 2026-03-31 | 后端 ChatMessage 添加 status 字段 |
| 2026-03-31 | 后端 ChatMessageResponse 添加 status 字段 |

## 📊 数据库字段

### chat_message 表

```sql
CREATE TABLE chat_message (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content VARCHAR(2000) NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'TEXT',  -- TEXT, IMAGE, SYSTEM, FILE
    status INTEGER NOT NULL DEFAULT 1000,      -- 消息状态码：1000-9999
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

## 🔢 前后端状态码对照

### 基础消息状态

| 状态码 | 前端常量 | 后端常量 | 说明 |
|--------|---------|---------|------|
| 1000 | `MessageStatus.SENDING` | `1000` | 发送中 |
| 2000 | `MessageStatus.SENT` | `2000` | 已发送 |
| 3000 | `MessageStatus.DELIVERED` | `3000` | 已送达 |
| 4000 | `MessageStatus.READ` | `4000` | 已读 |
| 5000 | `MessageStatus.FAILED` | `5000` | 发送失败 |

### 业务消息状态

| 分类 | 状态码范围 | 说明 |
|------|-----------|------|
| 订单消息 | 6000-6499 | 订单创建、发货、完成等 |
| 支付消息 | 6500-6799 | 支付成功/失败、退款等 |
| 物流消息 | 6800-6999 | 揽件、运输、派送、签收等 |
| 售后消息 | 7000-7999 | 退货、换货、维修、投诉等 |
| 促销消息 | 8000-8299 | 优惠券、促销活动等 |
| VIP 消息 | 8300-8599 | 会员状态、积分等 |
| 系统消息 | 8600-8999 | 系统通知、账户安全等 |

## 💻 前端使用

### TypeScript 定义

```typescript
// frontend/src/common/stores/messageStatus.ts
export const MessageStatus = {
  SENDING: 1000,
  SENT: 2000,
  DELIVERED: 3000,
  READ: 4000,
  FAILED: 5000
} as const

export type MessageStatusCode = typeof MessageStatus[keyof typeof MessageStatus]

export interface ChatMessage {
  id?: number
  senderId: number
  receiverId: number
  content: string
  type: 'TEXT' | 'IMAGE' | 'SYSTEM' | 'FILE'
  status: MessageStatusCode  // 使用统一状态码
  isRead: boolean
  createdAt?: string
}
```

### 前端 API 调用

```typescript
// 发送消息
const message = chatStore.addLocalMessage('您好', 'TEXT')
// message.status = 1000 (SENDING)

// WebSocket 发送后更新状态
chatStore.updateMessageStatus(message.localId, {
  status: MessageStatus.SENT  // 2000
})

// 接收后端消息
chatWS.onMessage((msg) => {
  // msg.status = 2000/3000/4000 (后端返回)
  chatStore.addReceivedMessage(msg)
})
```

## ☕ 后端使用

### Java 实体

```java
// backend/src/main/java/com/market/entity/ChatMessage.java
@Entity
@Table(name = "chat_message")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long senderId;
    private Long receiverId;
    private String content;
    
    @Enumerated(EnumType.STRING)
    private MessageType type = MessageType.TEXT;
    
    @Column(nullable = false)
    private Integer status = 1000;  // 消息状态码：1000-9999
    
    private Boolean isRead = false;
    
    // Getters and Setters
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
```

### 后端服务

```java
// backend/src/main/java/com/market/service/ChatService.java
@Service
public class ChatService {
    
    @Transactional
    public ChatMessageResponse sendMessage(Long senderId, ChatMessageRequest request) {
        ChatMessage message = new ChatMessage(
            senderId,
            request.getReceiverId(),
            request.getContent(),
            MessageType.valueOf(request.getType())
        );
        message.setStatus(2000); // 已发送 - 使用统一状态码
        message = chatMessageRepository.save(message);
        
        // 推送消息
        messagingTemplate.convertAndSendToUser(
            String.valueOf(request.getReceiverId()),
            "/queue/messages",
            convertToResponse(message)
        );
        
        return convertToResponse(message);
    }
    
    private ChatMessageResponse convertToResponse(ChatMessage message) {
        return new ChatMessageResponse(
            message.getId(),
            message.getSenderId(),
            message.getReceiverId(),
            message.getContent(),
            message.getType().name(),
            message.getStatus(),  // 返回统一状态码
            message.getIsRead(),
            message.getCreatedAt()
        );
    }
}
```

### 后端 DTO

```java
// backend/src/main/java/com/market/dto/ChatMessageResponse.java
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private String type;
    private Integer status;  // 消息状态码：1000-9999
    private Boolean isRead;
    private LocalDateTime createdAt;
    
    // Getters and Setters
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
```

## 🔄 状态更新流程

### 1. 用户发送消息

```typescript
// 前端
const localMessage = chatStore.addLocalMessage('您好')
// localMessage.status = 1000 (SENDING)

// 通过 WebSocket 发送
chatWS.sendChatMessage(receiverId, '您好', 'TEXT')
```

```java
// 后端接收
@PostMapping("/api/chat/send")
public ChatMessageResponse sendMessage(@RequestBody ChatMessageRequest request) {
    ChatMessage message = chatService.sendMessage(currentUserId, request);
    // message.status = 2000 (SENT)
    return message;
}
```

### 2. 消息送达

```java
// 后端推送
messagingTemplate.convertAndSendToUser(
    String.valueOf(receiverId),
    "/queue/messages",
    message  // message.status = 3000 (DELIVERED)
);
```

```typescript
// 前端接收
chatWS.onMessage((msg) => {
  // msg.status = 3000 (DELIVERED)
  chatStore.updateMessageStatus(msg.localId, {
    status: MessageStatus.DELIVERED
  })
})
```

### 3. 用户已读

```typescript
// 前端调用已读接口
await markAsRead(senderId)
```

```java
// 后端更新
@PostMapping("/api/chat/mark-read/{senderId}")
public void markAsRead(@PathVariable Long senderId) {
    chatService.markAsRead(currentUserId, senderId);
    // 更新消息状态为 4000 (READ)
}
```

## 📝 注意事项

### 1. 状态码一致性

- ✅ 前后端使用**相同的数字**表示相同状态
- ✅ 前端 TypeScript 定义与后端 Java 常量保持一致
- ✅ 数据库存储整数，不存储字符串

### 2. 状态流转规则

```
1000 (发送中) → 2000 (已发送) → 3000 (已送达) → 4000 (已读)
                     ↓
               5000 (失败)
```

- 状态只能**向前流转**，不能回退
- 失败状态 (5000) 可以重发到 1000

### 3. 业务消息状态

业务消息（订单、支付、物流等）使用 6000-8999 范围：

```typescript
// 订单创建通知
const orderMessage = {
  content: '订单已创建',
  status: 6000,  // ORDER_CREATED
  type: 'SYSTEM'
}

// 后端接收后直接存储
chatMessageRepository.save(orderMessage)
// status = 6000 保持不变
```

### 4. 数据迁移

如果数据库已有数据，需要执行迁移：

```sql
-- 添加 status 字段（如果不存在）
ALTER TABLE chat_message 
ADD COLUMN IF NOT EXISTS status INTEGER DEFAULT 1000;

-- 更新旧数据
UPDATE chat_message 
SET status = 2000 
WHERE status IS NULL;
```

## 🔧 开发指南

### 添加新状态码

1. **前端**：在 `messageStatus.ts` 添加常量
2. **后端**：在 Java 代码中使用相同数字
3. **数据库**：无需修改（使用 1000-9999 范围）
4. **文档**：更新 MESSAGE_STATUS.md

```typescript
// 前端
export const MessageStatus = {
  // ... 现有状态
  CUSTOM_NEW: 9000  // 使用 9000-9999 范围
}
```

```java
// 后端
message.setStatus(9000);  // 使用相同数字
```

### 调试技巧

#### 前端调试

```typescript
// 在浏览器控制台查看消息状态
console.log('Message status:', message.status)
console.log('Status description:', MessageStatusUtils.getDescription(message.status))
```

#### 后端调试

```java
// 在日志中查看状态码
log.info("Message status: {}", message.getStatus());
log.info("Message status description: {}", 
    MessageStatusUtils.getDescription(message.getStatus()));
```

## 📚 相关文档

- [MESSAGE_STATUS.md](./MESSAGE_STATUS.md) - 消息状态码完整定义
- [CUSTOMER_SERVICE.md](./CUSTOMER_SERVICE.md) - 客服功能使用指南
- [前端接口文档.typ](./前端接口文档.typ) - 前端组件接口规范

---

**文档版本**: v1.0.0  
**最后更新**: 2026 年 3 月 31 日
