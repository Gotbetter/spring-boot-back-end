package pcrc.gotbetter.setting.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.entity.User;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new GotBetterException(MessageType.ReLogin);
        }
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}