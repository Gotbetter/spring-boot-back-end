package pcrc.gotbetter.setting.security.JWT.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.user.data_access.domain.User;
import pcrc.gotbetter.user.data_access.repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String auth_id) throws UsernameNotFoundException {

        return userRepository.findByAuthId(auth_id)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("Not existed user."));
    }

    private UserDetails createUserDetails(User user) {
        return User.builder()
                .id(user.getId())
                .authId(user.getAuthId())
                .email(user.getEmail())
                .profile(user.getProfile())
                .build();
    }
}
