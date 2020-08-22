package com.perpetmatch.Board;

import com.perpetmatch.Domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,Long> {


   // @Query("select distinct b from Board b left join fetch b.petAge left join fetch b.zone left join fetch b.petTitle where b.id = :id")
    @EntityGraph(attributePaths = {"petAge","zone","petTitle", "manager"})
    Board findZoneAndPetTitleAndPetAgeById(@Param("id") Long id);


    Board findByTitle(String s);

    @EntityGraph(attributePaths = {"petAge","zone","petTitle"})
    Page<Board> findAll(Pageable pageable);

}
