package com.leave.controller;

import com.leave.common.PageResult;
import com.leave.common.Result;
import com.leave.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> getMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(messageService.getMyMessages(page, size));
    }

    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount() {
        return Result.success(messageService.getUnreadCount());
    }

    @PostMapping("/{id}/read")
    public Result<?> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return Result.success();
    }

    @PostMapping("/read-all")
    public Result<?> markAllAsRead() {
        messageService.markAllAsRead();
        return Result.success();
    }
}
