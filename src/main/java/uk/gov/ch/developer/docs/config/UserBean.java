package uk.gov.ch.developer.docs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ch.developer.docs.models.user.IUserModel;
import uk.gov.ch.developer.docs.models.user.UserModel;

@Configuration
public class UserBean {

    @Bean
    public IUserModel userModel() {
        return new UserModel();
    }
}
