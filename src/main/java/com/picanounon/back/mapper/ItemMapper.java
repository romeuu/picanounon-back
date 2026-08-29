package com.picanounon.back.mapper;

import com.picanounon.back.dto.ItemDTO;
import com.picanounon.back.dto.response.ItemResponse;
import com.picanounon.back.model.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public ItemDTO toDTO(Item item) {
        if (item == null) {
            return null;
        }
        return new ItemDTO(item.getId(), item.getName(), item.getDescription());
    }

    public Item toEntity(ItemDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Item(dto.getId(), dto.getName(), dto.getDescription());
    }

    public ItemResponse toResponse(ItemDTO dto) {
        if (dto == null) {
            return null;
        }
        return new ItemResponse(dto.getId(), dto.getName(), dto.getDescription());
    }

    public ItemResponse toResponse(Item item) {
        if (item == null) {
            return null;
        }
        return new ItemResponse(item.getId(), item.getName(), item.getDescription());
    }
}
