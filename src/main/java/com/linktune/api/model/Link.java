package com.linktune.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data // @Getter, @Setter, @ToString, @EqualsAndHashCode ve daha fazlasını sağlar.
@NoArgsConstructor // Boş constructor'ı oluşturur.
@AllArgsConstructor
@Entity
@Table(name="Links")
public class Link {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 2048)
    private String longUrl;

    @Column(nullable = false,unique = true)
    private String shortCode;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
