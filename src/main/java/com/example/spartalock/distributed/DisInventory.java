package com.example.spartalock.distributed;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class DisInventory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "int default 0")
    private int count;

    public void incrementCount() {
        this.count++;
    }
}
