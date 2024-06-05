package com.phatpl.learnvocabulary.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "words")
public class Word {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Integer id;
    String word;
    String phonetic;
    String definition;
    String example1;
    String example2;
}
