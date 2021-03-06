package com.perpetmatch.api.dto.Board;

import com.perpetmatch.AdoptBoard.domain.Gender;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@EqualsAndHashCode(of = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardUpdateRequest {


    @Min(value = 30000)
    @Max(value = 150000)
    private int credit;

    private String zone;
    private Gender gender;
    private int year;

    @Min(value = 1)
    @Max(value = 12)
    private int month;
    private String petTitle;
    private String checkUp;
    private String lineAgeImage;
    private String neuteredImage;
    private String description;
    private String boardImage1;
    private String boardImage2;
    private String boardImage3;
}
