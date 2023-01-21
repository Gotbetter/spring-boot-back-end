package pcrc.gotbetter.setting.security.JWT.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.setting.http_api.GetBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String auth_id) throws UsernameNotFoundException {

        return userRepository.findByAuthId(auth_id).orElseThrow(() ->
                new GetBetterException(MessageType.UsernameOrPasswordNotFound));
    }
}
