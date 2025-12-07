package com.example.finance_tracker.models;

import com.example.finance_tracker.common.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private Long id;
    private String name;
    private Type type;
    private String description;
    private Long userId;
}
