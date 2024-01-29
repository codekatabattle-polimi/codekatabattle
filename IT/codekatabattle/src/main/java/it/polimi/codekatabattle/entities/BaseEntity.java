package it.polimi.codekatabattle.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

import static it.polimi.codekatabattle.config.APIConstants.DATETIME_FORMAT;

@Data
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @CreationTimestamp
    @JsonFormat(pattern=DATETIME_FORMAT)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern=DATETIME_FORMAT)
    private LocalDateTime updatedAt;

}
