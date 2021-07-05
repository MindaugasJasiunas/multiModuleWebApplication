package com.example.demo.service;

import com.example.demo.dao.CategoryRepository;
import com.example.demo.dao.GenderRepository;
import com.example.demo.dao.SizeRepository;
import com.example.demo.entity.Category;
import com.example.demo.entity.Gender;
import com.example.demo.entity.Size;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemSortingServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private GenderRepository genderRepository;
    @Mock
    private SizeRepository sizeRepository;
    @InjectMocks
    private ItemSortingServiceImpl itemSortingService;

    @Test
    void getGenders() {
        //given
        Gender gender=new Gender();
        Mockito.when(genderRepository.findAll()).thenReturn(Arrays.asList(gender));

        //when
        Iterable<Gender> genderList= itemSortingService.getGenders();

        //then
        assertNotNull(genderList);
        assertEquals(1, ((List<Gender>) genderList).size());
    }

    @Test
    void getCategories() {
        //given
        Category category=new Category();
        Mockito.when(categoryRepository.findAll()).thenReturn(Arrays.asList(category));

        //when
        Iterable<Category> categoryList= itemSortingService.getCategories();

        //then
        assertNotNull(categoryList);
        assertEquals(1, ((List<Category>) categoryList).size());
    }

    @Test
    void getSizes() {
        //given
        Size size=new Size();
        Mockito.when(sizeRepository.findAll()).thenReturn(Arrays.asList(size));

        //when
        Iterable<Size> sizeList= itemSortingService.getSizes();

        //then
        assertNotNull(sizeList);
        assertEquals(1, ((List<Size>) sizeList).size());
    }
}