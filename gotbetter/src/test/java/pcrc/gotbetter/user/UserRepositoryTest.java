package pcrc.gotbetter.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.user.data_access.repository.UserRepositoryQueryDSLImpl;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    UserRepositoryQueryDSLImpl userRepositoryQueryDSLImpl;

    @Test
    @Transactional // 테스트 끝나고 바로 롤백해버림
//    @Rollback(value = false)
    public void testUser() throws Exception {
        //given
//        User user = new User();
//        user.setAuth_id("geulyeogeulyeo2");
//        user.setPassword("aa");
//        user.setUsername("짱구");
//        user.setEmail("jjang-gu2@gmail.com");
//        user.setProfile("??");

        //when
//        Integer saveId = userRepositoryQueryDSLImpl.save(user);
//        User findUser = userRepositoryQueryDSLImpl.findOne(saveId);

        //then
//        Assertions.assertThat(findUser.getId()).isEqualTo(user.getId());
//        Assertions.assertThat(findUser.getAuth_id()).isEqualTo(user.getAuth_id());

    }
}