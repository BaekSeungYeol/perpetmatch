package com.perpetmatch.api;

import com.perpetmatch.Domain.Item.Item;
import com.perpetmatch.Domain.Order;
import com.perpetmatch.modules.Item.ItemRepository;
import com.perpetmatch.modules.Member.UserService;
import com.perpetmatch.modules.Order.OrderService;
import com.perpetmatch.api.dto.Order.*;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import com.perpetmatch.jjwt.resource.ApiResponse;
import com.perpetmatch.jjwt.resource.ApiResponseWithData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Transactional
public class OrderApiController {

    private final ItemRepository itemRepository;
    private final OrderService orderService;
    private final UserService userService;


    @GetMapping("/shop/details/{id}")
    public ResponseEntity getFeedOne(@PathVariable Long id) {

        Optional<Item> item = itemRepository.findById(id);
        if(item.isEmpty())
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);

        Item curItem = item.get();
        ItemDtoOne itemDto = new ItemDtoOne(curItem);
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "아이템 단건 조회 입니다.",itemDto));
    }


    @GetMapping("/shop/feeds")
    public ResponseEntity getFeedList() {

        List<Item> items = itemRepository.findAllByCompany("벨리스");
        Set<ItemDto> collect = items.stream().map(ItemDto::new).collect(Collectors.toSet());

        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "사료 리스트 다건 조회 입니다.",collect));
    }

    @GetMapping("/shop/snacks")
    public ResponseEntity getSnackList() {

        List<Item> items = itemRepository.findAllByCompany("마이비펫");
        Set<ItemDto> collect = items.stream().map(ItemDto::new).collect(Collectors.toSet());
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "간식 리스트 다건 조회 입니다.",collect));
    }


    @GetMapping("/shop/goods")
    public ResponseEntity getGoodsList() {

        List<Item> items = itemRepository.findAllByCompany("까르페띠앙");
        Set<ItemDto> collect = items.stream().map(ItemDto::new).collect(Collectors.toSet());
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "용품 리스트 다건 조회 입니다.",collect));
    }

    @GetMapping("/shop/items/best")
    public ResponseEntity getItemsBest() {
        PageRequest pg = PageRequest.of(0, 6, Sort.by(Sort.Direction.ASC, "publishedDateTime"));
        Slice<Item> items = itemRepository.findAll(pg);
        Slice<ItemDto> collect = items.map(item -> new ItemDto(item));
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "아이템 베스트 다건 조회 입니다.",collect));
    }


    @GetMapping("/shop/items/new")
    public ResponseEntity getItemsNew() {
        PageRequest pg = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "publishedDateTime"));
        Slice<Item> items = itemRepository.findAll(pg);
        Slice<ItemDto> collect = items.map(item -> new ItemDto(item));
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "아이템 신규 다건 조회 입니다.",collect));
    }


    // TODO 장바구니 리스트 반환
    @GetMapping("/order/bags")
    public ResponseEntity getBags(@CurrentMember UserPrincipal currentMember) {
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        Set<BagDetailsDto> bags = userService.getBags(currentMember.getId());
        int totalSum = userService.getTotalSum(currentMember.getId());
        GetBagDto data = new GetBagDto(totalSum, bags);
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "장바구니 리스트입니다.",data));
    }


    /**
     * 장바구니 추가
     */
    @PostMapping("/order/bags/{id}")
    public ResponseEntity addBag(@CurrentMember UserPrincipal currentMember, @PathVariable Long id,
                                 @RequestBody BagDto bagDto){
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        userService.addBag(currentMember.getId(), id, bagDto.getCount());

        return ResponseEntity.ok().body(new ApiResponse(true, "장바구니에 아이템을 추가하였습니다."));
    }

    /**
     * 장바구니 삭제
     */
    @DeleteMapping("/order/bags/details/{id}")
    public ResponseEntity removeBag(@CurrentMember UserPrincipal currentMember, @PathVariable Long id){
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        userService.removeBag(currentMember.getId(), id);

        return ResponseEntity.ok().body(new ApiResponse(true, "장바구니에 아이템을 제거했습니다."));
    }

    /**
     * 최종 결제시
     */
    @PostMapping("/order/bags/pay")
    public ResponseEntity addBag(@CurrentMember UserPrincipal currentMember,
                                 @RequestBody AddressDto addressDto){
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        // TODO 주문
        Order order = orderService.makeAndPayOrders(currentMember.getId(), addressDto);
        PaymentOrderDto dto = new PaymentOrderDto(order);

        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "주문 완료 하였습니다.",dto));
    }


}
