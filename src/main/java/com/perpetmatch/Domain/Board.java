package com.perpetmatch.Domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.perpetmatch.api.dto.Board.BoardPostRequest;
import com.perpetmatch.api.dto.Board.BoardUpdateRequest;
import com.perpetmatch.modules.Board.Gender;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Board extends DateAudit{

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User manager;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    private String title;

    private int credit;

    @OneToOne(fetch = FetchType.LAZY)
    private Zone zone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private int year;
    private int month;

    @OneToOne(fetch = FetchType.LAZY)
    private PetAge petAge;

    // 품종
    @OneToOne(fetch = FetchType.LAZY)
    private Pet petTitle;


    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String checkUpImage;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String lineAgeImage;

    private boolean hasCheckUp;


    private boolean hasLineAge;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String neuteredImage;

    private boolean hasNeutered;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime publishedDateTime;

    private boolean closed;


    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String description;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String boardImage1;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String boardImage2;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String boardImage3;


    public void addManager(User member) {
        this.manager = member;
    }

    public boolean isMember(String username) {
        for(User user : users) {
            if(user.getNickname().equals(username))
                return true;
        }
        return false;
    }

    public boolean isManager(String username) {
        if(manager.getNickname().equals(username))
            return true;
        else return false;
    }
    public void addMember(User user) {
        this.getUsers().add(user);
    }
    public void removeMember(User user) {
        this.getUsers().remove(user);
    }

    public void updateBoard(BoardUpdateRequest boardRequest, Zone city, Pet byTitle, PetAge Age) {
        credit = boardRequest.getCredit();
        petTitle = byTitle;
        zone = city;
        petAge = Age;
        gender = boardRequest.getGender();
        year= boardRequest.getYear();
        month = boardRequest.getMonth();
        checkUpImage = boardRequest.getCheckUp();
        lineAgeImage = boardRequest.getLineAgeImage();
        neuteredImage = boardRequest.getNeuteredImage();
        description = boardRequest.getDescription();
        boardImage1 = boardRequest.getBoardImage1();
        boardImage2 = boardRequest.getBoardImage2();
        boardImage3 = boardRequest.getBoardImage3();
    }

    public boolean findAppliedUsers(String username) {
        return users.stream().anyMatch(u -> u.getNickname().equals(username));
    }
}
