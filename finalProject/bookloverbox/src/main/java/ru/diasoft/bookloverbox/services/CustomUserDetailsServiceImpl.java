package ru.diasoft.bookloverbox.services;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.diasoft.bookloverbox.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Используем findByEmailWithRoles для загрузки ролей через JOIN FETCH
        // Это предотвращает LazyInitializationException при LAZY fetch ролей
        return userRepository.findByEmailWithRoles(email)
            .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + email));
    }
}
