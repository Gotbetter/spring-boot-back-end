package pcrc.gotbetter.user.login_method.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.entity.UserSet;
import pcrc.gotbetter.user.data_access.repository.UserRepository;
import pcrc.gotbetter.user.data_access.repository.UserSetRepository;
import pcrc.gotbetter.user.login_method.oauth.UserPrincipal;

@Service
public class CustomUserDetailService implements UserDetailsService {

	private final UserRepository userRepository;
	private final UserSetRepository userSetRepository;

	@Autowired
	public CustomUserDetailService(UserRepository userRepository, UserSetRepository userSetRepository) {
		this.userRepository = userRepository;
		this.userSetRepository = userSetRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

		User user = userRepository.findByUserId(Long.valueOf(userId)).orElseThrow(() -> {
			throw new UsernameNotFoundException("Not existed user.");
		});
		UserSet userSet = userSetRepository.findByUserId(Long.valueOf(userId));
		if (userSet == null) {
			throw new UsernameNotFoundException("Not existed user.");
		}
		return UserPrincipal.create(user, userSet);
	}
}
