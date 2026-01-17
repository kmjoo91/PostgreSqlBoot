package me.kmj.vitedemo.postgresqldemo.service;

import me.kmj.vitedemo.postgresqldemo.dto.UserRequest;
import me.kmj.vitedemo.postgresqldemo.dto.UserResponse;
import me.kmj.vitedemo.postgresqldemo.entity.User;
import me.kmj.vitedemo.postgresqldemo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserRequest userRequest) {
        logger.info("새 사용자 생성 요청: email={}", userRequest.getEmail());
        
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            logger.warn("이메일 중복: {}", userRequest.getEmail());
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + userRequest.getEmail());
        }

        User user = new User(userRequest.getEmail(), userRequest.getName());
        User savedUser = userRepository.save(user);
        logger.info("사용자 생성 완료: id={}, email={}", savedUser.getId(), savedUser.getEmail());
        
        return UserResponse.from(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        logger.info("모든 사용자 조회 요청");
        List<User> users = userRepository.findAll();
        logger.info("조회된 사용자 수: {}", users.size());
        return users.stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        logger.info("사용자 조회 요청: id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("사용자를 찾을 수 없음: id={}", id);
                    return new IllegalArgumentException("사용자를 찾을 수 없습니다: id=" + id);
                });
        return UserResponse.from(user);
    }

    public UserResponse updateUser(Long id, UserRequest userRequest) {
        logger.info("사용자 업데이트 요청: id={}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("사용자를 찾을 수 없음: id={}", id);
                    return new IllegalArgumentException("사용자를 찾을 수 없습니다: id=" + id);
                });

        // 이메일 변경 시 중복 체크
        if (!user.getEmail().equals(userRequest.getEmail()) && 
            userRepository.existsByEmail(userRequest.getEmail())) {
            logger.warn("이메일 중복: {}", userRequest.getEmail());
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + userRequest.getEmail());
        }

        user.setEmail(userRequest.getEmail());
        user.setName(userRequest.getName());
        // @Transactional의 dirty checking으로 자동 업데이트됨
        
        logger.info("사용자 업데이트 완료: id={}", user.getId());
        
        return UserResponse.from(user);
    }

    public void deleteUser(Long id) {
        logger.info("사용자 삭제 요청: id={}", id);
        
        if (!userRepository.existsById(id)) {
            logger.warn("사용자를 찾을 수 없음: id={}", id);
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: id=" + id);
        }
        
        userRepository.deleteById(id);
        logger.info("사용자 삭제 완료: id={}", id);
    }
}
