package ro.itschool.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ro.itschool.controller.model.MyUserDTO;
import ro.itschool.mapper.MyUserMapper;
import ro.itschool.repository.MyUserRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/my-user")
public class MyUserController {

    private final MyUserRepository myUserRepository;

    private final MyUserMapper myUserMapper;


    @PostMapping(value = "/register")
    public void registerUser(@RequestBody MyUserDTO myUserDTO) {
        String encryptedPassword = new BCryptPasswordEncoder().encode(myUserDTO.getPassword());
        if (myUserDTO.getRole() == null) {
            myUserDTO.setRole("user");
        }
        myUserDTO.setPassword(encryptedPassword);
        myUserRepository.save(myUserMapper.toEntity(myUserDTO));
    }

    @GetMapping(value = "/all")
    public List<String> getAllUsers() {
        return myUserRepository.findAll()
                .stream()
                .map(myUserMapper::toDTO)
                .map(MyUserDTO::getUsername)
                .toList();
    }

}
