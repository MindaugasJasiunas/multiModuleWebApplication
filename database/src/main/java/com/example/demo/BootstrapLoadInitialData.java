package com.example.demo;

import com.example.demo.dao.*;
import com.example.demo.dao.authentication.AuthorityRepository;
import com.example.demo.dao.authentication.RoleRepository;
import com.example.demo.dao.authentication.UserEntityRepository;
import com.example.demo.entity.*;
import com.example.demo.entity.authentication.Authority;
import com.example.demo.entity.authentication.Role;
import com.example.demo.entity.authentication.UserEntity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Component
public class BootstrapLoadInitialData implements InitializingBean {
    @Autowired
    UserEntityRepository userEntityRepo;
    @Autowired
    GenderRepository genderRepo;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    StoreItemRepository storeItemRepository;
    @Autowired
    AuthorityRepository authorityRepo;
    @Autowired
    RoleRepository roleRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    CartRepository cartRepo;

    @Override
    public void afterPropertiesSet() throws Exception {
        loadAuthorities();

        loadFakeUsers();

        loadGenders();

        loadCategories();

        loadStores();

        loadItems();

        loadStoreItemWithQuantities();
    }

    private void loadAuthorities(){
        Authority adminAuthority=new Authority(); //kad .antMatchers("/admin").hasRole("ADMIN") veiktu
        adminAuthority.setPermission("ROLE_ADMIN");
        adminAuthority=authorityRepo.save(adminAuthority);

        Authority customerAuthority=new Authority();
        customerAuthority.setPermission("ROLE_CUSTOMER");
        customerAuthority=authorityRepo.save(customerAuthority);

        Authority employeeAuthority=new Authority();
        employeeAuthority.setPermission("ROLE_EMPLOYEE");
        employeeAuthority=authorityRepo.save(employeeAuthority);

//        Authority canAccessUsersPageAuthority=new Authority();
//        canAccessUsersPageAuthority.setPermission("canAccessUsersPage");
//        canAccessUsersPageAuthority=authorityRepo.save(canAccessUsersPageAuthority);

        Role adminRole=new Role();
        adminRole.setRoleName("ADMIN");
        adminRole.setAuthorities(new HashSet<>(Set.of(/*canAccessUsersPageAuthority,*/ adminAuthority)));
        adminRole=roleRepo.save(adminRole);

        Role customerRole=new Role();
        customerRole.setRoleName("CUSTOMER");
        customerRole.setAuthorities(new HashSet<>(Set.of(customerAuthority)));
        customerRole=roleRepo.save(customerRole);

        Role employeeRole=new Role();
        employeeRole.setRoleName("EMPLOYEE");
        employeeRole.setAuthorities(new HashSet<>(Set.of(employeeAuthority)));
        employeeRole=roleRepo.save(employeeRole);

    }


    private void loadFakeUsers(){
        //names who doesn't exist in real life

        Role adminRole= roleRepo.findRoleByRoleName("ADMIN").orElseThrow(()-> new RuntimeException("Role ADMIN doesn't exist!"));
        Role customerRole= roleRepo.findRoleByRoleName("CUSTOMER").orElseThrow(()-> new RuntimeException("Role CUSTOMER doesn't exist!"));
        Role employeeRole= roleRepo.findRoleByRoleName("EMPLOYEE").orElseThrow(()-> new RuntimeException("Role EMPLOYEE doesn't exist!"));

        UserEntity userEntity1=new UserEntity();
        userEntity1.setEmail("john.doe@example.com");
        userEntity1.setFirstName("John");
        userEntity1.setLastName("Doe");
        userEntity1.setEncryptedPassword(passwordEncoder.encode("password"));
        userEntity1.setRoles(new HashSet<>(Set.of(adminRole)));
        userEntity1.setEnabled(true);
        userEntityRepo.save(userEntity1);

        UserEntity userEntity2=new UserEntity();
        userEntity2.setEmail("jane.doe@example.com");
        userEntity2.setFirstName("Jane");
        userEntity2.setLastName("Doe");
        userEntity2.setEncryptedPassword(passwordEncoder.encode("passpass"));
        userEntity2.setRoles(new HashSet<>(Set.of(customerRole)));
        userEntity2.setEnabled(true);
        userEntityRepo.save(userEntity2);
        Cart cart=new Cart();
        cart.setUser(userEntity2);
        cartRepo.save(cart);

        UserEntity userEntity3=new UserEntity();
        userEntity3.setEmail("fabian.khalid@example.com");
        userEntity3.setFirstName("Fabian");
        userEntity3.setLastName("Khalid");
        userEntity3.setEncryptedPassword(passwordEncoder.encode("qwertyqwerty"));
        userEntity3.setRoles(new HashSet<>(Set.of(employeeRole)));
        userEntity3.setEnabled(true);
        userEntityRepo.save(userEntity3);


        UserEntity userEntity4=new UserEntity();
        userEntity4.setEmail("thomas.claiton@example.com");
        userEntity4.setFirstName("Thomas");
        userEntity4.setLastName("Claiton");
        userEntity4.setEncryptedPassword(passwordEncoder.encode("password123"));
        userEntity4.setRoles(new HashSet<>(Set.of(customerRole)));
        userEntity4.setEnabled(true);
        userEntityRepo.save(userEntity4);
        Cart cart2=new Cart();
        cart2.setUser(userEntity4);
        cartRepo.save(cart2);

        UserEntity userEntity5=new UserEntity();
        userEntity5.setEmail("josiah.wesley@example.com");
        userEntity5.setFirstName("Josiah");
        userEntity5.setLastName("Wesley");
        userEntity5.setEncryptedPassword(passwordEncoder.encode("passpass"));
        userEntity5.setRoles(new HashSet<>(Set.of(customerRole)));
        userEntity5.setEnabled(true);
        userEntityRepo.save(userEntity5);
        Cart cart3=new Cart();
        cart3.setUser(userEntity5);
        cartRepo.save(cart3);
    }

