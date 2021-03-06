package com.perpetmatch.AdoptBoard.ui;

import com.perpetmatch.AdoptBoard.domain.Board;
import com.perpetmatch.User.domain.User;
import com.perpetmatch.api.dto.Board.*;
import com.perpetmatch.api.dto.Profile.UserDtoWithCredit;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import com.perpetmatch.jjwt.resource.ApiResponse;
import com.perpetmatch.jjwt.resource.ApiResponseDto;
import com.perpetmatch.AdoptBoard.domain.BoardRepository;
import com.perpetmatch.AdoptBoard.application.BoardService;
import com.perpetmatch.User.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BoardApiController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final UserService userService;


    @GetMapping("/boards")
    public ResponseEntity getAdoptBoardLists() {
        Slice<Board> AdoptBoardEntityLists = boardService.findAllBoards();
        Slice<BoardPageData> AdoptBoardDtoListsData = AdoptBoardEntityLists.map(BoardPageData::new);
        return ResponseEntity.ok().body(ApiResponseDto.createOK(AdoptBoardDtoListsData));
    }


    @GetMapping("/boards/{id}")
    public ResponseEntity getOneAdoptBoardList(@PathVariable Long id) {
        if (!boardRepository.existsById(id)) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponseDto.Not_Found);

        Board board = boardService.findOneBoard(id);
        AdoptBoardV2 OneBoardListData = new AdoptBoardV2(board);

        return ResponseEntity.ok().body(ApiResponseDto.createOK(OneBoardListData));
    }

    @PostMapping("/boards")
    public ResponseEntity createBoard(@CurrentMember UserPrincipal currentMember, @RequestBody @Valid BoardPostRequest boardRequest
    , Errors errors) {
        if(currentMember == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDto.DEFAULT_UNAUTHORIZED);
        if(errors.hasErrors()) return ResponseEntity.badRequest().body(ApiResponseDto.badRequest());

        Board board = boardService.createNewBoard(currentMember.getId(), boardRequest);
        BoardTitleDto createdBoardIdAndTitleData = BoardTitleDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .build();

        return ResponseEntity.ok().body(ApiResponseDto.createOK(createdBoardIdAndTitleData));
    }


    @PutMapping("/boards/{id}")
    public ResponseEntity updateBoard(@CurrentMember UserPrincipal currentMember, @PathVariable Long id,
                                      @RequestBody @Valid BoardUpdateRequest boardRequest, Errors errors) {
        if(errors.hasErrors()) return ResponseEntity.badRequest().body(ApiResponseDto.badRequest());

        boardService.updateBoard(currentMember.getId(), id, boardRequest);

        Board board = boardService.findOneBoard(id);
        BoardTitleDto createdBoardIdAndTitleData = BoardTitleDto.builder().id(board.getId()).title(board.getTitle()).build();

        return ResponseEntity.ok().body(ApiResponseDto.createOK(createdBoardIdAndTitleData));
    }


    /**
     * 신청된 회원들은 글을 등록한 유저만(isManager) 표시가 되야 하며
     * 입양하기 게시글 클릭시 보여지도록 하여야 한다.
     * 이떄 보여주어야 할 것은 일단 image와 닉네임
     */
    @GetMapping("/boards/{id}/manager")
    public ResponseEntity isManager(@CurrentMember UserPrincipal currentMember, @PathVariable Long id) {
        if (currentMember == null || !userService.findManager(currentMember.getUsername(), id)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDto.DEFAULT_UNAUTHORIZED);

        List<ApplyUsers> applyUsers = userService.applyUserList(id);
        BoardAppliedUsers boardUserDtoData = new BoardAppliedUsers(applyUsers);
        return ResponseEntity.ok().body(ApiResponseDto.createOK(boardUserDtoData));

    }

    /**
     * 수락 버튼을 누를 시 결제가 되야 한다.
     * 여기서 id는 board의 id
     */
    @PostMapping("/boards/{id}/accept")
    public ResponseEntity accept(@CurrentMember UserPrincipal currentMember, @PathVariable Long id,
                                 @RequestBody NameDto name) {
        if(currentMember == null || !userService.findManager(currentMember.getUsername(), id)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDto.DEFAULT_UNAUTHORIZED);

        User user = userService.acceptUser(id, name);
        UserDtoWithCredit userDtoWithCreditData = new UserDtoWithCredit(user);

        return ResponseEntity.ok().body(ApiResponseDto.createOK(userDtoWithCreditData));
    }


    /**
     * 신청하기를 누를 시
     * 없다면 신청이 되고 신청이 된 상태에서 다시 한번 누르면 신청이 취소된다.
     */
    @PostMapping("/boards/{id}/apply")
    public ResponseEntity apply(@CurrentMember UserPrincipal currentMember, @PathVariable Long id) {
        if(currentMember == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDto.DEFAULT_UNAUTHORIZED);

        String username = currentMember.getUsername();
        boolean userIn = userService.hasAppliedUser(id, username);

        AppliedBoardDto appliedBoardData = new AppliedBoardDto(userIn);
        return ResponseEntity.ok().body(ApiResponseDto.createOK(appliedBoardData));
    }

    /**
     * 관심글 등록을 누를 시
     */
    @PostMapping("/boards/{id}/likes")
    public ResponseEntity likes(@CurrentMember UserPrincipal currentMember, @PathVariable Long id) {
        if(currentMember == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDto.DEFAULT_UNAUTHORIZED);

        String username = currentMember.getUsername();
        boolean likeApply = userService.hasBoardLikes(id, username);

        BoardLikeDto boardlikeData = new BoardLikeDto(likeApply);
        return ResponseEntity.ok().body(ApiResponseDto.createOK(boardlikeData));
    }


    /**
     * 신청하기가 되었는지 조회
     * applied_me
     */
    @GetMapping("/boards/{id}/applied_me")
    public ResponseEntity isAppliedUser(@CurrentMember UserPrincipal currentMember, @PathVariable Long id) {
        if(currentMember == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDto.DEFAULT_UNAUTHORIZED);

        boolean applied = boardService.isBoardApplied(id, currentMember.getUsername());
        return ResponseEntity.ok().body(new ApiResponse(applied, "신청 여부 입니다."));
    }



}
