package com.perpetmatch.Item.application;

import com.perpetmatch.Item.domain.Item;
import com.perpetmatch.api.dto.Order.ItemDto;
import com.perpetmatch.api.dto.Order.ItemDtoOne;
import com.perpetmatch.exception.ResourceNotFoundException;
import com.perpetmatch.Item.domain.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemDtoOne getItemById(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));
        return new ItemDtoOne(item);
    }

    public Set<ItemDto> findFeeds() {
        List<Item> feeds = itemRepository.findAllByCompany("벨리스");
        return feeds.stream().map(ItemDto::new).collect(Collectors.toSet());
    }

    public Set<ItemDto> findSnacks() {
        List<Item> items = itemRepository.findAllByCompany("마이비펫");
        return  items.stream().map(ItemDto::new).collect(Collectors.toSet());
    }

    public Set<ItemDto> findGoods() {
        List<Item> items = itemRepository.findAllByCompany("까르페띠앙");
        return items.stream().map(ItemDto::new).collect(Collectors.toSet());
    }
}
