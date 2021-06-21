package com.example.controller;

import com.example.demo.dao.ItemRepository;
import com.example.demo.dao.StoreItemRepository;
import com.example.demo.dao.StoreRepository;
import com.example.demo.entity.Item;
import com.example.demo.entity.Store;
import com.example.demo.entity.StoreItem;
import com.example.demo.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/item/")
@Controller
public class ItemController {
    private ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("{publicId}")
    public String getItem(@PathVariable("publicId") UUID publicId, Model model){
        Item item= itemService.findItemByPublicId(publicId).orElseThrow(() -> new RuntimeException("Item with public id "+publicId.toString()+" doesn't exist."));

        Map<Store, Integer> map = itemService.getMapWithStoresAndQuantitiesForItem(item);


        model.addAttribute("relatedItems", itemService.getItemsForRelatedProducts(4));

        model.addAttribute("storeWithQuantityMap", map);
        model.addAttribute("leftInWarehouse", itemService.ItemQuantityInWarehouse(item)); // if there is none - disable 'Add To Cart' button
        model.addAttribute("item", item);
        return "product-page";
    }


}
