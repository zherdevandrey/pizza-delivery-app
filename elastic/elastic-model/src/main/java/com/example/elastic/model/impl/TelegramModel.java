package com.example.elastic.model.impl;

import com.example.elastic.model.IndexModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TelegramModel implements IndexModel {

    private String id;

    private Long userId;

    private String text;

    private LocalDateTime createdAt;

}
