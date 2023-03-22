package ro.itschool.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ro.itschool.controller.model.MyUserDTO;
import ro.itschool.controller.model.ProductMyUserDTO;
import ro.itschool.entity.MyUser;
import ro.itschool.entity.Product;
import ro.itschool.mapper.ProductMapper;
import ro.itschool.mapper.ProductMyUserMapper;
import ro.itschool.repository.MyUserRepository;
import ro.itschool.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/product")
public class ProductController {

    private final ProductRepository productRepository;

    private final MyUserRepository myUserRepository;

    private final ProductMyUserMapper productMyUserMapper;

    private final ProductMapper productMapper;

    @PostMapping(value = "/all")
    public ResponseEntity<?> getAllProducts(@RequestBody MyUserDTO myUserDTO) {
        Optional<MyUser> databaseUser = myUserRepository.findByUsername(myUserDTO.getUsername());
        if (databaseUser.isPresent()) {
            BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
            boolean passwordMatches = bcrypt.matches(myUserDTO.getPassword(), databaseUser.get().getPassword());
            if (passwordMatches)
                if (databaseUser.get().getRole().equals("user")) {
                    List<Product> allProducts = productRepository.findAllForUser();
                    List<ProductMyUserDTO> allProductsDTO = allProducts
                            .stream()
                            .map(productMapper::toDTO)
                            .toList();
                    return new ResponseEntity<>(allProductsDTO, HttpStatus.OK);
                } else {
                    List<Product> allProducts = productRepository.findAll();
                    List<ProductMyUserDTO> allProductsDTO = allProducts
                            .stream()
                            .map(productMapper::toDTO)
                            .toList();
                    return new ResponseEntity<>(allProductsDTO, HttpStatus.OK);
                }
            else
                return new ResponseEntity<>("Password is incorrect", HttpStatus.FORBIDDEN);
        } else
            return new ResponseEntity<>("Username not found", HttpStatus.FORBIDDEN);
    }

    @PostMapping(value = "/add")
    public ResponseEntity<?> saveProduct(@RequestBody ProductMyUserDTO productUserDTO) {
        Optional<MyUser> databaseUser = myUserRepository.findByUsername(productUserDTO.getUsername());
        if (databaseUser.isPresent()) {
            BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
            boolean passwordMatches = bcrypt.matches(productUserDTO.getPassword(), databaseUser.get().getPassword());
            if (passwordMatches)
                if (databaseUser.get().getRole().equals("admin")) {
                    return new ResponseEntity<>(
                            productRepository.save(productMyUserMapper.toProductEntity(productUserDTO)),
                            HttpStatus.OK
                    );
                } else
                    return new ResponseEntity<>("Only admin can insert", HttpStatus.FORBIDDEN);
            else
                return new ResponseEntity<>("Password is incorrect", HttpStatus.FORBIDDEN);
        } else
            return new ResponseEntity<>("Username not found", HttpStatus.FORBIDDEN);
    }
}