    private void loadGenders(){
        Gender male= new Gender();
        male.setGender("male");
        genderRepo.save(male);

        Gender female= new Gender();
        female.setGender("female");
        genderRepo.save(female);

        Gender unisex= new Gender();
        unisex.setGender("unisex");
        genderRepo.save(unisex);
    }

    private void loadCategories() {
        Category tshirts=new Category();
        tshirts.setCategoryName("t-shirts");
        categoryRepository.save(tshirts);
        Category hoodies=new Category();
        hoodies.setCategoryName("Hoodies");
        categoryRepository.save(hoodies);
        Category accessories=new Category();
        accessories.setCategoryName("Accessories");
        categoryRepository.save(accessories);
        Category pants=new Category();
        pants.setCategoryName("Pants");
        categoryRepository.save(pants);
        Category shorts=new Category();
        shorts.setCategoryName("Shorts");
        categoryRepository.save(shorts);
        Category other=new Category();
        other.setCategoryName("Other");
        categoryRepository.save(other);
        Category shoes=new Category();
        shoes.setCategoryName("Shoes");
        categoryRepository.save(shoes);
    }

    private void loadItems(){
        Category shoes=categoryRepository.findCategoryByCategoryName("Shoes").orElseThrow(()->new RuntimeException("Category Shoes does not exists in DB"));
        Category pants=categoryRepository.findCategoryByCategoryName("Pants").orElseThrow(()->new RuntimeException("Category Pants does not exists in DB"));
        Category shorts=categoryRepository.findCategoryByCategoryName("Shorts").orElseThrow(()->new RuntimeException("Category Shorts does not exists in DB"));
        Category accessories=categoryRepository.findCategoryByCategoryName("Accessories").orElseThrow(()->new RuntimeException("Category Accessories does not exists in DB"));
        Category tshirts=categoryRepository.findCategoryByCategoryName("t-shirts").orElseThrow(()->new RuntimeException("Category t-shirts does not exists in DB"));
        Category hoodies=categoryRepository.findCategoryByCategoryName("Hoodies").orElseThrow(()->new RuntimeException("Category Hoodies does not exists in DB"));
        Category other=categoryRepository.findCategoryByCategoryName("Other").orElseThrow(()->new RuntimeException("Category Other does not exists in DB"));

        Gender male= genderRepo.findGenderByGender("male").orElseThrow(()->new RuntimeException("Gender male does not exists in DB"));
        Gender female= genderRepo.findGenderByGender("female").orElseThrow(()->new RuntimeException("Gender female does not exists in DB"));
        Gender unisex= genderRepo.findGenderByGender("unisex").orElseThrow(()->new RuntimeException("Gender unisex does not exists in DB"));

        CurrencyUnit euro = Monetary.getCurrency("EUR");

        Item item=new Item();
        item.setTitle("Fancy soap");
        item.setDescription("Fancy soap suitable for all kinds of skin. Try it out!");
        item.setPrice(Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber( 123.45 ).create());
        item.addCategory(other);
        item.setGender(unisex);
        itemRepository.save(item);

        Item item2=new Item();
        item2.setTitle("Compostable t-shirt");
        item2.setDescription("Be green. Compostable t-shirt degrades in 2 weeks time underground.");
        item2.setPrice(Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber( 19.995 ).create());
        item2.addCategory(tshirts);
        item2.setGender(unisex);
        itemRepository.save(item2);

        Item item3=new Item();
        item3.setTitle("Skinny jeans");
        item3.setDescription("Skinny jeans for youth.");
        item3.setPrice(Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber( 29.7564 ).create());
        item3.addCategory(pants);
        item3.setGender(male);
        itemRepository.save(item3);

        Item item4=new Item();
        item4.setTitle("Captain America hoodie");
        item4.setDescription("Cozy hoodie for late nights out.");
        item4.setPrice(Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber( 24.99 ).create());
        item4.addCategory(hoodies);
        item4.setGender(male);
        itemRepository.save(item4);

        Item item5=new Item();
        item5.setTitle("Carrot pants");
        item5.setDescription("Carrot pants is the new form in game. Try it.");
        item5.setPrice(Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber( 37.89564 ).create());
        item5.addCategory(pants);
        item5.setGender(male);
        itemRepository.save(item5);

        Item item6=new Item();
        item6.setTitle("Generic black sunglasses");
        item6.setDescription("Be stylish with our new and improved design sunglasses.");
        item6.setPrice(Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber( 7.80000 ).create());
        item6.addCategory(accessories);
        item6.setGender(female);
        itemRepository.save(item6);

        Item item7=new Item();
        item7.setTitle("White generic sneakers");
        item7.setDescription("Nothing says fashion more than white elegant sneakers.");
        item7.setPrice(Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber( 29.99000 ).create());
        item7.addCategory(shoes);
        item7.setGender(female);
        itemRepository.save(item7);

        Item item8=new Item();
        item8.setTitle("Black generic sneakers");
        item8.setDescription("Street wear sneakers for your lifestyle.");
        item8.setPrice(Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber( 27 ).create());
        item8.addCategory(shoes);
        item8.setGender(female);
        itemRepository.save(item8);

        Item item9=new Item();
        item9.setTitle("Black Avengers hoodie");
        item9.setDescription("Hoodie for those Avengers fans");
        item9.setPrice(Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber( 35.0000 ).create());
        item9.addCategory(hoodies);
        item9.setGender(male);
        itemRepository.save(item9);

        Item item10=new Item();
        item10.setTitle("Jogger pants");
        item10.setDescription("Sporty and comfy jogger style pants.");
        item10.setPrice(Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber( 19.99 ).create());
        item10.addCategory(pants);
        item10.setGender(male);
        itemRepository.save(item10);

        Item item11=new Item();
        item11.setTitle("Pink generic hoodie");
        item11.setDescription("Think different");
        item11.setPrice(Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber( 29.705 ).create());
        item11.addCategory(hoodies);
        item11.setGender(unisex);
        itemRepository.save(item11);

        Item item12=new Item();
        item12.setTitle("Black jeans shorts");
        item12.setDescription("Practical black shorts for hot summer days");
        item12.setPrice(Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber( 29.007 ).create());
        item12.addCategory(pants);
        item12.addCategory(shorts);
        item12.setGender(female);
        itemRepository.save(item12);

        Item item13=new Item();
        item13.setTitle("Black bermuda shorts");
        item13.setDescription("Stylish black bermuda shorts");
        item13.setPrice(Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber( 34 ).create());
        item13.addCategory(pants);
        item13.setGender(male);
        itemRepository.save(item13);

    }

    private void loadStores(){
        Store warehouse= new Store();
        warehouse.setStoreTitle("Warehouse");
        storeRepository.save(warehouse);
    }

    private void loadStoreItemWithQuantities(){
        Store warehouse= storeRepository.getStoreByStoreTitle("Warehouse").orElseThrow(()-> new RuntimeException("Store 'Warehouse' not exists."));

        for(int i=1; i<=13; i++){
            int finalI = i;
            Item item= itemRepository.findById((long) finalI).orElseThrow(() -> new RuntimeException("Item with id "+ finalI +" not exists."));
            StoreItem storeItem=new StoreItem();
            storeItem.setItem(item);
            storeItem.setStore(warehouse);
            storeItem.setQuantity(new Random().nextInt(20));
            storeItemRepository.save(storeItem);
        }

    }

}
