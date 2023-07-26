package pcrc.gotbetter.setting.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();

        if (authentication == null || authentication.getName() == null
            || authentication.getPrincipal().equals("anonymousUser")) {
            throw new GotBetterException(MessageType.ReLogin);
        }

        UserDetails principal = (UserDetails)authentication.getPrincipal();
        return Long.parseLong(principal.getUsername());
    }
}