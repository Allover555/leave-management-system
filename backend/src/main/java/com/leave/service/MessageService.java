package com.leave.service;

import com.leave.common.PageResult;
import com.leave.entity.SysMessage;
import com.leave.entity.SysUser;
import com.leave.repository.SysMessageRepository;
import com.leave.repository.SysUserRepository;
import com.leave.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final SysMessageRepository messageRepository;
    private final SysUserRepository userRepository;

    public void sendMessage(Long receiverId, String title, String content, Integer msgType, Long relatedId) {
        SysUser receiver = userRepository.findById(receiverId).orElse(null);
        if (receiver == null) return;

        SysMessage message = new SysMessage();
        message.setReceiver(receiver);
        message.setTitle(title);
        message.setContent(content);
        message.setMsgType(msgType);
        message.setRelatedId(relatedId);
        messageRepository.save(message);
    }

    public PageResult<Map<String, Object>> getMyMessages(int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<SysMessage> msgPage = messageRepository.findByReceiverId(userId, PageRequest.of(page - 1, size));
        List<Map<String, Object>> list = msgPage.getContent().stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("title", m.getTitle());
            map.put("content", m.getContent());
            map.put("msgType", m.getMsgType());
            map.put("relatedId", m.getRelatedId());
            map.put("isRead", m.getIsRead());
            map.put("createdAt", m.getCreatedAt());
            return map;
        }).collect(Collectors.toList());
        return PageResult.of(msgPage, list);
    }

    public Long getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        return messageRepository.countUnreadByReceiverId(userId);
    }

    @Transactional
    public void markAsRead(Long messageId) {
        messageRepository.findById(messageId).ifPresent(m -> {
            m.setIsRead(1);
            messageRepository.save(m);
        });
    }

    @Transactional
    public void markAllAsRead() {
        Long userId = SecurityUtils.getCurrentUserId();
        messageRepository.markAllAsRead(userId);
    }
}
