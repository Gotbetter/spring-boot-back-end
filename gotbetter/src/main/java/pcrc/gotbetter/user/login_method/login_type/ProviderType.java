package pcrc.gotbetter.user.login_method.login_type;

import lombok.Getter;

@Getter
public enum ProviderType {
    GOOGLE,
    KAKAO;

    public static boolean contains(String providerType) {
        try {
            ProviderType.valueOf(providerType);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
