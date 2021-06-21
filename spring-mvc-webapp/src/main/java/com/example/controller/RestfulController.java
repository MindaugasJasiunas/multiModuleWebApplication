package com.example.controller;

import com.example.demo.dao.ItemRepository;
import com.example.demo.entity.Item;
import com.example.demo.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequestMapping("/api/v1/")
@RestController
public class RestfulController {
    private ItemService itemService;

    public RestfulController(ItemService itemService) {
        this.itemService = itemService;
    }

    @RequestMapping("items")
    public List<Item> list(@RequestParam(value = "page", defaultValue = "0") int page/*, @RequestParam(value = "size", defaultValue = "2") int size*/, @RequestParam(value = "sort", defaultValue = "id") String sortBy){
        if(page>0){
            page=page-1; // JPA pages starts from 0 (pagination in webpage starts from 1)
        }
        Pageable pageAndSizeAndSorted = PageRequest.of(page, 8, Sort.by(sortBy));
        Page<Item> p=itemService.findAll(pageAndSizeAndSorted);
       return p.getContent();
    }

}
