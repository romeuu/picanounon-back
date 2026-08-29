package com.picanounon.back.controller;

import com.picanounon.back.dto.ItemDTO;
import com.picanounon.back.dto.response.ApiResponse;
import com.picanounon.back.dto.response.ItemResponse;
import com.picanounon.back.mapper.ItemMapper;
import com.picanounon.back.model.Item;
import com.picanounon.back.repository.ItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public ItemController(ItemRepository itemRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemResponse>>> getAllItems() {
        List<ItemResponse> items = itemRepository.findAll().stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ItemResponse>> createItem(@RequestBody ItemDTO itemDTO) {
        Item item = itemMapper.toEntity(itemDTO);
        Item saved = itemRepository.save(item);
        ItemResponse response = itemMapper.toResponse(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Item created successfully", response));
    }
}
